package com.blog.blogbackend.service;

import com.blog.blogbackend.common.PageResult;
import com.blog.blogbackend.dto.PostQuery;
import com.blog.blogbackend.vo.AdjacentPostsVO;
import com.blog.blogbackend.vo.ArchiveVO;
import com.blog.blogbackend.vo.PostDetailVO;
import com.blog.blogbackend.vo.PostSummaryVO;

import java.util.List;

public interface PostService {

    PageResult<PostSummaryVO> getPosts(PostQuery query);

    List<PostSummaryVO> getLatestPosts(int limit);

    PostDetailVO getPost(String slug);

    AdjacentPostsVO getAdjacentPosts(String slug);

    List<ArchiveVO> getArchive();
}
