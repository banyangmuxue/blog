package com.blog.blogbackend.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminPostDetailVO extends AdminPostSummaryVO {
    private String content;
    private String coverFrom;
    private String coverTo;
    private String coverImage;
    private List<String> tagSlugs;
}
