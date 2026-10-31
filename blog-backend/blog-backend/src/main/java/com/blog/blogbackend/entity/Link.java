package com.blog.blogbackend.entity;

import lombok.Data;

@Data
public class Link {
    private Long id;
    private String name;
    private String url;
    private String domain;
    private String avatarText;
    private String description;
    private Integer sortOrder;
    private Boolean visible;
}
