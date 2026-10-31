package com.blog.blogbackend.mapper;

import com.blog.blogbackend.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryVO> selectCategoryList();
    Long selectIdBySlug(@Param("slug") String slug);
}