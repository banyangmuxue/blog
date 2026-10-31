package com.blog.blogbackend.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;
    private int pages;

    public static <T> PageResult<T> of(List<T> items, long total, int page, int size) {
        PageResult<T> r = new PageResult<>();
        r.items = items;
        r.total = total;
        r.page = page;
        r.size = size;
        r.pages = Math.max(1, (int) Math.ceil(total / (double) size));
        return r;
    }
}