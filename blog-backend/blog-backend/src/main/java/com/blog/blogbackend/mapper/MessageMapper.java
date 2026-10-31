package com.blog.blogbackend.mapper;

import com.blog.blogbackend.dto.MessageRow;
import com.blog.blogbackend.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MessageMapper {
    long countApproved();

    List<MessageRow> selectApprovedPage(@Param("offset") int offset, @Param("size") int size);

    long countRecentByIp(@Param("ip") String ip, @Param("since") LocalDateTime since);

    int insert(Message message);
}
