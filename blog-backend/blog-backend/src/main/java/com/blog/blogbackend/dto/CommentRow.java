package com.blog.blogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentRow {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private String replyContent;
    private LocalDateTime replyAt;
}
