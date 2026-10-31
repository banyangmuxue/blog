package com.blog.blogbackend.vo;

import lombok.Data;
import java.util.List;

@Data
public class SiteConfigVO {
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
    private List<SocialLinkVO> socials;
    private FeaturesVO features;

    @Data
    public static class FeaturesVO {
        private boolean comments;
        private boolean review;
        private boolean guestbook;
        private boolean search;
    }
}