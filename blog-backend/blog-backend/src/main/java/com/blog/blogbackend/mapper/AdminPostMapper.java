package com.blog.blogbackend.mapper;

import com.blog.blogbackend.dto.AdminPostQuery;
import com.blog.blogbackend.dto.AdminPostRow;
import com.blog.blogbackend.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminPostMapper {
    long countAdminPosts(AdminPostQuery query);

    List<AdminPostRow> selectAdminPostPage(AdminPostQuery query);

    AdminPostRow selectAdminPostById(@Param("id") Long id);

    int countBySlug(@Param("slug") String slug, @Param("excludeId") Long excludeId);

    int insertPost(Post post);

    int updatePost(Post post);

    int updatePostStatus(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("publishedAt") LocalDateTime publishedAt);

    int deletePost(@Param("id") Long id);

    int deletePostTags(@Param("postId") Long postId);

    int insertPostTags(@Param("postId") Long postId, @Param("tagIds") List<Long> tagIds);

    List<String> selectTagSlugsByPostId(@Param("postId") Long postId);
}
