package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.common.BusinessException;
import com.blog.blogbackend.common.ErrorCode;
import com.blog.blogbackend.dto.AdminProfileSaveRequest;
import com.blog.blogbackend.entity.Profile;
import com.blog.blogbackend.mapper.ProfileMapper;
import com.blog.blogbackend.service.ProfileService;
import com.blog.blogbackend.vo.ProfileVO;
import com.blog.blogbackend.vo.SocialLinkVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMapper profileMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ProfileVO getProfile() {
        Profile profile = profileMapper.selectProfile();
        if (profile == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND, "博主信息不存在", HttpStatus.NOT_FOUND);
        }

        ProfileVO vo = new ProfileVO();
        vo.setName(profile.getName());
        vo.setAvatarText(profile.getAvatarText());
        vo.setAvatarUrl(profile.getAvatarUrl());
        vo.setBio(profile.getBio());
        vo.setJoinedAt(profile.getJoinedAt());
        vo.setAbout(profile.getAbout());
        vo.setSkills(parseSkills(profile.getSkills()));
        vo.setSocials(parseSocials(profile.getSocials()));
        return vo;
    }

    @Override
    @Transactional
    public ProfileVO updateProfile(AdminProfileSaveRequest request) {
        Profile profile = new Profile();
        profile.setName(request.getName().trim());
        profile.setAvatarText(trimToEmpty(request.getAvatarText()));
        profile.setAvatarUrl(trimToEmpty(request.getAvatarUrl()));
        profile.setBio(trimToEmpty(request.getBio()));
        profile.setJoinedAt(trimToEmpty(request.getJoinedAt()));
        profile.setAbout(request.getAbout() == null ? "" : request.getAbout());
        profile.setSkills(toJson(normalizeSkills(request.getSkills())));
        profile.setSocials(toJson(normalizeSocials(request.getSocials())));

        if (profileMapper.updateProfile(profile) == 0) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND, "博主信息不存在", HttpStatus.NOT_FOUND);
        }
        return getProfile();
    }

    private List<String> normalizeSkills(List<String> skills) {
        List<String> result = new ArrayList<>();
        if (skills == null) {
            return result;
        }
        for (String item : skills) {
            if (item == null) {
                continue;
            }
            String value = item.trim();
            if (value.isEmpty()) {
                continue;
            }
            if (value.length() > 30) {
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                        "单个技能不能超过 30 个字符", HttpStatus.BAD_REQUEST);
            }
            result.add(value);
        }
        if (result.size() > 20) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "技能最多填写 20 项", HttpStatus.BAD_REQUEST);
        }
        return result;
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
            log.error("博主资料 JSON 序列化失败", e);
            throw new BusinessException(ErrorCode.SERVER_ERROR,
                    "资料保存失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> parseSkills(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("博主技能 JSON 解析失败：{}", json, e);
            return List.of();
        }
    }

    private List<SocialLinkVO> parseSocials(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<SocialLinkVO>>() {});
        } catch (Exception e) {
            log.warn("博主社交链接 JSON 解析失败：{}", json, e);
            return List.of();
        }
    }
}
