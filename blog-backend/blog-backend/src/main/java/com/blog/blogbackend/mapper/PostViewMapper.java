package com.blog.blogbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface PostViewMapper {
    int insertIgnore(@Param("postId") Long postId,
                     @Param("visitorKey") String visitorKey,
                     @Param("viewedAt") LocalDateTime viewedAt);

    int refresh(@Param("postId") Long postId,
                @Param("visitorKey") String visitorKey,
                @Param("threshold") LocalDateTime threshold,
                @Param("viewedAt") LocalDateTime viewedAt);
}
