package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/cover")
    public ApiResponse<String> uploadCover(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(imageStorageService.storeCover(file));
    }

    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(imageStorageService.storeAvatar(file));
    }

    @PostMapping("/logo")
    public ApiResponse<String> uploadLogo(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(imageStorageService.storeLogo(file));
    }
}
