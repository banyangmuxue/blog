package com.blog.blogbackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveVO {
    private String year;
    private int count;
    private List<ArchiveMonthVO> months;
}
