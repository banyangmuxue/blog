package com.blog.blogbackend.dto;

import lombok.Data;

@Data
public class AdminPostQuery {
    private Integer page;
    private Integer size;
    private String status;
    private String keyword;
    private Integer offset;
}
