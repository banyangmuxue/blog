package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@ConditionalOnProperty(name = "blog.upload.type", havingValue = "local", matchIfMissing = true)
public class ImageStorageServiceImpl implements ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM");

    @Value("${blog.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 保存文章封面图，返回可直接访问的 URL。
     */
    @Override
    public String storeCover(MultipartFile file) {
        return store(file, "covers");
    }

    /**
     * 保存头像图片，返回可直接访问的 URL。
     */
    @Override
    public String storeAvatar(MultipartFile file) {
        return store(file, "avatars");
    }

    /**
     * 保存站点 Logo 图片，返回可直接访问的 URL。
     */
    @Override
    public String storeLogo(MultipartFile file) {
        return store(file, "logos");
    }

    private String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "请选择要上传的图片", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "图片不能超过 5MB", HttpStatus.BAD_REQUEST);
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "仅支持 JPG、PNG、GIF、WEBP 格式的图片", HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "仅支持上传图片文件", HttpStatus.BAD_REQUEST);
        }

        String relativePath = folder + "/" + LocalDate.now().format(MONTH_FORMAT)
                + "/" + UUID.randomUUID() + "." + extension;
        Path target = root().resolve(relativePath);
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            log.error("图片保存失败：{}", target, e);
            throw new BusinessException(ErrorCode.SERVER_ERROR,
                    "图片保存失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return "/api/files/" + relativePath;
    }

    @Override
    public Path root() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return "";
        }
        return filename.substring(index + 1).toLowerCase(Locale.ROOT);
    }
}
