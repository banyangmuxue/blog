package com.blog.blogbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminPostRow extends PostRow {
    private String status;
}
