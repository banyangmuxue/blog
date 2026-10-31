package com.blog.blogbackend.entity;

import lombok.Data;

@Data
public class Profile {
    private Integer id;
    private String name;
    private String avatarText;
    private String avatarUrl;
    private String bio;
    private String skills;
    private String socials;
    private String joinedAt;
    private String about;
}