package com.blog.blogbackend.mapper;

import com.blog.blogbackend.entity.SiteConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteConfigMapper {
    SiteConfig selectSiteConfig();

    int updateSiteConfig(SiteConfig config);
}