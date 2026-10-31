package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.AdminCommentRow;
import com.blog.blogbackend.mapper.AdminCommentMapper;
import com.blog.blogbackend.mapper.AdminPostMapper;
import com.blog.blogbackend.service.AdminCommentService;
import com.blog.blogbackend.vo.AdminCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private final AdminCommentMapper adminCommentMapper;
    private final AdminPostMapper adminPostMapper;

    @Override
    public PageResult<AdminCommentVO> getComments(Long postId, int page, int size) {
        if (adminPostMapper.selectAdminPostById(postId) == null) {
            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND, "文章不存在", HttpStatus.NOT_FOUND);
        }

        int normalizedSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        long total = adminCommentMapper.countByPostId(postId);
        int pages = Math.max(1, (int) Math.ceil(total / (double) normalizedSize));
        int current = Math.min(Math.max(1, page), pages);
        int offset = (current - 1) * normalizedSize;

        List<AdminCommentVO> items = adminCommentMapper
                .selectPageByPostId(postId, offset, normalizedSize).stream()
                .map(this::toVO)
                .toList();
        return PageResult.of(items, total, current, normalizedSize);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        if (adminCommentMapper.deleteById(id) == 0) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND, "评论不存在或已删除", HttpStatus.NOT_FOUND);
        }
    }

    private AdminCommentVO toVO(AdminCommentRow row) {
        AdminCommentVO vo = new AdminCommentVO();
        vo.setId(row.getId());
        vo.setNickname(row.getNickname());
        vo.setContent(row.getContent());
        vo.setStatus(row.getStatus());
        vo.setCreatedAt(row.getCreatedAt() == null ? "" : row.getCreatedAt().format(DATETIME_FORMAT));
        return vo;
    }
}
