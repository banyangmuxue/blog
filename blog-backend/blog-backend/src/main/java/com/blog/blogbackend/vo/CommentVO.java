package com.blog.blogbackend.vo;

import lombok.Data;

@Data
public class CommentVO {
    private Long id;
    private String nickname;
    private String content;
    private String createdAt;
    private ReplyVO reply;
}
