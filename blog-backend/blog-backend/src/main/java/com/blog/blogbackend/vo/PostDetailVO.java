package com.blog.blogbackend.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostDetailVO extends PostSummaryVO {
    private String content;
}
