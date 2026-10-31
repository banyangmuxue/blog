package com.blog.blogbackend.mapper;

import com.blog.blogbackend.dto.CommentRow;
import com.blog.blogbackend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentRow> selectApprovedByPostId(@Param("postId") Long postId);

    long countRecentByIpAndPost(@Param("postId") Long postId,
                                @Param("ip") String ip,
                                @Param("since") LocalDateTime since);

    int insert(Comment comment);
}
