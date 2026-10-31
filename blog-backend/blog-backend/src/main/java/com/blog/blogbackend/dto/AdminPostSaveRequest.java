package com.blog.blogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AdminPostSaveRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 120, message = "标题不能超过 120 个字符")
    private String title;

    @NotBlank(message = "Slug 不能为空")
    @Size(max = 100, message = "Slug 不能超过 100 个字符")
    private String slug;

    @Size(max = 300, message = "摘要不能超过 300 个字符")
    private String excerpt;

    @Size(max = 200000, message = "正文长度超出限制")
    private String content;

    @Size(max = 7, message = "封面起始色格式不正确")
    private String coverFrom;

    @Size(max = 7, message = "封面结束色格式不正确")
    private String coverTo;

    @Size(max = 255, message = "封面图地址过长")
    private String coverImage;

    private String categorySlug;

    private List<String> tagSlugs;

    private String status;

    private Boolean pinned;
}
