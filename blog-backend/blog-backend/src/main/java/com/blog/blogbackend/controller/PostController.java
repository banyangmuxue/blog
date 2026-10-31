package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.common.IpUtils;
import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.CommentCreateRequest;
import com.blog.blogbackend.dto.PostQuery;
import com.blog.blogbackend.service.CommentService;
import com.blog.blogbackend.service.PostService;
import com.blog.blogbackend.service.PostViewService;
import com.blog.blogbackend.vo.AdjacentPostsVO;
import com.blog.blogbackend.vo.CommentVO;
import com.blog.blogbackend.vo.PostDetailVO;
import com.blog.blogbackend.vo.PostSummaryVO;
import com.blog.blogbackend.vo.SubmissionVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostViewService postViewService;
    private final CommentService commentService;

    @GetMapping
    public ApiResponse<PageResult<PostSummaryVO>> getPosts(PostQuery query) {
        return ApiResponse.ok(postService.getPosts(query));
    }

    @GetMapping("/latest")
    public ApiResponse<List<PostSummaryVO>> getLatestPosts(
            @RequestParam(defaultValue = "6") int limit) {
        return ApiResponse.ok(postService.getLatestPosts(limit));
    }

    @GetMapping("/{slug}")
    public ApiResponse<PostDetailVO> getPost(@PathVariable String slug) {
        return ApiResponse.ok(postService.getPost(slug));
    }

    @GetMapping("/{slug}/adjacent")
    public ApiResponse<AdjacentPostsVO> getAdjacentPosts(@PathVariable String slug) {
        return ApiResponse.ok(postService.getAdjacentPosts(slug));
    }

    @PostMapping("/{slug}/view")
    public ApiResponse<Boolean> increaseViews(
            @PathVariable String slug,
            HttpServletRequest request,
            HttpServletResponse response) {
        return ApiResponse.ok(postViewService.recordView(slug, request, response));
    }

    @GetMapping("/{slug}/comments")
    public ApiResponse<List<CommentVO>> getComments(@PathVariable String slug) {
        return ApiResponse.ok(commentService.getComments(slug));
    }

    @PostMapping("/{slug}/comments")
    public ApiResponse<SubmissionVO> submitComment(
            @PathVariable String slug,
            @RequestBody CommentCreateRequest commentRequest,
            HttpServletRequest httpRequest) {
        return ApiResponse.ok(commentService.submitComment(
                slug, commentRequest, IpUtils.getClientIp(httpRequest)));
    }
}
