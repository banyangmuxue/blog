package com.blog.blogbackend.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PostViewService {

    boolean recordView(String slug, HttpServletRequest request, HttpServletResponse response);
}
