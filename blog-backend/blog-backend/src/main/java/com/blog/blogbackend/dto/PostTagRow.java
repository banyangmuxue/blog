package com.blog.blogbackend.dto;

import lombok.Data;

@Data
public class PostTagRow {
    private Long postId;
    private String slug;
    private String name;
}
