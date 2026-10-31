package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.service.TagService;
import com.blog.blogbackend.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ApiResponse<List<TagVO>> getTags() {
        return ApiResponse.ok(tagService.getTags());
    }
}
