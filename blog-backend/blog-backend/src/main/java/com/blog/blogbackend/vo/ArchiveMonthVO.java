package com.blog.blogbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveMonthVO {
    private String month;
    private String label;
    private List<PostSummaryVO> items;
}
