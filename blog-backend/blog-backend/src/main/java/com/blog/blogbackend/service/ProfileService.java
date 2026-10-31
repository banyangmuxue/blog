package com.blog.blogbackend.service;

import com.blog.blogbackend.dto.AdminProfileSaveRequest;
import com.blog.blogbackend.vo.ProfileVO;

public interface ProfileService {

    ProfileVO getProfile();

    ProfileVO updateProfile(AdminProfileSaveRequest request);
}
