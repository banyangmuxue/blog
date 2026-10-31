package com.blog.blogbackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private String nickname;
    private String email;
    private String content;
    private String status;
    private String replyContent;
    private LocalDateTime replyAt;
    private String ip;
    private LocalDateTime createdAt;
}
