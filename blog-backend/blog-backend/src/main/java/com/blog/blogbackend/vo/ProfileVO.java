package com.blog.blogbackend.vo;

import lombok.Data;
import java.util.List;

@Data
public class ProfileVO {
    private String name;
    private String avatarText;
    private String avatarUrl;
    private String bio;
    private List<String> skills;
    private List<SocialLinkVO> socials;
    private String joinedAt;
    private String about;
}