package com.blog.blogbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfig {
    private Integer id;
    private String name;
    private String shortName;
    private String logoText;
    private String logoUrl;
    private String title;
    private String subtitle;
    private String description;
    private String author;
    private String announcement;
    private String footerText;
    private String icp;
    private String foundedAt;
    private String socials;
    private Boolean featuresComments;
    private Boolean featuresReview;
    private Boolean featuresGuestbook;
    private Boolean featuresSearch;;   // 先接住 JSON 文本，在 Service 里再解析
}
