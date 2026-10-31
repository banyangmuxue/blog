package com.blog.blogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRow {
    private Long id;
    private String slug;
    private String title;
    private String excerpt;
    private String content;
    private String coverFrom;
    private String coverTo;
    private String coverImage;
    private Long views;
    private Boolean pinned;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private String categorySlug;
    private String categoryName;
    private String categoryDescription;
}
