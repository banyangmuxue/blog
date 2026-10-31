package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BannedWordFilter;
import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.MessageCreateRequest;
import com.blog.blogbackend.dto.MessageRow;
import com.blog.blogbackend.entity.Message;
import com.blog.blogbackend.entity.SiteConfig;
import com.blog.blogbackend.mapper.MessageMapper;
import com.blog.blogbackend.mapper.SiteConfigMapper;
import com.blog.blogbackend.service.MessageService;
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
public class MessageServiceImpl implements MessageService {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[a-zA-Z!/][^>]*>");

    private final MessageMapper messageMapper;
    private final SiteConfigMapper siteConfigMapper;

    @Override
    public PageResult<CommentVO> getMessages(int page, int size) {
        int safeSize = size < 1 ? 10 : Math.min(size, 50);
        long total = messageMapper.countApproved();
        int pages = Math.max(1, (int) Math.ceil(total / (double) safeSize));
        int current = Math.min(Math.max(page, 1), pages);
        int offset = (current - 1) * safeSize;

        List<CommentVO> items = messageMapper.selectApprovedPage(offset, safeSize).stream()
                .map(this::toVO)
                .toList();
        return PageResult.of(items, total, current, safeSize);
    }

    @Override
    public SubmissionVO submitMessage(MessageCreateRequest request, String ip) {
        SiteConfig config = siteConfigMapper.selectSiteConfig();
        if (config != null && !Boolean.TRUE.equals(config.getFeaturesGuestbook())) {
            throw new BusinessException(
                    ErrorCode.GUESTBOOK_CLOSED, "留言功能已关闭", HttpStatus.FORBIDDEN);
        }

        String nickname = requireNickname(request.getNickname());
        String email = normalizeEmail(request.getEmail());
        String content = requireContent(request.getContent());
        if (BannedWordFilter.containsBannedWord(nickname, content)) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "内容包含不允许的词语，请修改后再提交", HttpStatus.BAD_REQUEST);
        }

        if (messageMapper.countRecentByIp(ip, LocalDateTime.now().minusSeconds(60)) > 0) {
            throw new BusinessException(
                    ErrorCode.TOO_MANY_REQUESTS, "提交太频繁，请 60 秒后再试", HttpStatus.TOO_MANY_REQUESTS);
        }

        String status = config != null && Boolean.TRUE.equals(config.getFeaturesReview())
                ? "pending" : "approved";
        Message message = new Message();
        message.setNickname(nickname);
        message.setEmail(email);
        message.setContent(content);
        message.setStatus(status);
        message.setIp(ip);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        return new SubmissionVO(status);
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

    private String requireContent(String value) {
        String content = value == null ? "" : value.trim();
        if (content.length() < 5 || content.length() > 300) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "内容需 5—300 字", HttpStatus.BAD_REQUEST);
        }
        if (HTML_TAG_PATTERN.matcher(content).find()) {
            throw new BusinessException(
                    ErrorCode.PARAM_INVALID, "内容不能包含 HTML 标签", HttpStatus.BAD_REQUEST);
        }
        return content;
    }

    private CommentVO toVO(MessageRow row) {
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
