package com.blog.blogbackend.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.service.ImageStorageService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 阿里云 OSS 图片存储。通过 blog.upload.type=oss 启用。
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "blog.upload.type", havingValue = "oss")
public class OssImageStorageServiceImpl implements ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM");

    @Value("${blog.upload.dir:uploads}")
    private String uploadDir;

    @Value("${blog.upload.oss.endpoint:}")
    private String endpoint;

    @Value("${blog.upload.oss.bucket:}")
    private String bucket;

    @Value("${blog.upload.oss.access-key-id:}")
    private String accessKeyId;

    @Value("${blog.upload.oss.access-key-secret:}")
    private String accessKeySecret;

    @Value("${blog.upload.oss.public-base-url:}")
    private String publicBaseUrl;

    private OSS client;

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

    /**
     * 保留本地目录，用于继续访问切换前上传的历史图片。
     */
    @Override
    public Path root() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
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
            throw new BusinessException(ErrorCode.PARAM_INVALID, "仅支持上传图片文件", HttpStatus.BAD_REQUEST);
        }

        String key = folder + "/" + LocalDate.now().format(MONTH_FORMAT)
                + "/" + UUID.randomUUID() + "." + extension;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        try (InputStream input = file.getInputStream()) {
            client().putObject(bucket, key, input, metadata);
        } catch (IOException | OSSException | ClientException e) {
            log.error("图片上传到 OSS 失败：{}", key, e);
            throw new BusinessException(ErrorCode.SERVER_ERROR,
                    "图片上传失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return publicUrl(key);
    }

    private synchronized OSS client() {
        if (client == null) {
            if (!StringUtils.hasText(endpoint) || !StringUtils.hasText(bucket)
                    || !StringUtils.hasText(accessKeyId) || !StringUtils.hasText(accessKeySecret)) {
                throw new BusinessException(ErrorCode.SERVER_ERROR,
                        "对象存储配置不完整，请检查 blog.upload.oss 配置", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        return client;
    }

    private String publicUrl(String key) {
        if (StringUtils.hasText(publicBaseUrl)) {
            return trimTrailingSlash(publicBaseUrl.trim()) + "/" + key;
        }
        String host = endpoint;
        int schemeIndex = host.indexOf("://");
        if (schemeIndex >= 0) {
            host = host.substring(schemeIndex + 3);
        }
        return "https://" + bucket + "." + trimTrailingSlash(host) + "/" + key;
    }

    private String trimTrailingSlash(String value) {
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
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

    @PreDestroy
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }
}
