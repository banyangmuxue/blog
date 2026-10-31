package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.config.CacheConfig;
import com.blog.blogbackend.dto.PostQuery;
import com.blog.blogbackend.dto.PostRow;
import com.blog.blogbackend.dto.PostTagRow;
import com.blog.blogbackend.mapper.PostMapper;
import com.blog.blogbackend.mapper.TagMapper;
import com.blog.blogbackend.service.PostService;
import com.blog.blogbackend.vo.AdjacentPostsVO;
import com.blog.blogbackend.vo.ArchiveMonthVO;
import com.blog.blogbackend.vo.ArchiveVO;
import com.blog.blogbackend.vo.CategoryBriefVO;
import com.blog.blogbackend.vo.PostCoverVO;
import com.blog.blogbackend.vo.PostDetailVO;
import com.blog.blogbackend.vo.PostSummaryVO;
import com.blog.blogbackend.vo.TagBriefVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM");
    private static final Set<String> SUPPORTED_SORTS = Set.of("latest", "oldest", "views");

    private final PostMapper postMapper;
    private final TagMapper tagMapper;

    @Override
    @Cacheable(
            cacheNames = CacheConfig.PINNED_POSTS,
            key = "'top:' + #p0.size",
            condition = "#p0.pinned == true and #p0.page == 1"
                    + " and #p0.category == null and #p0.tag == null and #p0.q == null"
                    + " and (#p0.sort == null or #p0.sort == 'latest')")
    public PageResult<PostSummaryVO> getPosts(PostQuery query) {
        normalize(query);

        long total = postMapper.countPosts(query);
        int pages = Math.max(1, (int) Math.ceil(total / (double) query.getSize()));
        int current = Math.min(query.getPage(), pages);
        query.setPage(current);
        query.setOffset((current - 1) * query.getSize());

        List<PostRow> rows = postMapper.selectPostPage(query);
        if (rows.isEmpty()) {
            return PageResult.of(List.of(), total, current, query.getSize());
        }

        Map<Long, List<TagBriefVO>> tagsByPost = loadTags(rows);
        String keyword = query.getQ();
        List<PostSummaryVO> items = rows.stream()
                .map(row -> toSummary(row, tagsByPost.getOrDefault(row.getId(), List.of()), keyword))
                .toList();

        return PageResult.of(items, total, current, query.getSize());
    }

    @Override
    public List<PostSummaryVO> getLatestPosts(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        List<PostRow> rows = postMapper.selectLatestPosts(safeLimit);
        if (rows.isEmpty()) {
            return List.of();
        }
        Map<Long, List<TagBriefVO>> tagsByPost = loadTags(rows);
        return rows.stream()
                .map(row -> toSummary(row, tagsByPost.getOrDefault(row.getId(), List.of()), null))
                .toList();
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.POST_DETAIL, key = "#p0", unless = "#result == null")
    public PostDetailVO getPost(String slug) {
        PostRow row = postMapper.selectBySlug(slug);
        if (row == null) {
            return null;
        }
        List<TagBriefVO> tags = loadTags(List.of(row))
                .getOrDefault(row.getId(), List.of());
        PostDetailVO vo = new PostDetailVO();
        BeanUtils.copyProperties(toSummary(row, tags, null), vo);
        vo.setContent(row.getContent());
        return vo;
    }

    @Override
    public AdjacentPostsVO getAdjacentPosts(String slug) {
        PostRow current = postMapper.selectBySlug(slug);
        if (current == null || current.getPublishedAt() == null) {
            return new AdjacentPostsVO(null, null);
        }

        PostRow prev = postMapper.selectPrevPost(current.getPublishedAt(), current.getId());
        PostRow next = postMapper.selectNextPost(current.getPublishedAt(), current.getId());

        List<PostRow> rows = new ArrayList<>();
        if (prev != null) {
            rows.add(prev);
        }
        if (next != null) {
            rows.add(next);
        }
        Map<Long, List<TagBriefVO>> tagsByPost = rows.isEmpty() ? Map.of() : loadTags(rows);

        PostSummaryVO prevVO = prev == null ? null
                : toSummary(prev, tagsByPost.getOrDefault(prev.getId(), List.of()), null);
        PostSummaryVO nextVO = next == null ? null
                : toSummary(next, tagsByPost.getOrDefault(next.getId(), List.of()), null);
        return new AdjacentPostsVO(prevVO, nextVO);
    }

    @Override
    public List<ArchiveVO> getArchive() {
        List<PostRow> rows = postMapper.selectArchivePosts();
        if (rows.isEmpty()) {
            return List.of();
        }

        Map<Long, List<TagBriefVO>> tagsByPost = loadTags(rows);
        Map<String, Map<String, List<PostSummaryVO>>> grouped = new LinkedHashMap<>();
        for (PostRow row : rows) {
            String year = String.valueOf(row.getPublishedAt().getYear());
            String month = String.format("%02d", row.getPublishedAt().getMonthValue());
            grouped.computeIfAbsent(year, key -> new LinkedHashMap<>())
                    .computeIfAbsent(month, key -> new ArrayList<>())
                    .add(toSummary(row, tagsByPost.getOrDefault(row.getId(), List.of()), null));
        }

        List<ArchiveVO> result = new ArrayList<>();
        grouped.forEach((year, months) -> {
            int count = months.values().stream().mapToInt(List::size).sum();
            List<ArchiveMonthVO> monthVOs = months.entrySet().stream()
                    .map(entry -> new ArchiveMonthVO(
                            entry.getKey(),
                            Integer.parseInt(entry.getKey()) + " 月",
                            entry.getValue()))
                    .toList();
            result.add(new ArchiveVO(year, count, monthVOs));
        });
        return result;
    }

    private void normalize(PostQuery query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        } else if (query.getSize() > 50) {
            query.setSize(50);
        }

        query.setCategory(blankToNull(query.getCategory()));
        query.setTag(blankToNull(query.getTag()));

        String keyword = blankToNull(query.getQ());
        if (keyword != null && keyword.length() > 50) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "搜索关键词不能超过 50 个字符", HttpStatus.BAD_REQUEST);
        }
        query.setQ(keyword);

        String sort = query.getSort() == null
                ? "latest"
                : query.getSort().trim().toLowerCase(Locale.ROOT);
        query.setSort(SUPPORTED_SORTS.contains(sort) ? sort : "latest");
    }

    private Map<Long, List<TagBriefVO>> loadTags(List<PostRow> rows) {
        List<Long> postIds = rows.stream().map(PostRow::getId).toList();
        List<PostTagRow> tagRows = tagMapper.selectTagsByPostIds(postIds);
        return tagRows.stream().collect(Collectors.groupingBy(
                PostTagRow::getPostId,
                Collectors.mapping(row -> new TagBriefVO(row.getSlug(), row.getName()), Collectors.toList())
        ));
    }

    private PostSummaryVO toSummary(PostRow row, List<TagBriefVO> tags, String keyword) {
        PostSummaryVO vo = new PostSummaryVO();
        vo.setId(row.getId());
        vo.setSlug(row.getSlug());
        vo.setTitle(row.getTitle());
        vo.setExcerpt(row.getExcerpt());
        vo.setViews(row.getViews());
        vo.setPinned(Boolean.TRUE.equals(row.getPinned()));
        vo.setCover(new PostCoverVO(row.getCoverFrom(), row.getCoverTo(), row.getCoverImage()));
        if (row.getCategorySlug() != null) {
            vo.setCategory(new CategoryBriefVO(
                    row.getCategorySlug(), row.getCategoryName(), row.getCategoryDescription()));
        }
        vo.setTags(tags);
        vo.setReadingTime(readingTimeOf(row.getContent()));
        if (row.getPublishedAt() != null) {
            vo.setPublishedAt(row.getPublishedAt().format(DATE_FORMAT));
            vo.setCoverLabel(row.getPublishedAt().format(MONTH_FORMAT));
        }
        if (row.getUpdatedAt() != null) {
            vo.setUpdatedAt(row.getUpdatedAt().format(DATE_FORMAT));
        }
        if (keyword != null) {
            vo.setSnippet(snippetOf(row.getContent(), keyword));
        }
        return vo;
    }

    private int readingTimeOf(String content) {
        int chars = content.replaceAll("\\s", "").length();
        return Math.max(2, (int) Math.round(chars / 380.0));
    }

    private String snippetOf(String content, String keyword) {
        String plain = plainText(content);
        if (plain.isEmpty()) {
            return "";
        }
        String lowerPlain = plain.toLowerCase(Locale.ROOT);
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        int index = lowerPlain.indexOf(lowerKeyword);
        if (index < 0) {
            return plain.substring(0, Math.min(90, plain.length())) + "…";
        }
        int start = Math.max(0, index - 32);
        int end = Math.min(plain.length(), index + keyword.length() + 48);
        return (start > 0 ? "…" : "") + plain.substring(start, end) + "…";
    }

    private String plainText(String content) {
        return content
                .replaceAll("(?m)^#.*$", "")
                .replaceAll("(?s)```.*?```", "")
                .replaceAll("[>*_`\\-|]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
