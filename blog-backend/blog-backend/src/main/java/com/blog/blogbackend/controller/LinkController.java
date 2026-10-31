package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.service.LinkService;
import com.blog.blogbackend.vo.LinkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @GetMapping
    public ApiResponse<List<LinkVO>> getLinks() {
        return ApiResponse.ok(linkService.getLinks());
    }
}
