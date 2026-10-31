package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.config.CacheConfig;
import com.blog.blogbackend.mapper.TagMapper;
import com.blog.blogbackend.service.TagService;
import com.blog.blogbackend.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    @Cacheable(cacheNames = CacheConfig.TAG_LIST, key = "'all'")
    public List<TagVO> getTags() {
        // 缓存要求返回 ArrayList：其他不可变 List 写进 Redis 后无法反序列化
        return new ArrayList<>(tagMapper.selectTagList().stream()
                .filter(item -> item.getCount() != null && item.getCount() > 0)
                .toList());
    }
}
