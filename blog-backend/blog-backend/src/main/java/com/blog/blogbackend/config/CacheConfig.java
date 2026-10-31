package com.blog.blogbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Redis 缓存配置。
 * Redis 只作为 MySQL 之上的缓存层，读取失败时自动回退数据库。
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig implements CachingConfigurer {

    public static final String PINNED_POSTS = "pinnedPosts";
    public static final String POST_DETAIL = "postDetail";
    public static final String SITE_CONFIG = "siteConfig";
    public static final String TAG_LIST = "tagList";
    public static final String CATEGORY_LIST = "categoryList";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .prefixCacheNameWith("blog:")
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(Map.of(
                        PINNED_POSTS, defaults.entryTtl(Duration.ofMinutes(5)),
                        POST_DETAIL, defaults.entryTtl(Duration.ofMinutes(10)),
                        SITE_CONFIG, defaults.entryTtl(Duration.ofHours(1)),
                        TAG_LIST, defaults.entryTtl(Duration.ofMinutes(30)),
                        CATEGORY_LIST, defaults.entryTtl(Duration.ofMinutes(30)))
                )
                .build();
    }

    /**
     * 缓存异常只记录日志，不阻断业务请求，避免 Redis 故障导致博客不可访问。
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis 缓存读取失败，回退数据库 cache={} key={}: {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis 缓存写入失败 cache={} key={}: {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis 缓存清理失败 cache={} key={}: {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis 缓存清空失败 cache={}: {}",
                        cache.getName(), exception.getMessage());
            }
        };
    }
}
