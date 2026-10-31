package com.blog.blogbackend.service;

import com.blog.blogbackend.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> getCategories();
}
