package com.blog.blogbackend.dto;

import com.blog.blogbackend.vo.SocialLinkVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AdminSiteConfigSaveRequest {

    @NotBlank(message = "站点全名不能为空")
    @Size(max = 50, message = "站点全名不能超过 50 个字符")
    private String name;

    @NotBlank(message = "站点简称不能为空")
    @Size(max = 20, message = "站点简称不能超过 20 个字符")
    private String shortName;

    @Size(max = 20, message = "Logo 文字不能超过 20 个字符")
    private String logoText;

    @Size(max = 255, message = "Logo 图片地址不能超过 255 个字符")
    private String logoUrl;

    @NotBlank(message = "首页标题不能为空")
    @Size(max = 50, message = "首页标题不能超过 50 个字符")
    private String title;

    @Size(max = 100, message = "副标题不能超过 100 个字符")
    private String subtitle;

    @Size(max = 500, message = "站点描述不能超过 500 个字符")
    private String description;

    @Size(max = 50, message = "博主名不能超过 50 个字符")
    private String author;

    @Size(max = 255, message = "公告不能超过 255 个字符")
    private String announcement;

    @Size(max = 100, message = "页脚文案不能超过 100 个字符")
    private String footerText;

    @Size(max = 50, message = "备案号不能超过 50 个字符")
    private String icp;

    @Pattern(regexp = "^$|\\d{4}-\\d{2}$", message = "建站月份格式应为 YYYY-MM")
    private String foundedAt;

    @Size(max = 20, message = "社交链接最多填写 20 条")
    private List<SocialLinkVO> socials;

    private boolean featuresComments = true;

    private boolean featuresReview = true;

    private boolean featuresGuestbook = true;

    private boolean featuresSearch = true;
}
