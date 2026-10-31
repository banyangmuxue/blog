package com.blog.blogbackend.mapper;

import com.blog.blogbackend.dto.AdminCommentRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminCommentMapper {

    long countByPostId(@Param("postId") Long postId);

    List<AdminCommentRow> selectPageByPostId(
            @Param("postId") Long postId,
            @Param("offset") int offset,
            @Param("size") int size);

    int deleteById(@Param("id") Long id);
}
