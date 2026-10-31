package com.blog.blogbackend.service.impl;

import com.blog.blogbackend.entity.Link;
import com.blog.blogbackend.mapper.LinkMapper;
import com.blog.blogbackend.service.LinkService;
import com.blog.blogbackend.vo.LinkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {

    private final LinkMapper linkMapper;

    @Override
    public List<LinkVO> getLinks() {
        return linkMapper.selectVisibleLinks().stream()
                .map(this::toVO)
                .toList();
    }

    private LinkVO toVO(Link link) {
        LinkVO vo = new LinkVO();
        vo.setId(link.getId());
        vo.setName(link.getName());
        vo.setUrl(link.getUrl());
        vo.setDomain(link.getDomain());
        vo.setAvatarText(link.getAvatarText());
        vo.setDescription(link.getDescription());
        vo.setOrder(link.getSortOrder());
        vo.setVisible(link.getVisible());
        return vo;
    }
}
