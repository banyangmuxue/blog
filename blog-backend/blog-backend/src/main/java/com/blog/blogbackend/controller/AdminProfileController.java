package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.dto.AdminProfileSaveRequest;
import com.blog.blogbackend.service.ProfileService;
import com.blog.blogbackend.vo.ProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<ProfileVO> getProfile() {
        return ApiResponse.ok(profileService.getProfile());
    }

    @PutMapping
    public ApiResponse<ProfileVO> updateProfile(
            @Valid @RequestBody AdminProfileSaveRequest request) {
        return ApiResponse.ok(profileService.updateProfile(request));
    }
}
