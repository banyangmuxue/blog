package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.service.AdminCommentService;
import com.blog.blogbackend.vo.AdminCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<PageResult<AdminCommentVO>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(adminCommentService.getComments(postId, page, size));
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        adminCommentService.deleteComment(id);
        return ApiResponse.ok();
    }
}
