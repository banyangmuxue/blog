package com.blog.blogbackend.service;

import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.vo.AdminCommentVO;

public interface AdminCommentService {

    PageResult<AdminCommentVO> getComments(Long postId, int page, int size);

    void deleteComment(Long id);
}
