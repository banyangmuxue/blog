package com.blog.blogbackend.mapper;

import com.blog.blogbackend.dto.PostTagRow;
import com.blog.blogbackend.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    List<TagVO> selectTagList();

    List<PostTagRow> selectTagsByPostIds(@Param("postIds") List<Long> postIds);

    List<Long> selectIdsBySlugs(@Param("slugs") List<String> slugs);
}
