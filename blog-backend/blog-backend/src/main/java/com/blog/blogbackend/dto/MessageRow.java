package com.blog.blogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageRow {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private String replyContent;
    private LocalDateTime replyAt;
}
