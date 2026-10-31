package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.config.CacheConfig;
import com.blog.blogbackend.dto.AdminSiteConfigSaveRequest;
import com.blog.blogbackend.entity.SiteConfig;
import com.blog.blogbackend.mapper.SiteConfigMapper;
import com.blog.blogbackend.service.SiteService;
import com.blog.blogbackend.vo.SiteConfigVO;
import com.blog.blogbackend.vo.SocialLinkVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteConfigMapper siteConfigMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(cacheNames = CacheConfig.SITE_CONFIG, key = "'config'", unless = "#result == null")
    public SiteConfigVO getSiteConfig() {
        SiteConfig config = siteConfigMapper.selectSiteConfig();
        if (config == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND, "站点配置不存在", HttpStatus.NOT_FOUND);
        }

        SiteConfigVO vo = new SiteConfigVO();
        vo.setName(config.getName());
        vo.setShortName(config.getShortName());
        vo.setLogoText(config.getLogoText());
        vo.setLogoUrl(config.getLogoUrl());
        vo.setTitle(config.getTitle());
        vo.setSubtitle(config.getSubtitle());
        vo.setDescription(config.getDescription());
        vo.setAuthor(config.getAuthor());
        vo.setAnnouncement(config.getAnnouncement());
        vo.setFooterText(config.getFooterText());
        vo.setIcp(config.getIcp());
        vo.setFoundedAt(config.getFoundedAt());
        vo.setSocials(parseSocials(config.getSocials()));

        SiteConfigVO.FeaturesVO features = new SiteConfigVO.FeaturesVO();
        features.setComments(Boolean.TRUE.equals(config.getFeaturesComments()));
        features.setReview(Boolean.TRUE.equals(config.getFeaturesReview()));
        features.setGuestbook(Boolean.TRUE.equals(config.getFeaturesGuestbook()));
        features.setSearch(Boolean.TRUE.equals(config.getFeaturesSearch()));
        vo.setFeatures(features);

        return vo;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.SITE_CONFIG, allEntries = true)
    public SiteConfigVO updateSiteConfig(AdminSiteConfigSaveRequest request) {
        SiteConfig config = new SiteConfig();
        config.setName(request.getName().trim());
        config.setShortName(request.getShortName().trim());
        String logoText = trimToEmpty(request.getLogoText());
        config.setLogoText(logoText.isEmpty() ? "B/" : logoText);
        config.setLogoUrl(trimToEmpty(request.getLogoUrl()));
        config.setTitle(request.getTitle().trim());
        config.setSubtitle(trimToEmpty(request.getSubtitle()));
        config.setDescription(trimToEmpty(request.getDescription()));
        config.setAuthor(trimToEmpty(request.getAuthor()));
        config.setAnnouncement(trimToEmpty(request.getAnnouncement()));
        config.setFooterText(trimToEmpty(request.getFooterText()));
        config.setIcp(trimToEmpty(request.getIcp()));
        config.setFoundedAt(trimToEmpty(request.getFoundedAt()));
        config.setSocials(toJson(normalizeSocials(request.getSocials())));
        config.setFeaturesComments(request.isFeaturesComments());
        config.setFeaturesReview(request.isFeaturesReview());
        config.setFeaturesGuestbook(request.isFeaturesGuestbook());
        config.setFeaturesSearch(request.isFeaturesSearch());

        if (siteConfigMapper.updateSiteConfig(config) == 0) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND, "站点配置不存在", HttpStatus.NOT_FOUND);
        }
        return getSiteConfig();
    }

    private List<SocialLinkVO> normalizeSocials(List<SocialLinkVO> socials) {
        List<SocialLinkVO> result = new ArrayList<>();
        if (socials == null) {
            return result;
        }
        for (SocialLinkVO item : socials) {
            if (item == null) {
                continue;
            }
            String label = trimToEmpty(item.getLabel());
            String url = trimToEmpty(item.getUrl());
            if (label.isEmpty() && url.isEmpty()) {
                continue;
            }
            if (label.isEmpty() || url.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                        "社交链接的名称和地址需要成对填写", HttpStatus.BAD_REQUEST);
            }
            if (label.length() > 50 || url.length() > 255) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                        "社交链接的名称或地址过长", HttpStatus.BAD_REQUEST);
            }
            SocialLinkVO link = new SocialLinkVO();
            link.setLabel(label);
            link.setUrl(url);
            result.add(link);
        }
        if (result.size() > 20) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "社交链接最多填写 20 条", HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("站点社交链接 JSON 序列化失败", e);
            throw new BusinessException(ErrorCode.SERVER_ERROR,
                    "站点配置保存失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private List<SocialLinkVO> parseSocials(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<SocialLinkVO>>() {});
        } catch (Exception e) {
            log.warn("站点社交链接 JSON 解析失败：{}", json, e);
            return List.of();
        }
    }
}
