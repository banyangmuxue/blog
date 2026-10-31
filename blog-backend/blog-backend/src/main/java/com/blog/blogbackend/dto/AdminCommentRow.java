package com.blog.blogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCommentRow {
    private Long id;
    private String nickname;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
