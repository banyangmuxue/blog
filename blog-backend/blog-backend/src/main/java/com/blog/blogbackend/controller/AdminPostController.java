package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.AdminPostQuery;
import com.blog.blogbackend.dto.AdminPostSaveRequest;
import com.blog.blogbackend.dto.AdminStatusRequest;
import com.blog.blogbackend.service.AdminPostService;
import com.blog.blogbackend.vo.AdminPostDetailVO;
import com.blog.blogbackend.vo.AdminPostSummaryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final AdminPostService adminPostService;

    @GetMapping
    public ApiResponse<PageResult<AdminPostSummaryVO>> getPosts(AdminPostQuery query) {
        return ApiResponse.ok(adminPostService.getPosts(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminPostDetailVO> getPost(@PathVariable Long id) {
        return ApiResponse.ok(adminPostService.getPost(id));
    }

    @PostMapping
    public ApiResponse<AdminPostDetailVO> createPost(
            @Valid @RequestBody AdminPostSaveRequest request) {
        return ApiResponse.ok(adminPostService.createPost(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminPostDetailVO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody AdminPostSaveRequest request) {
        return ApiResponse.ok(adminPostService.updatePost(id, request));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<AdminPostDetailVO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminStatusRequest request) {
        return ApiResponse.ok(adminPostService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        adminPostService.deletePost(id);
        return ApiResponse.ok();
    }
}
