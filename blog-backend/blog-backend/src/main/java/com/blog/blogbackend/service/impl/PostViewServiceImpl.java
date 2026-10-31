package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.dto.PostRow;
import com.blog.blogbackend.mapper.PostMapper;
import com.blog.blogbackend.mapper.PostViewMapper;
import com.blog.blogbackend.service.PostViewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostViewServiceImpl implements PostViewService {

    private static final String VISITOR_COOKIE = "blog_visitor";
    private static final Pattern VISITOR_KEY_PATTERN = Pattern.compile("[A-Za-z0-9-]{1,64}");
    private static final Duration DEDUP_WINDOW = Duration.ofMinutes(30);
    private static final int COOKIE_MAX_AGE = 365 * 24 * 60 * 60;

    private final PostMapper postMapper;
    private final PostViewMapper postViewMapper;

    @Override
    @Transactional
    public boolean recordView(String slug, HttpServletRequest request, HttpServletResponse response) {
        PostRow post = postMapper.selectBySlug(slug);
        if (post == null) {
            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND, "文章不存在或未发布", HttpStatus.NOT_FOUND);
        }

        String visitorKey = resolveVisitorKey(request, response);
        LocalDateTime now = LocalDateTime.now();
        boolean counted = postViewMapper.insertIgnore(post.getId(), visitorKey, now) > 0;
        if (!counted) {
            counted = postViewMapper.refresh(
                    post.getId(), visitorKey, now.minus(DEDUP_WINDOW), now) > 0;
        }
        if (counted) {
            postMapper.incrementViews(post.getId());
        }
        return counted;
    }

    private String resolveVisitorKey(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (VISITOR_COOKIE.equals(cookie.getName())
                        && cookie.getValue() != null
                        && VISITOR_KEY_PATTERN.matcher(cookie.getValue()).matches()) {
                    return cookie.getValue();
                }
            }
        }

        String visitorKey = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(VISITOR_COOKIE, visitorKey);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
        return visitorKey;
    }
}
