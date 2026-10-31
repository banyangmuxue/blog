package com.blog.blogbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCoverVO {
    private String from;
    private String to;
    private String image;
}
