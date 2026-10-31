package com.blog.blogbackend.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminPostSummaryVO {
    private Long id;
    private String slug;
    private String title;
    private String excerpt;
    private String status;
    private Boolean pinned;
    private Long views;
    private String categorySlug;
    private String categoryName;
    private String publishedAt;
    private String updatedAt;
    private List<TagBriefVO> tags;
}
