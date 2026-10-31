package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.config.CacheConfig;
import com.blog.blogbackend.dto.AdminPostQuery;
import com.blog.blogbackend.dto.AdminPostRow;
import com.blog.blogbackend.dto.AdminPostSaveRequest;
import com.blog.blogbackend.dto.PostTagRow;
import com.blog.blogbackend.entity.Post;
import com.blog.blogbackend.mapper.AdminPostMapper;
import com.blog.blogbackend.mapper.CategoryMapper;
import com.blog.blogbackend.mapper.TagMapper;
import com.blog.blogbackend.service.AdminPostService;
import com.blog.blogbackend.vo.AdminPostDetailVO;
import com.blog.blogbackend.vo.AdminPostSummaryVO;
import com.blog.blogbackend.vo.TagBriefVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPostServiceImpl implements AdminPostService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Set<String> STATUSES = Set.of("draft", "published");
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");
    private static final String DEFAULT_COVER_FROM = "#123043";
    private static final String DEFAULT_COVER_TO = "#2a1b4d";

    private final AdminPostMapper adminPostMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final CacheManager cacheManager;

    @Override
    public PageResult<AdminPostSummaryVO> getPosts(AdminPostQuery query) {
        normalize(query);

        long total = adminPostMapper.countAdminPosts(query);
        int pages = Math.max(1, (int) Math.ceil(total / (double) query.getSize()));
        int current = Math.min(query.getPage(), pages);
        query.setPage(current);
        query.setOffset((current - 1) * query.getSize());

        List<AdminPostRow> rows = adminPostMapper.selectAdminPostPage(query);
        if (rows.isEmpty()) {
            return PageResult.of(List.of(), total, current, query.getSize());
        }

        Map<Long, List<TagBriefVO>> tagsByPost = loadTags(rows);
        List<AdminPostSummaryVO> items = rows.stream()
                .map(row -> toSummary(row, tagsByPost.getOrDefault(row.getId(), List.of())))
                .toList();
        return PageResult.of(items, total, current, query.getSize());
    }

    @Override
    public AdminPostDetailVO getPost(Long id) {
        AdminPostRow row = getExisting(id);
        return toDetail(row);
    }

    @Override
    @Transactional
    public AdminPostDetailVO createPost(AdminPostSaveRequest request) {
        Post post = buildPost(request, null);
        List<Long> tagIds = resolveTagIds(request.getTagSlugs());

        adminPostMapper.insertPost(post);
        replaceTags(post.getId(), tagIds);
        evictPostCaches(post.getSlug());
        return getPost(post.getId());
    }

    @Override
    @Transactional
    public AdminPostDetailVO updatePost(Long id, AdminPostSaveRequest request) {
        AdminPostRow existing = getExisting(id);
        Post post = buildPost(request, id);
        post.setId(id);

        LocalDateTime publishedAt = existing.getPublishedAt();
        if ("published".equals(post.getStatus()) && publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }
        post.setPublishedAt(publishedAt);

        List<Long> tagIds = resolveTagIds(request.getTagSlugs());
        adminPostMapper.updatePost(post);
        replaceTags(id, tagIds);
        evictPostCaches(existing.getSlug(), post.getSlug());
        return getPost(id);
    }

    @Override
    @Transactional
    public AdminPostDetailVO updateStatus(Long id, String status) {
        AdminPostRow existing = getExisting(id);
        String normalized = normalizeStatus(status);

        if ("published".equals(normalized) && isBlank(existing.getContent())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "正文为空，无法发布", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime publishedAt = existing.getPublishedAt();
        if ("published".equals(normalized) && publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }
        adminPostMapper.updatePostStatus(id, normalized, publishedAt);
        evictPostCaches(existing.getSlug());
        return getPost(id);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        AdminPostRow existing = getExisting(id);
        adminPostMapper.deletePost(id);
        evictPostCaches(existing.getSlug());
    }

    /**
     * 数据库事务提交后再清理缓存，避免其他请求读到未提交的数据。
     */
    private void evictPostCaches(String... slugs) {
        Runnable eviction = () -> {
            try {
                Cache pinnedCache = cacheManager.getCache(CacheConfig.PINNED_POSTS);
                if (pinnedCache != null) {
                    pinnedCache.clear();
                }
                Cache detailCache = cacheManager.getCache(CacheConfig.POST_DETAIL);
                if (detailCache != null) {
                    for (String slug : slugs) {
                        if (slug != null && !slug.isBlank()) {
                            detailCache.evict(slug);
                        }
                    }
                }
                Cache tagCache = cacheManager.getCache(CacheConfig.TAG_LIST);
                if (tagCache != null) {
                    tagCache.clear();
                }
                Cache categoryCache = cacheManager.getCache(CacheConfig.CATEGORY_LIST);
                if (categoryCache != null) {
                    categoryCache.clear();
                }
            } catch (RuntimeException exception) {
                log.warn("清理文章缓存失败: {}", exception.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eviction.run();
                }
            });
        } else {
            eviction.run();
        }
    }

    private Post buildPost(AdminPostSaveRequest request, Long excludeId) {
        String slug = request.getSlug() == null
                ? ""
                : request.getSlug().trim().toLowerCase(Locale.ROOT);
        if (slug.isEmpty() || !SLUG_PATTERN.matcher(slug).matches() || slug.length() > 100) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "Slug 只能包含小写字母、数字和中划线", HttpStatus.BAD_REQUEST);
        }
        if (adminPostMapper.countBySlug(slug, excludeId) > 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "这个 Slug 已经被其他文章使用", HttpStatus.BAD_REQUEST);
        }

        String title = request.getTitle() == null ? "" : request.getTitle().trim();
        if (title.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "标题不能为空", HttpStatus.BAD_REQUEST);
        }

        String status = normalizeStatus(request.getStatus());
        String content = request.getContent() == null ? "" : request.getContent();
        if ("published".equals(status) && content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "正文为空，无法发布", HttpStatus.BAD_REQUEST);
        }

        Post post = new Post();
        post.setSlug(slug);
        post.setTitle(title);
        post.setExcerpt(request.getExcerpt() == null ? "" : request.getExcerpt().trim());
        post.setContent(content);
        post.setCoverFrom(normalizeColor(request.getCoverFrom(), DEFAULT_COVER_FROM));
        post.setCoverTo(normalizeColor(request.getCoverTo(), DEFAULT_COVER_TO));
        post.setCoverImage(normalizeCoverImage(request.getCoverImage()));
        post.setCategoryId(resolveCategoryId(request.getCategorySlug()));
        post.setStatus(status);
        post.setPinned(Boolean.TRUE.equals(request.getPinned()));
        post.setPublishedAt("published".equals(status) ? LocalDateTime.now() : null);
        return post;
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "draft" : status.trim().toLowerCase(Locale.ROOT);
        if (!STATUSES.contains(normalized)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "文章状态不正确", HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

    private String normalizeColor(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String color = value.trim();
        if (!COLOR_PATTERN.matcher(color).matches()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "封面颜色格式不正确", HttpStatus.BAD_REQUEST);
        }
        return color;
    }

    private String normalizeCoverImage(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String url = value.trim();
        if (!url.startsWith("/api/files/") || url.length() > 255) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "封面图地址不合法", HttpStatus.BAD_REQUEST);
        }
        return url;
    }

    private Long resolveCategoryId(String categorySlug) {
        if (categorySlug == null || categorySlug.isBlank()) {
            return null;
        }
        Long categoryId = categoryMapper.selectIdBySlug(categorySlug.trim());
        if (categoryId == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "所选分类不存在", HttpStatus.BAD_REQUEST);
        }
        return categoryId;
    }

    private List<Long> resolveTagIds(List<String> tagSlugs) {
        if (tagSlugs == null || tagSlugs.isEmpty()) {
            return List.of();
        }
        List<String> slugs = tagSlugs.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(slug -> !slug.isEmpty())
                .distinct()
                .toList();
        if (slugs.isEmpty()) {
            return List.of();
        }

        List<Long> ids = tagMapper.selectIdsBySlugs(slugs);
        if (ids.size() < slugs.size()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "包含不存在的标签，请刷新后重试", HttpStatus.BAD_REQUEST);
        }
        return ids;
    }

    private void replaceTags(Long postId, List<Long> tagIds) {
        adminPostMapper.deletePostTags(postId);
        if (!tagIds.isEmpty()) {
            adminPostMapper.insertPostTags(postId, tagIds);
        }
    }

    private AdminPostRow getExisting(Long id) {
        AdminPostRow row = adminPostMapper.selectAdminPostById(id);
        if (row == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "文章不存在", HttpStatus.NOT_FOUND);
        }
        return row;
    }

    private AdminPostDetailVO toDetail(AdminPostRow row) {
        List<TagBriefVO> tags = loadTags(List.of(row))
                .getOrDefault(row.getId(), List.of());

        AdminPostDetailVO vo = new AdminPostDetailVO();
        fillSummary(vo, row, tags);
        vo.setContent(row.getContent());
        vo.setCoverFrom(row.getCoverFrom());
        vo.setCoverTo(row.getCoverTo());
        vo.setCoverImage(row.getCoverImage());
        vo.setTagSlugs(adminPostMapper.selectTagSlugsByPostId(row.getId()));
        return vo;
    }

    private AdminPostSummaryVO toSummary(AdminPostRow row, List<TagBriefVO> tags) {
        AdminPostSummaryVO vo = new AdminPostSummaryVO();
        fillSummary(vo, row, tags);
        return vo;
    }

    private void fillSummary(AdminPostSummaryVO vo, AdminPostRow row, List<TagBriefVO> tags) {
        vo.setId(row.getId());
        vo.setSlug(row.getSlug());
        vo.setTitle(row.getTitle());
        vo.setExcerpt(row.getExcerpt());
        vo.setStatus(row.getStatus());
        vo.setPinned(Boolean.TRUE.equals(row.getPinned()));
        vo.setViews(row.getViews());
        vo.setCategorySlug(row.getCategorySlug());
        vo.setCategoryName(row.getCategoryName());
        vo.setPublishedAt(formatTime(row.getPublishedAt()));
        vo.setUpdatedAt(formatTime(row.getUpdatedAt()));
        vo.setTags(tags);
    }

    private Map<Long, List<TagBriefVO>> loadTags(List<AdminPostRow> rows) {
        List<Long> postIds = rows.stream().map(AdminPostRow::getId).toList();
        List<PostTagRow> tagRows = tagMapper.selectTagsByPostIds(postIds);
        return tagRows.stream().collect(Collectors.groupingBy(
                PostTagRow::getPostId,
                Collectors.mapping(row -> new TagBriefVO(row.getSlug(), row.getName()), Collectors.toList())
        ));
    }

    private String formatTime(LocalDateTime value) {
        return value == null ? null : value.format(TIME_FORMAT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void normalize(AdminPostQuery query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        } else if (query.getSize() > 50) {
            query.setSize(50);
        }

        String status = query.getStatus() == null ? null : query.getStatus().trim();
        if (status != null && !status.isEmpty() && !STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "状态筛选不正确", HttpStatus.BAD_REQUEST);
        }
        query.setStatus(status == null || status.isEmpty() ? null : status);

        String keyword = query.getKeyword() == null ? null : query.getKeyword().trim();
        if (keyword != null && keyword.length() > 50) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "搜索关键词不能超过 50 个字符", HttpStatus.BAD_REQUEST);
        }
        query.setKeyword(keyword == null || keyword.isEmpty() ? null : keyword);
    }
}
