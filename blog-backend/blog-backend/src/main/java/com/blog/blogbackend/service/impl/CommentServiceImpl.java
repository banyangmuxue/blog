package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BannedWordFilter;
import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.dto.CommentCreateRequest;
import com.blog.blogbackend.dto.CommentRow;
import com.blog.blogbackend.dto.PostRow;
import com.blog.blogbackend.entity.Comment;
import com.blog.blogbackend.entity.SiteConfig;
import com.blog.blogbackend.mapper.CommentMapper;
import com.blog.blogbackend.mapper.PostMapper;
import com.blog.blogbackend.mapper.SiteConfigMapper;
import com.blog.blogbackend.service.CommentService;
import com.blog.blogbackend.vo.CommentVO;
import com.blog.blogbackend.vo.ReplyVO;
import com.blog.blogbackend.vo.SubmissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[a-zA-Z!/][^>]*>");

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final SiteConfigMapper siteConfigMapper;

    @Override
    public List<CommentVO> getComments(String slug) {
        PostRow post = requirePublishedPost(slug);
        return commentMapper.selectApprovedByPostId(post.getId()).stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public SubmissionVO submitComment(String slug, CommentCreateRequest request, String ip) {
        SiteConfig config = siteConfigMapper.selectSiteConfig();
        if (config != null && !Boolean.TRUE.equals(config.getFeaturesComments())) {
            throw new BusinessException(
                    ErrorCode.COMMENTS_CLOSED, "评论功能已关闭", HttpStatus.FORBIDDEN);
        }

        PostRow post = requirePublishedPost(slug);
        String nickname = requireNickname(request.getNickname());
        String email = normalizeEmail(request.getEmail());
        String content = requireContent(request.getContent(), 5, 500);
        if (BannedWordFilter.containsBannedWord(nickname, content)) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "内容包含不允许的词语，请修改后再提交", HttpStatus.BAD_REQUEST);
        }

        if (commentMapper.countRecentByIpAndPost(
                post.getId(), ip, LocalDateTime.now().minusSeconds(60)) > 0) {
            throw new BusinessException(
                    ErrorCode.TOO_MANY_REQUESTS, "提交太频繁，请 60 秒后再试", HttpStatus.TOO_MANY_REQUESTS);
        }

        Comment comment = new Comment();
        comment.setPostId(post.getId());
        comment.setNickname(nickname);
        comment.setEmail(email);
        comment.setContent(content);
        comment.setStatus("approved");
        comment.setIp(ip);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        return new SubmissionVO("approved");
    }

    private PostRow requirePublishedPost(String slug) {
        PostRow post = postMapper.selectBySlug(slug);
        if (post == null) {
            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND, "文章不存在或未发布", HttpStatus.NOT_FOUND);
        }
        return post;
    }

    private String requireNickname(String value) {
        String nickname = value == null ? "" : value.trim();
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "昵称需 2—20 个字", HttpStatus.BAD_REQUEST);
        }
        return nickname;
    }

    private String normalizeEmail(String value) {
        String email = value == null ? "" : value.trim();
        if (email.isEmpty()) {
            return null;
        }
        if (email.length() > 100) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "邮箱长度不能超过 100 个字符", HttpStatus.BAD_REQUEST);
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "邮箱格式不正确", HttpStatus.BAD_REQUEST);
        }
        return email;
    }

    private String requireContent(String value, int min, int max) {
        String content = value == null ? "" : value.trim();
        if (content.length() < min || content.length() > max) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID,
                    "内容需 " + min + "—" + max + " 字",
                    HttpStatus.BAD_REQUEST);
        }
        if (HTML_TAG_PATTERN.matcher(content).find()) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "内容不能包含 HTML 标签", HttpStatus.BAD_REQUEST);
        }
        return content;
    }

    private CommentVO toVO(CommentRow row) {
        CommentVO vo = new CommentVO();
        vo.setId(row.getId());
        vo.setNickname(row.getNickname());
        vo.setContent(row.getContent());
        vo.setCreatedAt(row.getCreatedAt() == null ? "" : row.getCreatedAt().format(DATETIME_FORMAT));
        if (row.getReplyContent() != null && row.getReplyAt() != null) {
            vo.setReply(new ReplyVO(row.getReplyContent(), row.getReplyAt().format(DATETIME_FORMAT)));
        }
        return vo;
    }
}
