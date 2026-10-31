package com.blog.blogbackend.dto;

import lombok.Data;

@Data
public class MessageCreateRequest {
    private String slug;
    private String nickname;
    private String email;
    private String content;
}
