package com.blog.blogbackend.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class PostSummaryVO {
    private Long id;
    private String slug;
    private String title;
    private String excerpt;
    private String publishedAt;
    private String updatedAt;
    private Long views;
    private Boolean pinned;
    private PostCoverVO cover;
    private CategoryBriefVO category;
    private List<TagBriefVO> tags;
    private Integer readingTime;
    private String coverLabel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String snippet;
}
