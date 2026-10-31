package com.blog.blogbackend.controller;

import com.blog.blogbackend.common.ApiResponse;
import com.blog.blogbackend.dto.AdminLoginRequest;
import com.blog.blogbackend.service.AdminAuthService;
import com.blog.blogbackend.vo.AdminSessionVO;
import com.blog.blogbackend.vo.AdminUserVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ApiResponse<AdminUserVO> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpSession session) {
        return ApiResponse.ok(adminAuthService.login(request, session));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        adminAuthService.logout(session);
        return ApiResponse.ok();
    }

    @GetMapping("/session")
    public ApiResponse<AdminSessionVO> session(HttpSession session) {
        return ApiResponse.ok(adminAuthService.getSession(session));
    }
}
