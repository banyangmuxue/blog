package com.blog.blogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminStatusRequest {

    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "draft|published", message = "状态只能是 draft 或 published")
    private String status;
}
