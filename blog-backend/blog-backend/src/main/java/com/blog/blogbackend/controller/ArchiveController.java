package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.service.PostService;
import com.blog.blogbackend.vo.ArchiveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<List<ArchiveVO>> getArchive() {
        return ApiResponse.ok(postService.getArchive());
    }
}
