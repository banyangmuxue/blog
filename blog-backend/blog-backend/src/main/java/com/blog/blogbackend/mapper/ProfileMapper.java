package com.blog.blogbackend.mapper;

import com.blog.blogbackend.entity.Profile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfileMapper {
    Profile selectProfile();

    int updateProfile(Profile profile);
}