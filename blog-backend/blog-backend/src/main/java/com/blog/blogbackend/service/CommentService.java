package com.blog.blogbackend.service;

import com.blog.blogbackend.dto.CommentCreateRequest;
import com.blog.blogbackend.vo.CommentVO;
import com.blog.blogbackend.vo.SubmissionVO;

import java.util.List;

public interface CommentService {

    List<CommentVO> getComments(String slug);

    SubmissionVO submitComment(String slug, CommentCreateRequest request, String ip);
}
