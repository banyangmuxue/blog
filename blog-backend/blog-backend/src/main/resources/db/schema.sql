-- ============================================================
-- 博客系统数据库 v1
-- 对应接口文档：docs/api.md
-- 适用版本：MySQL 8.0+
-- 字符集：utf8mb4
-- 说明：本文件包含建库、建表与初始化数据，首次部署时执行一次
-- ============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `blog`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `blog`;

-- ------------------------------------------------------------
-- 1. 分类
-- ------------------------------------------------------------
CREATE TABLE `category` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `slug`        VARCHAR(50)     NOT NULL                COMMENT 'URL 标识，如 frontend',
  `name`        VARCHAR(50)     NOT NULL                COMMENT '分类名',
  `description` VARCHAR(255)    NOT NULL DEFAULT ''     COMMENT '分类描述',
  `sort_order`  INT             NOT NULL DEFAULT 0      COMMENT '排序，升序',
  `created_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_slug` (`slug`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章分类';

-- ------------------------------------------------------------
-- 2. 标签
-- ------------------------------------------------------------
CREATE TABLE `tag` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `slug`       VARCHAR(50)     NOT NULL                COMMENT 'URL 标识，如 vue',
  `name`       VARCHAR(50)     NOT NULL                COMMENT '标签名',
  `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_slug` (`slug`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章标签';

-- ------------------------------------------------------------
-- 3. 文章
--    readingTime（阅读分钟数）与 coverLabel（封面月份）由后端按 content / published_at 计算，不落库
-- ------------------------------------------------------------
CREATE TABLE `post` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `slug`         VARCHAR(100)    NOT NULL                COMMENT 'URL 标识，全局唯一',
  `title`        VARCHAR(120)    NOT NULL                COMMENT '标题',
  `excerpt`      VARCHAR(300)    NOT NULL DEFAULT ''     COMMENT '摘要',
  `content`      MEDIUMTEXT      NOT NULL                COMMENT 'Markdown 正文',
  `cover_from`   CHAR(7)         NOT NULL DEFAULT '#123043' COMMENT '封面渐变起始色',
  `cover_to`     CHAR(7)         NOT NULL DEFAULT '#2a1b4d' COMMENT '封面渐变结束色',
  `cover_image`  VARCHAR(255)    NOT NULL DEFAULT ''     COMMENT '封面图 URL，空则使用渐变封面',
  `category_id`  BIGINT UNSIGNED NULL                    COMMENT '分类 ID',
  `status`       VARCHAR(16)     NOT NULL DEFAULT 'draft' COMMENT '状态：draft 草稿 / published 已发布',
  `pinned`       TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '是否置顶：0 否 / 1 是',
  `views`        INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '阅读量',
  `published_at` DATETIME        NULL                    COMMENT '发布时间；status=published 时必填',
  `created_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_slug` (`slug`),
  KEY `idx_post_status_published` (`status`, `published_at`),
  KEY `idx_post_category` (`category_id`),
  KEY `idx_post_pinned` (`pinned`),
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章';

-- ------------------------------------------------------------
-- 4. 文章-标签关联
-- ------------------------------------------------------------
CREATE TABLE `post_tag` (
  `post_id` BIGINT UNSIGNED NOT NULL COMMENT '文章 ID',
  `tag_id`  BIGINT UNSIGNED NOT NULL COMMENT '标签 ID',
  PRIMARY KEY (`post_id`, `tag_id`),
  KEY `idx_post_tag_tag` (`tag_id`),
  CONSTRAINT `fk_post_tag_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章与标签关联';

-- ------------------------------------------------------------
-- 5. 评论
--    reply_content / reply_at 为博主单条回复；email 仅入库，不对外返回
-- ------------------------------------------------------------
CREATE TABLE `comment` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id`       BIGINT UNSIGNED NOT NULL                COMMENT '文章 ID',
  `nickname`      VARCHAR(20)     NOT NULL                COMMENT '昵称',
  `email`         VARCHAR(100)    NULL                    COMMENT '邮箱，仅用于联系，不公开',
  `content`       VARCHAR(500)    NOT NULL                COMMENT '评论内容（纯文本）',
  `status`        VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending 待审 / approved 通过 / rejected 拒绝',
  `reply_content` VARCHAR(500)    NULL                    COMMENT '博主回复内容',
  `reply_at`      DATETIME        NULL                    COMMENT '回复时间',
  `ip`            VARCHAR(45)     NULL                    COMMENT '提交者 IP，用于限流与风控',
  `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_post_status_created` (`post_id`, `status`, `created_at`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章评论';

-- ------------------------------------------------------------
-- 6. 留言
-- ------------------------------------------------------------
CREATE TABLE `message` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `nickname`      VARCHAR(20)     NOT NULL                COMMENT '昵称',
  `email`         VARCHAR(100)    NULL                    COMMENT '邮箱，仅用于联系，不公开',
  `content`       VARCHAR(300)    NOT NULL                COMMENT '留言内容（纯文本）',
  `status`        VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending 待审 / approved 通过 / rejected 拒绝',
  `reply_content` VARCHAR(500)    NULL                    COMMENT '博主回复内容',
  `reply_at`      DATETIME        NULL                    COMMENT '回复时间',
  `ip`            VARCHAR(45)     NULL                    COMMENT '提交者 IP，用于限流与风控',
  `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_status_created` (`status`, `created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '留言板留言';

-- ------------------------------------------------------------
-- 7. 友链
-- ------------------------------------------------------------
CREATE TABLE `link` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50)     NOT NULL                COMMENT '站点名',
  `url`         VARCHAR(255)    NOT NULL                COMMENT '站点地址',
  `domain`      VARCHAR(100)    NOT NULL DEFAULT ''     COMMENT '展示域名',
  `avatar_text` VARCHAR(4)      NOT NULL DEFAULT ''     COMMENT '头像占位文字',
  `description` VARCHAR(255)    NOT NULL DEFAULT ''     COMMENT '描述',
  `sort_order`  INT             NOT NULL DEFAULT 0      COMMENT '排序，升序',
  `visible`     TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '是否展示：0 否 / 1 是',
  `created_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_link_visible_sort` (`visible`, `sort_order`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '友情链接';

-- ------------------------------------------------------------
-- 8. 站点配置（单行表，固定 id = 1）
--    socials 存 JSON 数组文本：[{"label":"GitHub","url":"..."}]
-- ------------------------------------------------------------
CREATE TABLE `site_config` (
  `id`                TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '固定为 1',
  `name`              VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '站点全名',
  `short_name`        VARCHAR(20)     NOT NULL DEFAULT '' COMMENT '站点简称',
  `logo_text`         VARCHAR(20)     NOT NULL DEFAULT 'B/' COMMENT 'Logo 文字',
  `logo_url`          VARCHAR(255)    NOT NULL DEFAULT '' COMMENT 'Logo 图片 URL，空则使用文字',
  `title`             VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '首页标题',
  `subtitle`          VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '副标题',
  `description`       VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '站点描述',
  `author`            VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '博主名',
  `announcement`      VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '公告',
  `footer_text`       VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '页脚文案',
  `icp`               VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '备案号',
  `founded_at`        VARCHAR(7)      NOT NULL DEFAULT '' COMMENT '建站月份，如 2023-06',
  `socials`           TEXT            NOT NULL COMMENT '社交链接 JSON 数组文本',
  `features_comments` TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '是否开启评论',
  `features_review`   TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '评论/留言是否先审后发',
  `features_guestbook` TINYINT(1)     NOT NULL DEFAULT 1 COMMENT '是否开启留言板',
  `features_search`   TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '是否开启搜索',
  `created_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站点配置';

-- ------------------------------------------------------------
-- 9. 博主信息（单行表，固定 id = 1）
--    skills / socials 存 JSON 数组文本
-- ------------------------------------------------------------
CREATE TABLE `profile` (
  `id`          TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '固定为 1',
  `name`        VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '博主名',
  `avatar_text` VARCHAR(4)      NOT NULL DEFAULT '' COMMENT '头像占位文字',
  `avatar_url`  VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '头像图片 URL，空则使用文字头像',
  `bio`         VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '一句话简介',
  `skills`      TEXT            NOT NULL COMMENT '技能 JSON 数组文本',
  `socials`     TEXT            NOT NULL COMMENT '社交链接 JSON 数组文本',
  `joined_at`   VARCHAR(7)      NOT NULL DEFAULT '' COMMENT '加入月份，如 2023-06',
  `about`       MEDIUMTEXT      NOT NULL COMMENT '关于页 Markdown 正文',
  `created_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博主信息';

-- ------------------------------------------------------------
-- 10. 阅读记录（用于同一访客 30 分钟内重复阅读去重）
--     visitor_key 建议由后端写入 HttpOnly Cookie 的访客标识
-- ------------------------------------------------------------
CREATE TABLE `post_view` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id`     BIGINT UNSIGNED NOT NULL                COMMENT '文章 ID',
  `visitor_key` VARCHAR(64)     NOT NULL                COMMENT '访客标识',
  `viewed_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近一次计入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_view` (`post_id`, `visitor_key`),
  KEY `idx_post_view_time` (`viewed_at`),
  CONSTRAINT `fk_post_view_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章阅读去重记录';

-- ============================================================
-- 初始化数据（首次执行；文章数据后续通过后台或脚本导入）
-- ============================================================

INSERT INTO `site_config`
  (`id`, `name`, `short_name`, `logo_text`, `logo_url`, `title`, `subtitle`,
   `description`, `author`, `announcement`, `footer_text`, `icp`, `founded_at`,
   `socials`, `features_comments`, `features_review`, `features_guestbook`, `features_search`)
VALUES
  (1,
   'B/ 记录代码与生活',
   'B/',
   'B/',
   '',
   '记录代码与生活',
   '前端、后端与持续学习 — 一个个人博客',
   '这里记录我在前端工程、后端服务与日常学习中的思考：怎么把页面写得更稳，怎么让接口更清楚，以及怎么把读过的东西留下来。',
   '林深',
   '博客已升级到 v2，评论与留言默认先审后发，感谢你的耐心。',
   '用代码记录思考，用文字整理生活。',
   '京 ICP 备 2026000000 号',
   '2023-06',
   '[{"label":"GitHub","url":"https://github.com/"},{"label":"RSS","url":"/archive"},{"label":"Email","url":"mailto:hi@example.com"}]',
   1, 1, 1, 1);

INSERT INTO `profile`
  (`id`, `name`, `avatar_text`, `avatar_url`, `bio`, `skills`, `socials`, `joined_at`, `about`)
VALUES
  (1,
   '林深',
   'L',
   '',
   '写代码，也写字。关注前端工程化与后端服务设计。',
   '["Vue","TypeScript","Spring Boot","写作"]',
   '[{"label":"GitHub","url":"https://github.com/"},{"label":"Email","url":"mailto:hi@example.com"}]',
   '2023-06',
   '## 我是谁

我是林深，一名在做前端、也在补后端的开发者。白天写 Vue 与 TypeScript，晚上读一点 Java 和数据库，剩下的时间用来把学到的东西写成文章。

这个博客从 2023 年开始，中间停更过两次。现在重新整理成 v2，希望它更像一个可以长期维护的地方，而不是一次性的作品集。

## 我在做什么

- 维护一套基于 Vue 3 与 Spring Boot 的个人内容系统
- 把性能优化、接口设计这类“说起来都对”的话题拆成可以验证的步骤
- 每月至少写一篇长文，记录一次真实的踩坑过程

## 我关注的方向

### 前端工程化

比起追新，我更关心一套代码在半年后还能不能被自己读懂。组件边界、状态管理、样式组织，这些看起来琐碎的决定，往往比框架版本更能影响项目的寿命。

### 后端服务设计

一个人的项目也需要清楚的接口约定。我在文章里反复提到的一件事是：前后端分离的收益，来自双方都愿意把规则写下来。

### 写作与阅读

写作是整理思考最便宜的方式。读过的书、用过的工具、做错的决定，只要能说清楚一次，就值得留下来。

## 联系方式

如果你也在做类似的事情，欢迎在留言板打个招呼，或者通过侧栏的邮箱联系我。评论与留言我都会看，只是回复可能慢一点。');

INSERT INTO `category` (`slug`, `name`, `description`, `sort_order`) VALUES
  ('frontend',    '前端开发', 'Vue、TypeScript、CSS 与性能优化，关于把界面写稳这件事。', 1),
  ('backend',     '后端笔记', 'Spring Boot、接口设计与数据持久化的实践记录。', 2),
  ('notes',       '随笔',     '读书、写作与日常，和技术无关但同样重要的部分。', 3),
  ('engineering', '工程实践', '构建、调试、协作与长期维护，一个人的工程化。', 4);

INSERT INTO `tag` (`slug`, `name`) VALUES
  ('vue',         'Vue'),
  ('typescript',  'TypeScript'),
  ('css',         'CSS'),
  ('performance', '性能优化'),
  ('spring-boot', 'Spring Boot'),
  ('java',        'Java'),
  ('database',    '数据库'),
  ('writing',     '写作'),
  ('reading',     '阅读'),
  ('tools',       '工具');

INSERT INTO `link`
  (`name`, `url`, `domain`, `avatar_text`, `description`, `sort_order`, `visible`)
VALUES
  ('Overreacted', 'https://overreacted.io/', 'overreacted.io', 'O',
   'Dan Abramov 的博客，关于 React、抽象与学习方式的长文。', 1, 1),
  ('Julia Evans', 'https://jvns.ca/', 'jvns.ca', 'J',
   '用漫画和直白的语言解释调试、网络与系统知识。', 2, 1),
  ('阮一峰的网络日志', 'https://www.ruanyifeng.com/blog/', 'ruanyifeng.com', '阮',
   '每周科技爱好者周刊，长期稳定的中文技术写作。', 3, 1),
  ('CSS-Tricks', 'https://css-tricks.com/', 'css-tricks.com', 'C',
   '前端布局与 CSS 技巧的资料库，遇到问题常来查。', 4, 1),
  ('Martin Fowler', 'https://martinfowler.com/', 'martinfowler.com', 'M',
   '软件设计与重构的经典文章集合，值得慢慢读。', 5, 1);
