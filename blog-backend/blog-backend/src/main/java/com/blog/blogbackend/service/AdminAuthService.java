package com.blog.blogbackend.service;

import com.blog.blogbackend.dto.AdminLoginRequest;
import com.blog.blogbackend.vo.AdminSessionVO;
import com.blog.blogbackend.vo.AdminUserVO;
import jakarta.servlet.http.HttpSession;

public interface AdminAuthService {

    AdminUserVO login(AdminLoginRequest request, HttpSession session);

    void logout(HttpSession session);

    AdminSessionVO getSession(HttpSession session);

    boolean isLoggedIn(HttpSession session);
}
