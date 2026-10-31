package com.blog.blogbackend.mapper;

import com.blog.blogbackend.entity.Link;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LinkMapper {
    List<Link> selectVisibleLinks();
}
