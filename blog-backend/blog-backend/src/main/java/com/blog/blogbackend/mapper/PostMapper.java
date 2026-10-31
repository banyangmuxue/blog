package com.blog.blogbackend.mapper;

import com.blog.blogbackend.dto.PostQuery;
import com.blog.blogbackend.dto.PostRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper {
    long countPosts(PostQuery query);

    List<PostRow> selectPostPage(PostQuery query);

    List<PostRow> selectLatestPosts(@Param("limit") int limit);

    PostRow selectBySlug(@Param("slug") String slug);

    PostRow selectPrevPost(@Param("publishedAt") LocalDateTime publishedAt, @Param("id") Long id);

    PostRow selectNextPost(@Param("publishedAt") LocalDateTime publishedAt, @Param("id") Long id);

    List<PostRow> selectArchivePosts();

    int incrementViews(@Param("postId") Long postId);
}
