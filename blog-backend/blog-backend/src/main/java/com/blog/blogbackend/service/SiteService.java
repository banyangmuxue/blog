package com.blog.blogbackend.service;

import com.blog.blogbackend.dto.AdminSiteConfigSaveRequest;
import com.blog.blogbackend.vo.SiteConfigVO;

public interface SiteService {

    SiteConfigVO getSiteConfig();

    SiteConfigVO updateSiteConfig(AdminSiteConfigSaveRequest request);
}
