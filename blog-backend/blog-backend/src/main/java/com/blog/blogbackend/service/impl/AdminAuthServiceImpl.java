package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.dto.AdminLoginRequest;
import com.blog.blogbackend.service.AdminAuthService;
import com.blog.blogbackend.vo.AdminSessionVO;
import com.blog.blogbackend.vo.AdminUserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    public static final String SESSION_KEY = "blog_admin_username";

    private static final int SESSION_SECONDS = 8 * 60 * 60;

    @Value("${blog.admin.username:admin}")
    private String adminUsername;

    @Value("${blog.admin.password}")
    private String adminPassword;

    @Override
    public AdminUserVO login(AdminLoginRequest request, HttpSession session) {
        boolean usernameMatches = constantEquals(request.getUsername(), adminUsername);
        boolean passwordMatches = constantEquals(request.getPassword(), adminPassword);
        if (!usernameMatches || !passwordMatches) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码不正确", HttpStatus.UNAUTHORIZED);
        }

        session.setAttribute(SESSION_KEY, adminUsername);
        session.setMaxInactiveInterval(SESSION_SECONDS);
        return new AdminUserVO(adminUsername);
    }

    @Override
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public AdminSessionVO getSession(HttpSession session) {
        Object username = session == null ? null : session.getAttribute(SESSION_KEY);
        if (username == null) {
            return new AdminSessionVO(false, null);
        }
        return new AdminSessionVO(true, String.valueOf(username));
    }

    @Override
    public boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(SESSION_KEY) != null;
    }

    private boolean constantEquals(String actual, String expected) {
        if (actual == null || expected == null) {
            return false;
        }
        return MessageDigest.isEqual(
                actual.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }
}
