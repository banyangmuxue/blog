package com.blog.blogbackend.vo;

import lombok.Data;

@Data
public class AdminCommentVO {
    private Long id;
    private String nickname;
    private String content;
    private String status;
    private String createdAt;
}
