package com.blog.blogbackend.service;

import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.AdminPostQuery;
import com.blog.blogbackend.dto.AdminPostSaveRequest;
import com.blog.blogbackend.vo.AdminPostDetailVO;
import com.blog.blogbackend.vo.AdminPostSummaryVO;

public interface AdminPostService {

    PageResult<AdminPostSummaryVO> getPosts(AdminPostQuery query);

    AdminPostDetailVO getPost(Long id);

    AdminPostDetailVO createPost(AdminPostSaveRequest request);

    AdminPostDetailVO updatePost(Long id, AdminPostSaveRequest request);

    AdminPostDetailVO updateStatus(Long id, String status);

    void deletePost(Long id);
}
