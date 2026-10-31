package com.blog.blogbackend.dto;

import lombok.Data;

@Data
public class PostQuery {
    private Integer page = 1;
    private Integer size = 10;
    private String category;
    private String tag;
    private Boolean pinned;
    private String q;
    private String sort = "latest";
    private Integer offset;
}
