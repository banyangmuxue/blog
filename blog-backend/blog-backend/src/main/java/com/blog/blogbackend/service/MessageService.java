package com.blog.blogbackend.service;

import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.MessageCreateRequest;
import com.blog.blogbackend.vo.CommentVO;
import com.blog.blogbackend.vo.SubmissionVO;

public interface MessageService {

    PageResult<CommentVO> getMessages(int page, int size);

    SubmissionVO submitMessage(MessageCreateRequest request, String ip);
}
