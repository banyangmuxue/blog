package com.blog.blogbackend.vo;

import lombok.Data;

@Data
public class TagVO {
    private String slug;
    private String name;
    private Integer count;
}
