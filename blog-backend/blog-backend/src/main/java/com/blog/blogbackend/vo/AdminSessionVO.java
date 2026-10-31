package com.blog.blogbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminSessionVO {
    private boolean loggedIn;
    private String username;
}
