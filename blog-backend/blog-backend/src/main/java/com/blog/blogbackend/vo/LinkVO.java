package com.blog.blogbackend.vo;

import lombok.Data;

@Data
public class LinkVO {
    private Long id;
    private String name;
    private String url;
    private String domain;
    private String avatarText;
    private String description;
    private Integer order;
    private Boolean visible;
}
