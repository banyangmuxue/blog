package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.config.CacheConfig;
import com.blog.blogbackend.mapper.CategoryMapper;
import com.blog.blogbackend.service.CategoryService;
import com.blog.blogbackend.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(cacheNames = CacheConfig.CATEGORY_LIST, key = "'all'")
    public List<CategoryVO> getCategories() {
        // 缓存要求返回 ArrayList：其他不可变 List 写进 Redis 后无法反序列化
        return new ArrayList<>(categoryMapper.selectCategoryList().stream()
                .filter(item -> item.getCount() != null && item.getCount() > 0)
                .toList());
    }
}
