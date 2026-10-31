package com.blog.blogbackend.vo;

import lombok.Data;

@Data
public class CategoryVO {
    private String slug;
    private String name;
    private String description;
    private Integer count;
}