package com.blog.blogbackend.common;

import java.util.List;
import java.util.Locale;

/**
 * 评论与留言的违禁词过滤。
 * 如需增删词条，直接修改 WORDS 列表即可；英文词按小写匹配。
 */
public final class BannedWordFilter {

    private static final List<String> WORDS = List.of(
            // 广告引流
            "加微信", "加qq", "微信号", "qq号", "扫码加", "扫二维码", "私聊我",
            "刷单", "兼职招聘", "日赚", "月入过万", "网赚", "代理加盟",
            "优惠券", "低价出售", "返利", "引流",
            // 赌博与灰产
            "赌博", "博彩", "赌场", "彩票代购", "棋牌", "私服", "外挂",
            "贷款秒批", "花呗套现",
            // 色情
            "约炮", "一夜情", "裸聊", "嫖娼", "卖淫", "色情", "成人视频", "成人片",
            // 辱骂
            "傻逼", "煞笔", "沙比", "操你妈", "草你妈", "尼玛", "去死吧",
            "脑残", "智障", "贱人", "狗东西",
            "fuck", "shit", "bitch", "asshole"
    );

    private BannedWordFilter() {
    }

    public static boolean containsBannedWord(String... texts) {
        for (String text : texts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            String normalized = text.toLowerCase(Locale.ROOT);
            for (String word : WORDS) {
                if (normalized.contains(word)) {
                    return true;
                }
            }
        }
        return false;
    }
}
