package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.dto.AdminSiteConfigSaveRequest;
import com.blog.blogbackend.service.SiteService;
import com.blog.blogbackend.vo.SiteConfigVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/site")
@RequiredArgsConstructor
public class AdminSiteController {

    private final SiteService siteService;

    @GetMapping
    public ApiResponse<SiteConfigVO> getSiteConfig() {
        return ApiResponse.ok(siteService.getSiteConfig());
    }

    @PutMapping
    public ApiResponse<SiteConfigVO> updateSiteConfig(
            @Valid @RequestBody AdminSiteConfigSaveRequest request) {
        return ApiResponse.ok(siteService.updateSiteConfig(request));
    }
}
