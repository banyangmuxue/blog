# 博客系统接口文档（v1）

> 技术栈：Spring Boot 3.5 + MyBatis + MySQL
> 适用范围：前台公开接口（文章、分类、标签、评论、留言、友链、归档、站点信息）
> 前端调用代码：[api/index.js](../blog-frontend/src/api/index.js)

---

## 1. 通用约定

### 1.1 基础信息

| 项目 | 约定 |
| --- | --- |
| 开发环境地址 | `http://localhost:8080` |
| 接口前缀 | `/api` |
| 数据格式 | 请求与响应均为 JSON，`Content-Type: application/json; charset=utf-8` |
| 鉴权 | 本期接口均为公开接口，无需登录 |
| 跨域 | 前端请求带 `credentials: 'include'`，后端 CORS 必须指定具体来源，不能使用 `*` |

### 1.2 统一响应结构

所有接口（包括失败）都返回同一层外壳：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | int | `0` 表示成功，非 0 表示业务错误 |
| message | string | 错误提示，前端会直接展示，需为中文可读文案 |
| data | any | 业务数据；无返回内容时为 `null` |

> 注意：前端判断 `payload.code` 为真即视为失败，因此成功时必须是 `0`，不能用 `200` 之类的业务码。

### 1.3 HTTP 状态码

| 场景 | HTTP 状态码 |
| --- | --- |
| 成功 | 200 |
| 参数校验失败 | 400 |
| 未登录 | 401 |
| 无权限 / 功能关闭 | 403 |
| 资源不存在 | 404 |
| 请求过于频繁 | 429 |
| 服务器异常 | 500 |

### 1.4 错误码分段

| 错误码 | HTTP | 含义 |
| --- | --- | --- |
| 0 | 200 | 成功 |
| 1001 | 400 | 参数校验失败 |
| 1002 | 401 | 未登录 |
| 1003 | 403 | 无权限 |
| 1004 | 404 | 资源不存在 |
| 1005 | 429 | 请求过于频繁 |
| 2001 | 404 | 文章不存在或未发布 |
| 3001 | 403 | 评论功能已关闭 |
| 3002 | 403 | 留言功能已关闭 |
| 5000 | 500 | 服务器内部错误 |

### 1.5 时间格式

| 场景 | 格式 | 示例 |
| --- | --- | --- |
| 文章日期（`publishedAt` / `updatedAt`） | `yyyy-MM-dd` | `2026-10-20` |
| 评论、留言时间（`createdAt`） | `yyyy-MM-dd HH:mm` | `2026-10-29 09:12` |

> 与前端现有的 `formatDate` / `relativeTime` 工具函数保持一致，避免时区偏移导致显示错位。

### 1.6 分页约定

- 请求参数：`page` 从 `1` 开始（默认 1），`size` 默认 `10`，最大 `50`
- 响应结构固定为：

```json
{
  "items": [],
  "total": 0,
  "page": 1,
  "size": 10,
  "pages": 1
}
```

- `page` 超出范围时自动收敛到 `[1, pages]`；无数据时 `pages` 返回 `1`
- 文章列表默认按 `publishedAt` 倒序

---

## 2. 数据模型

### 2.1 PostSummary（文章摘要）

列表、归档、上下篇、最新文章统一使用该结构，**不包含正文**：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | long | 文章 ID |
| slug | string | URL 标识，唯一 |
| title | string | 标题 |
| excerpt | string | 摘要 |
| publishedAt | string | 发布日期 `yyyy-MM-dd` |
| updatedAt | string | 更新日期 `yyyy-MM-dd` |
| views | int | 阅读量 |
| pinned | boolean | 是否置顶 |
| cover | object | 封面渐变色 `{ "from": "#0d3b46", "to": "#101a3f" }` |
| category | object \| null | `{ slug, name, description }` |
| tags | array | `[{ slug, name }]` |
| readingTime | int | 预计阅读分钟数（后端计算，最少 2） |
| coverLabel | string | 封面文字，取发布月份 `2026.10` |
| snippet | string | 仅搜索接口返回：关键词附近片段 |

### 2.2 PostDetail（文章详情）

在 `PostSummary` 基础上增加：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| content | string | Markdown 原文，由前端渲染 |

### 2.3 其它模型

| 模型 | 字段 |
| --- | --- |
| Category | `slug`, `name`, `description`, `count` |
| Tag | `slug`, `name`, `count` |
| Comment | `id`, `nickname`, `content`, `createdAt`, `status`, `reply` |
| Reply | `{ content, createdAt }` 或 `null` |
| Message | 同 Comment |
| Link | `id`, `name`, `url`, `domain`, `avatarText`, `description`, `order`, `visible` |
| Social | `{ label, url }` |
| Features | `{ comments, review, guestbook, search }`（boolean） |

> 评论与留言的 `email` 仅入库用于联系，**任何接口都不得返回**。

---

## 3. 接口详情

### 3.1 获取站点配置

`GET /api/site`

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "name": "B/ 记录代码与生活",
    "shortName": "B/",
    "title": "记录代码与生活",
    "subtitle": "前端、后端与持续学习 — 一个个人博客",
    "description": "这里记录我在前端工程、后端服务与日常学习中的思考……",
    "author": "林深",
    "announcement": "博客已升级到 v2，评论与留言默认先审后发，感谢你的耐心。",
    "footerText": "用代码记录思考，用文字整理生活。",
    "icp": "京 ICP 备 2026000000 号",
    "foundedAt": "2023-06",
    "socials": [
      { "label": "GitHub", "url": "https://github.com/" },
      { "label": "RSS", "url": "/archive" },
      { "label": "Email", "url": "mailto:hi@example.com" }
    ],
    "features": {
      "comments": true,
      "review": true,
      "guestbook": true,
      "search": true
    }
  }
}
```

**说明**

- `features.review` 为 `true` 时，评论与留言提交后状态为 `pending`，需审核后才公开
- `features.comments` / `features.guestbook` 为 `false` 时，对应提交接口返回 `3001` / `3002`

---

### 3.2 获取博主信息

`GET /api/profile`

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "name": "林深",
    "avatarText": "L",
    "bio": "写代码，也写字。关注前端工程化与后端服务设计。",
    "skills": ["Vue", "TypeScript", "Spring Boot", "写作"],
    "socials": [
      { "label": "GitHub", "url": "https://github.com/" },
      { "label": "Email", "url": "mailto:hi@example.com" }
    ],
    "joinedAt": "2023-06",
    "about": "## 我是谁\n\n我是林深……"
  }
}
```

**说明**

- `about` 为 Markdown 原文
- `avatarText` 用于头像占位文字

---

### 3.3 获取分类

`GET /api/categories`

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "slug": "frontend",
      "name": "前端开发",
      "description": "Vue、TypeScript、CSS 与性能优化，关于把界面写稳这件事。",
      "count": 4
    }
  ]
}
```

**说明**

- `count` 为已发布文章数
- 只返回 `count > 0` 的分类
- 排序：按分类的 `sort` 字段升序（未配置时按 `id` 升序）

---

### 3.4 获取标签

`GET /api/tags`

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    { "slug": "writing", "name": "写作", "count": 3 },
    { "slug": "vue", "name": "Vue", "count": 1 }
  ]
}
```

**说明**

- 只返回 `count > 0` 的标签
- 排序：`count` 降序，数量相同按 `name` 升序

---

### 3.5 获取文章列表 / 搜索

`GET /api/posts`

**查询参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页数量，最大 50 |
| category | string | 否 | — | 分类 slug |
| tag | string | 否 | — | 标签 slug |
| pinned | boolean | 否 | — | 传 `true` 只返回置顶文章 |
| q | string | 否 | — | 搜索关键词，最长 50 字符 |
| sort | string | 否 | latest | `latest` 最新 / `oldest` 最早 / `views` 阅读最多 |

**行为说明**

- 只返回 `status = published` 的文章
- `category` / `tag` 不存在时返回空列表，不报 404
- 搜索范围：标题、摘要、正文、标签名（不区分大小写）
- 搜索结果排序：标题命中优先，其次按发布时间倒序
- 搜索模式下每条结果额外返回 `snippet`（关键词上下文，约 90 字）
- 其余模式不返回 `snippet`

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": 2,
        "slug": "vue3-performance",
        "title": "Vue 3 渲染性能：先测量，再优化",
        "excerpt": "一次列表页卡顿的排查记录……",
        "publishedAt": "2026-10-20",
        "updatedAt": "2026-10-22",
        "views": 986,
        "pinned": true,
        "cover": { "from": "#0d3b46", "to": "#101a3f" },
        "category": {
          "slug": "frontend",
          "name": "前端开发",
          "description": "Vue、TypeScript、CSS 与性能优化，关于把界面写稳这件事。"
        },
        "tags": [
          { "slug": "vue", "name": "Vue" },
          { "slug": "performance", "name": "性能优化" }
        ],
        "readingTime": 5,
        "coverLabel": "2026.10",
        "snippet": "……滚动时帧率从 60 掉到 30 左右……"
      }
    ],
    "total": 12,
    "page": 1,
    "size": 10,
    "pages": 2
  }
}
```

> `snippet` 仅搜索时出现，非搜索场景可忽略该字段。

---

### 3.6 获取最新文章

`GET /api/posts/latest`

**查询参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| limit | int | 否 | 6 | 返回条数，最大 20 |

**说明**

- 返回 `PostSummary` 数组，不含分页结构
- 按 `publishedAt` 倒序
- 只返回已发布文章

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "slug": "hello-blog-v2",
      "title": "从一个想法到一篇可读的文章",
      "publishedAt": "2026-10-28",
      "views": 1204,
      "pinned": true,
      "category": { "slug": "notes", "name": "随笔", "description": "……" },
      "tags": [{ "slug": "writing", "name": "写作" }],
      "readingTime": 4,
      "coverLabel": "2026.10"
    }
  ]
}
```

---

### 3.7 获取文章详情

`GET /api/posts/{slug}`

**路径参数**

| 参数 | 说明 |
| --- | --- |
| slug | 文章 URL 标识 |

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 2,
    "slug": "vue3-performance",
    "title": "Vue 3 渲染性能：先测量，再优化",
    "excerpt": "一次列表页卡顿的排查记录……",
    "publishedAt": "2026-10-20",
    "updatedAt": "2026-10-22",
    "views": 986,
    "pinned": true,
    "cover": { "from": "#0d3b46", "to": "#101a3f" },
    "category": { "slug": "frontend", "name": "前端开发", "description": "……" },
    "tags": [
      { "slug": "vue", "name": "Vue" },
      { "slug": "performance", "name": "性能优化" }
    ],
    "readingTime": 5,
    "coverLabel": "2026.10",
    "content": "## 现象：滚动时掉帧\n\n一个包含 600 条记录的列表页……"
  }
}
```

**404 约定**

- 文章不存在或未发布时，为了配合前端“文章不存在”的页面状态，返回 **HTTP 200 + `data: null`**，不返回错误码
- 前端逻辑为 `if (detail) { ... } else { 展示 404 状态 }`

---

### 3.8 获取上下篇

`GET /api/posts/{slug}/adjacent`

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "prev": {
      "slug": "css-grid-layout",
      "title": "用 CSS Grid 重建博客布局",
      "publishedAt": "2026-09-26",
      "category": { "slug": "frontend", "name": "前端开发", "description": "……" },
      "tags": [{ "slug": "css", "name": "CSS" }],
      "readingTime": 3,
      "coverLabel": "2026.09"
    },
    "next": {
      "slug": "springboot-api-design",
      "title": "Spring Boot 接口设计：让前后端少吵三次架",
      "publishedAt": "2026-10-12",
      "readingTime": 4,
      "coverLabel": "2026.10"
    }
  }
}
```

**说明**

- `prev` 表示更早的一篇，`next` 表示更新的一篇（以发布日期排序）
- 到达边界时对应字段为 `null`
- 文章不存在时返回 `{ "prev": null, "next": null }`
- 元素结构为 `PostSummary`

---

### 3.9 增加阅读量

`POST /api/posts/{slug}/view`

**说明**

- 同一访客（Cookie 或 IP）对同一文章 **30 分钟内只计一次**
- 返回本次是否计入阅读量

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": true
}
```

| data | 含义 |
| --- | --- |
| true | 本次已计入，前端阅读量 +1 |
| false | 30 分钟内重复访问，未计入 |

**错误**

- `2001` / 404：文章不存在

---

### 3.10 获取文章评论

`GET /api/posts/{slug}/comments`

**说明**

- 只返回 `status = approved` 的评论
- 按 `createdAt` 倒序
- 不返回邮箱

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 3,
      "nickname": "K",
      "content": "CSS 变量这个思路很好……",
      "createdAt": "2026-10-23 14:20",
      "reply": {
        "content": "用 requestAnimationFrame 做节流……",
        "createdAt": "2026-10-23 18:02"
      }
    },
    {
      "id": 4,
      "nickname": "小满",
      "content": "Performance 面板确实比猜靠谱……",
      "createdAt": "2026-10-21 11:05",
      "reply": null
    }
  ]
}
```

**错误**

- `2001` / 404：文章不存在

---

### 3.11 提交文章评论

`POST /api/posts/{slug}/comments`

**请求体**

```json
{
  "slug": "vue3-performance",
  "nickname": "阿澈",
  "email": "hi@example.com",
  "content": "写得很清楚，学到了。"
}
```

| 字段 | 类型 | 必填 | 校验规则 |
| --- | --- | --- | --- |
| slug | string | 是 | 与路径一致；后端以路径参数为准 |
| nickname | string | 是 | 2—20 个字符，去除首尾空格 |
| email | string | 否 | 邮箱格式，长度 ≤ 100，仅入库不公开 |
| content | string | 是 | 去除首尾空格后 5—500 字；纯文本，禁止 HTML |

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "status": "pending"
  }
}
```

| status | 含义 |
| --- | --- |
| pending | 提交成功，等待审核（`features.review = true` 时） |
| approved | 提交成功，立即公开（`features.review = false` 时） |

**校验与限制**

- `features.comments = false` → `3001` / 403
- 同一 IP 对同一文章 60 秒内重复提交 → `1005` / 429
- 参数错误 → `1001` / 400
- 文章不存在或未发布 → `2001` / 404

---

### 3.12 获取留言列表

`GET /api/messages`

**查询参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页数量，最大 50 |

**说明**

- 只返回 `status = approved` 的留言
- 按 `createdAt` 倒序
- 返回分页结构

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": 1,
        "nickname": "阿远",
        "content": "从 RSS 过来的，文章排版很舒服……",
        "createdAt": "2026-10-27 19:20",
        "reply": {
          "content": "从后台读的，前台只认已发布的分类。",
          "createdAt": "2026-10-28 08:40"
        }
      }
    ],
    "total": 4,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 3.13 提交留言

`POST /api/messages`

**请求体**

```json
{
  "nickname": "木木",
  "email": "hi@example.com",
  "content": "第一次来，博客的暗色配色看着很舒服。"
}
```

| 字段 | 类型 | 必填 | 校验规则 |
| --- | --- | --- | --- |
| nickname | string | 是 | 2—20 个字符，去除首尾空格 |
| email | string | 否 | 邮箱格式，长度 ≤ 100，仅入库不公开 |
| content | string | 是 | 去除首尾空格后 5—300 字；纯文本，禁止 HTML |

> 前端提交留言时也会带上 `slug`（空字符串），后端忽略该字段。

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "status": "pending"
  }
}
```

**校验与限制**

- `features.guestbook = false` → `3002` / 403
- 同一 IP 60 秒内重复提交 → `1005` / 429
- 参数错误 → `1001` / 400

---

### 3.14 获取友链

`GET /api/links`

**说明**

- 只返回 `visible = true` 的友链
- 按 `order` 升序
- `domain` 用于展示，`avatarText` 为头像占位文字

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Overreacted",
      "url": "https://overreacted.io/",
      "domain": "overreacted.io",
      "avatarText": "O",
      "description": "Dan Abramov 的博客，关于 React、抽象与学习方式的长文。",
      "order": 1,
      "visible": true
    }
  ]
}
```

---

### 3.15 获取归档

`GET /api/archive`

**说明**

- 只统计已发布文章
- 按年份倒序，年份内按月份倒序
- `items` 为 `PostSummary` 数组

**响应示例**

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "year": "2026",
      "count": 8,
      "months": [
        {
          "month": "10",
          "label": "10 月",
          "items": [
            {
              "slug": "hello-blog-v2",
              "title": "从一个想法到一篇可读的文章",
              "publishedAt": "2026-10-28",
              "category": { "slug": "notes", "name": "随笔", "description": "……" },
              "readingTime": 4,
              "coverLabel": "2026.10"
            }
          ]
        },
        {
          "month": "09",
          "label": "9 月",
          "items": []
        }
      ]
    }
  ]
}
```

---

## 4. 接口清单

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| GET | `/api/site` | 站点配置 | 否 |
| GET | `/api/profile` | 博主信息 | 否 |
| GET | `/api/categories` | 分类列表 | 否 |
| GET | `/api/tags` | 标签列表 | 否 |
| GET | `/api/posts` | 文章列表 / 搜索 | 否 |
| GET | `/api/posts/latest` | 最新文章 | 否 |
| GET | `/api/posts/{slug}` | 文章详情 | 否 |
| GET | `/api/posts/{slug}/adjacent` | 上下篇 | 否 |
| POST | `/api/posts/{slug}/view` | 阅读量 +1 | 否 |
| GET | `/api/posts/{slug}/comments` | 文章评论列表 | 否 |
| POST | `/api/posts/{slug}/comments` | 提交评论 | 否 |
| GET | `/api/messages` | 留言列表 | 否 |
| POST | `/api/messages` | 提交留言 | 否 |
| GET | `/api/links` | 友链列表 | 否 |
| GET | `/api/archive` | 归档 | 否 |

---

## 5. 待补充接口（后续迭代）

本期只覆盖前台公开接口。以下接口待管理端页面确定后再设计：

- 管理员登录 / 登出 / 会话校验
- 文章 CRUD 与发布、下线、置顶
- 分类、标签、友链、站点配置维护
- 评论与留言审核、回复、删除
- 图片上传

---

## 6. 前端联调注意

1. 关闭 Mock：在 `blog-frontend` 下配置环境变量 `VUE_APP_USE_MOCK=false`，并把 `VUE_APP_API_BASE` 指向后端，例如 `http://localhost:8080/api`。
2. 跨域：前端使用 `credentials: 'include'`，后端 CORS 需允许具体来源（如 `http://localhost:8080` 之外的 `http://localhost:8081`）并开启 `allowCredentials`。
3. 成功响应 `code` 必须为 `0`，否则前端会当作失败处理。
4. `GET /api/posts/{slug}` 未找到时返回 `data: null`，前端据此展示“文章不存在”页面。
5. 阅读量接口返回布尔值：`true` 才让前端把数字 +1。
