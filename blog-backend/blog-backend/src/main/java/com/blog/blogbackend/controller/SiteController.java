package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.service.SiteService;
import com.blog.blogbackend.vo.SiteConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/site")
    public ApiResponse<SiteConfigVO> getSite() {
        return ApiResponse.ok(siteService.getSiteConfig());
    }
}