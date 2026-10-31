package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.common.IpUtils;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.MessageCreateRequest;
import com.blog.blogbackend.service.MessageService;
import com.blog.blogbackend.vo.CommentVO;
import com.blog.blogbackend.vo.SubmissionVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ApiResponse<PageResult<CommentVO>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(messageService.getMessages(page, size));
    }

    @PostMapping
    public ApiResponse<SubmissionVO> submitMessage(
            @RequestBody MessageCreateRequest request,
            HttpServletRequest httpRequest) {
        return ApiResponse.ok(
                messageService.submitMessage(request, IpUtils.getClientIp(httpRequest)));
    }
}
