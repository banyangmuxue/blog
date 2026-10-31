package com.blog.blogbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ImageStorageService {

    /**
     * 保存文章封面图，返回可直接访问的 URL。
     */
    String storeCover(MultipartFile file);

    /**
     * 保存头像图片，返回可直接访问的 URL。
     */
    String storeAvatar(MultipartFile file);

    /**
     * 保存站点 Logo 图片，返回可直接访问的 URL。
     */
    String storeLogo(MultipartFile file);

    Path root();
}
