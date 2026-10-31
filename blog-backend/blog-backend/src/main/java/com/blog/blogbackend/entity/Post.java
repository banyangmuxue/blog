package com.blog.blogbackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private String slug;
    private String title;
    private String excerpt;
    private String content;
    private String coverFrom;
    private String coverTo;
    private String coverImage;
    private Long categoryId;
    private String status;
    private Boolean pinned;
    private Long views;
    private LocalDateTime publishedAt;
}
