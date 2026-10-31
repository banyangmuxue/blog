package com.blog.blogbackend.dto;

import com.blog.blogbackend.vo.SocialLinkVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AdminProfileSaveRequest {

    @NotBlank(message = "博主名不能为空")
    @Size(max = 50, message = "博主名不能超过 50 个字符")
    private String name;

    @Size(max = 4, message = "头像文字最多 4 个字符")
    private String avatarText;

    @Size(max = 255, message = "头像图片地址过长")
    private String avatarUrl;

    @Size(max = 255, message = "简介不能超过 255 个字符")
    private String bio;

    @Size(max = 20, message = "技能最多填写 20 项")
    private List<String> skills;

    @Size(max = 20, message = "社交链接最多填写 20 条")
    private List<SocialLinkVO> socials;

    @Pattern(regexp = "^$|\\d{4}-\\d{2}$", message = "加入月份格式应为 YYYY-MM")
    private String joinedAt;

    @Size(max = 200000, message = "关于正文长度超出限制")
    private String about;
}
