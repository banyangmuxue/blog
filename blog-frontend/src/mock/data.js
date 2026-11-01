const demoCover = (slug) => `${process.env.BASE_URL}demo/covers/${slug}.jpg`

export const siteConfig = {
  name: 'B/ 记录代码与生活',
  shortName: 'B/',
  title: '记录代码与生活',
  subtitle: '前端、后端与持续学习 — 一个个人博客',
  description:
    '这里记录我在前端工程、后端服务与日常学习中的思考：怎么把页面写得更稳，怎么让接口更清楚，以及怎么把读过的东西留下来。',
  author: '林深',
  announcement: '博客已升级到 v2，评论与留言默认先审后发，感谢你的耐心。',
  footerText: '用代码记录思考，用文字整理生活。',
  icp: '京 ICP 备 2026000000 号',
  foundedAt: '2023-06',
  socials: [
    { label: 'GitHub', url: 'https://github.com/' },
    { label: 'RSS', url: '/archive' },
    { label: 'Email', url: 'mailto:hi@example.com' }
  ],
  features: {
    comments: true,
    review: true,
    guestbook: true,
    search: true
  }
}

export const profile = {
  name: '林深',
  avatarText: 'L',
  bio: '写代码，也写字。关注前端工程化与后端服务设计。',
  skills: ['Vue', 'TypeScript', 'Spring Boot', '写作'],
  socials: [
    { label: 'GitHub', url: 'https://github.com/' },
    { label: 'Email', url: 'mailto:hi@example.com' }
  ],
  joinedAt: '2023-06',
  about: `
## 我是谁

我是林深，一名在做前端、也在补后端的开发者。白天写 Vue 与 TypeScript，晚上读一点 Java 和数据库，剩下的时间用来把学到的东西写成文章。

这个博客从 2023 年开始，中间停更过两次。现在重新整理成 v2，希望它更像一个可以长期维护的地方，而不是一次性的作品集。

## 我在做什么

- 维护一套基于 Vue 3 与 Spring Boot 的个人内容系统
- 把性能优化、接口设计这类"说起来都对"的话题拆成可以验证的步骤
- 每月至少写一篇长文，记录一次真实的踩坑过程

## 我关注的方向

### 前端工程化

比起追新，我更关心一套代码在半年后还能不能被自己读懂。组件边界、状态管理、样式组织，这些看起来琐碎的决定，往往比框架版本更能影响项目的寿命。

### 后端服务设计

一个人的项目也需要清楚的接口约定。我在文章里反复提到的一件事是：前后端分离的收益，来自双方都愿意把规则写下来。

### 写作与阅读

写作是整理思考最便宜的方式。读过的书、用过的工具、做错的决定，只要能说清楚一次，就值得留下来。

## 联系方式

如果你也在做类似的事情，欢迎在留言板打个招呼，或者通过侧栏的邮箱联系我。评论与留言我都会看，只是回复可能慢一点。
`
}

export const categories = [
  {
    slug: 'frontend',
    name: '前端开发',
    description: 'Vue、TypeScript、CSS 与性能优化，关于把界面写稳这件事。'
  },
  {
    slug: 'backend',
    name: '后端笔记',
    description: 'Spring Boot、接口设计与数据持久化的实践记录。'
  },
  {
    slug: 'notes',
    name: '随笔',
    description: '读书、写作与日常，和技术无关但同样重要的部分。'
  },
  {
    slug: 'engineering',
    name: '工程实践',
    description: '构建、调试、协作与长期维护，一个人的工程化。'
  }
]

export const tags = [
  { slug: 'vue', name: 'Vue' },
  { slug: 'typescript', name: 'TypeScript' },
  { slug: 'css', name: 'CSS' },
  { slug: 'performance', name: '性能优化' },
  { slug: 'spring-boot', name: 'Spring Boot' },
  { slug: 'java', name: 'Java' },
  { slug: 'database', name: '数据库' },
  { slug: 'writing', name: '写作' },
  { slug: 'reading', name: '阅读' },
  { slug: 'tools', name: '工具' }
]

export const posts = [
  {
    id: 1,
    slug: 'hello-blog-v2',
    title: '从一个想法到一篇可读的文章',
    excerpt:
      '写博客最难的不是开始，而是让每一篇都值得被读完。这篇文章记录我把一个模糊想法整理成可发布内容的步骤。',
    categorySlug: 'notes',
    tagSlugs: ['writing', 'tools'],
    publishedAt: '2026-10-28',
    updatedAt: '2026-10-29',
    views: 1204,
    pinned: true,
    status: 'published',
    cover: { from: '#123043', to: '#2a1b4d', image: demoCover('hello-blog-v2') },
    content: `
## 先写一句能被反驳的话

很多文章写不下去，是因为一开始就想写"全面"。全面意味着没有重点，而读者只会记住一句最清楚的话。

我的做法是先把核心观点压成一句话，要求它足够具体、足够容易被反驳。比如"性能优化应该先测量再动手"，就比"要重视性能"更值得展开。

## 把想法拆成三个问题

一句话观点之后，我会问自己三个问题：

1. 这个问题在什么场景下真实发生过？
2. 我当时做错了什么，或者做对了什么？
3. 读者照做时，第一个会遇到的阻碍是什么？

这三个问题通常对应文章的三段结构：场景、过程、结论。它们也决定了文章需不需要示例代码。

## 写正文时只做一件事

正文阶段不再调整结构，只负责把每一段说清楚。我的习惯是：

- 每段只讲一个点，超过四行就拆段
- 代码块前后各留一句解释，不贴"裸代码"
- 小标题用陈述句，不用问句制造悬念

> 如果一段话自己读两遍还不顺，通常不是措辞问题，而是想法还没想清楚。

## 发布前做一遍删减

初稿完成后放半天，再回来删三样东西：重复的结论、没有支撑的形容词、为了显得专业而加的名词。删完之后，文章通常短了三成，但可读性会明显提升。

## 留一个可执行的结尾

结尾不必升华。给读者一个具体的下一步，比如"下次遇到渲染卡顿，先打开 Performance 面板录一段"，比"希望对你有帮助"更有用。

写作是整理思考的过程。文章写完，受益的第一个人其实是自己。
`
  },
  {
    id: 2,
    slug: 'vue3-performance',
    title: 'Vue 3 渲染性能：先测量，再优化',
    excerpt:
      '一次列表页卡顿的排查记录：从 Performance 面板到 Vue 响应式依赖，最后只改了三行代码。',
    categorySlug: 'frontend',
    tagSlugs: ['vue', 'performance'],
    publishedAt: '2026-10-20',
    updatedAt: '2026-10-22',
    views: 986,
    pinned: true,
    status: 'published',
    cover: { from: '#0d3b46', to: '#101a3f', image: demoCover('vue3-performance') },
    content: `
## 现象：滚动时掉帧

一个包含 600 条记录的列表页，在快速滚动时帧率从 60 掉到 30 左右。第一反应是"列表太长"，但把列表砍到 200 条后问题依旧存在。

这说明瓶颈不在数量，而在更新方式。

## 用 Performance 面板定位

录制一段滚动操作后，主要耗时出现在 Recalculate Style 与 Layout，而不是 JavaScript。继续看调用栈，发现每次滚动都会触发一次组件更新。

原因是滚动监听里读取了一个响应式变量，又把它写回了另一个响应式变量，形成了不必要的依赖链。

~~~js
// 优化前：滚动时不断写入响应式状态
const offset = ref(0)
window.addEventListener('scroll', () => {
  offset.value = window.scrollY
  activeIndex.value = Math.floor(offset.value / itemHeight)
})
~~~

## 三个改动

### 一、把高频状态移出响应式系统

滚动位置只需要驱动一个 CSS 变量，不需要参与 Vue 的依赖收集。

~~~js
const root = ref(null)
window.addEventListener('scroll', () => {
  root.value?.style.setProperty('--scroll-y', window.scrollY + 'px')
}, { passive: true })
~~~

### 二、给监听加上 passive

滚动监听默认会阻塞浏览器优化。加上 \`{ passive: true }\` 后，浏览器不再等待回调执行完毕才滚动。

### 三、只在需要时计算可见区间

列表虚拟化不是必须的，但"哪几条可见"应该按需计算，而不是每次滚动都全量遍历。

## 结果与边界

三处改动后，帧率回到 55—60。这个案例的结论并不复杂：**先确认耗时发生在哪一类任务，再决定改代码的哪一层**。上来就加虚拟列表，可能只是把问题藏起来。

另外要注意，CSS 变量方案在需要参与计算的场景并不适用；如果滚动位置还要驱动数据请求，仍然需要响应式状态，只是应该做节流。
`
  },
  {
    id: 3,
    slug: 'springboot-api-design',
    title: 'Spring Boot 接口设计：让前后端少吵三次架',
    excerpt:
      '统一响应结构、分页参数、错误码与时间格式，这四件事在项目开始前定好，能省掉大量返工。',
    categorySlug: 'backend',
    tagSlugs: ['spring-boot', 'java'],
    publishedAt: '2026-10-12',
    updatedAt: '2026-10-14',
    views: 842,
    pinned: true,
    status: 'published',
    cover: { from: '#1b2b4d', to: '#0c333a', image: demoCover('springboot-api-design') },
    content: `
## 约定比实现更重要

前后端分离的项目里，最早发生的争论通常不是业务逻辑，而是"这个接口返回什么"。把下面四件事提前写进文档，能避免大部分反复。

## 一、统一响应结构

成功与失败都使用同一层外壳，前端只需要写一次解包逻辑。

~~~json
{
  "code": 0,
  "message": "ok",
  "data": { "items": [], "total": 0 }
}
~~~

业务错误使用非零 code，HTTP 状态码仍然保持语义：参数错误 400，未登录 401，无权限 403，不存在 404。

## 二、分页参数固定

统一使用 \`page\` 从 1 开始、\`size\` 默认 10、上限 50。返回体固定包含 \`items\`、\`total\`、\`page\`、\`size\`、\`pages\`。

不要在有的接口用 offset，有的用 cursor。前端记不住，测试也覆盖不全。

## 三、错误码按领域分段

- 1xxx：通用参数与权限
- 2xxx：内容相关
- 3xxx：互动相关

错误码只用于程序判断，展示文案由前端决定。后端返回的 message 面向开发者，不直接展示给访客。

## 四、时间统一 ISO 8601

数据库存 UTC，接口输出带时区的 ISO 字符串，前端按用户时区格式化。不要在接口里返回 "2026-10-12 08:00" 这种没有时区的时间。

## 写进文档，而不是记在脑子里

这四条规则最适合放在接口文档的首页。它们的价值不在于"更规范"，而在于出现分歧时，双方都有一个不需要争输赢的参照。
`
  },
  {
    id: 4,
    slug: 'css-grid-layout',
    title: '用 CSS Grid 重建博客布局',
    excerpt:
      '从 float 到 flex 再到 grid，同一套博客布局写了三遍，这次终于把侧栏与正文的关系说清楚了。',
    categorySlug: 'frontend',
    tagSlugs: ['css'],
    publishedAt: '2026-09-26',
    updatedAt: '2026-09-26',
    views: 630,
    pinned: false,
    status: 'published',
    cover: { from: '#153a3a', to: '#241a3f', image: demoCover('css-grid-layout') },
    content: `
## 为什么换成 Grid

博客布局的核心关系只有一句话：正文优先，侧栏跟随。用 flex 实现时，侧栏宽度、间距和换行顺序都要靠额外规则维持；换成 grid 之后，这些关系可以直接写进模板。

## 两套模板

列表页是"内容 + 侧栏"，详情页是"正文 + 目录"。两者都可以用同一组断点：

~~~css
.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 240px;
  gap: 40px;
}

@media (max-width: 1023px) {
  .layout {
    grid-template-columns: minmax(0, 1fr);
  }
}
~~~

\`minmax(0, 1fr)\` 很关键。默认的 \`1fr\` 在遇到长代码块时会被撑开，加上最小值 0 才能让内部滚动生效。

## 目录的吸顶

目录列使用 \`position: sticky\`，但父级不能有 \`overflow: hidden\`，否则吸顶会失效。这个问题我排查了半小时，最后发现是卡片上的圆角裁切。

## 空列与顺序

移动端把侧栏放到正文之后，只需要改一处 \`order\`，不需要改 DOM 顺序。这也是 Grid 比 flex 更适合页面级布局的原因：视觉顺序和结构顺序可以分开。

## 小结

Grid 不是"更高级的 flex"，它解决的是二维关系。页面级布局用它，组件内部仍然可以用 flex。两者混用不是妥协，而是分清职责。
`
  },
  {
    id: 5,
    slug: 'typescript-patterns',
    title: 'TypeScript 里几个让代码更安静的模式',
    excerpt:
      '不是类型体操，而是几个让调用方少猜、让报错更早出现的小习惯。',
    categorySlug: 'frontend',
    tagSlugs: ['typescript'],
    publishedAt: '2026-09-12',
    updatedAt: '2026-09-15',
    views: 512,
    pinned: false,
    status: 'published',
    cover: { from: '#1c2b45', to: '#3a1c45', image: demoCover('typescript-patterns') },
    content: `
## 让非法状态无法表示

比起在运行时反复判断，更好的方式是从类型上让非法组合无法出现。

~~~ts
type LoadState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: Post[] }
  | { status: 'error'; message: string }
~~~

这样在使用 \`data\` 之前，类型系统会强制你先判断 \`status === 'success'\`。

## 用 satisfies 保留字面量

\`as const\` 会把所有字段变成只读，而 \`satisfies\` 只做检查，不改变推断结果。

~~~ts
const routes = {
  home: '/',
  articles: '/articles'
} satisfies Record<string, string>
~~~

## 返回值显式标注

公共函数尽量写返回类型。它不是为了啰嗦，而是防止实现改动时悄悄改变对外契约。

## 别把 any 当逃生舱

临时用 \`unknown\`，再在边界处收窄，比 \`any\` 更容易在后续排查中找到问题。真正需要关闭检查的地方，用注释说明原因。

## 安静不等于严格

这些模式的共同点是：让错误在编译期出现，让调用方不需要读实现。类型不是目的，减少沟通成本才是。
`
  },
  {
    id: 6,
    slug: 'jpa-pagination',
    title: 'JPA 分页的三种写法与它们的代价',
    excerpt:
      'Page、Slice 与自定义查询，三种分页方式在不同数据量下的表现差异，以及我最终的选择。',
    categorySlug: 'backend',
    tagSlugs: ['java', 'database'],
    publishedAt: '2026-08-30',
    updatedAt: '2026-09-02',
    views: 468,
    pinned: false,
    status: 'published',
    cover: { from: '#243018', to: '#0f2c3a', image: demoCover('jpa-pagination') },
    content: `
## 三种常见写法

- \`Page<T>\`：返回内容与总数，会额外执行一次 count 查询
- \`Slice<T>\`：只判断是否还有下一页，不查总数
- 自定义查询：手写 count 与列表，适合复杂条件

## Page 的代价

\`Page\` 用起来最方便，但每次请求都会多一次 count。数据量小的时候无感，数据量大、条件复杂时，count 可能比列表查询还慢。

博客的文章列表需要显示总数，所以我保留了 \`Page\`，但把 count 限定在已发布状态，并给常用筛选字段加了索引。

## Slice 适合无限滚动

前台首页的"加载更多"不需要总数，用 \`Slice\` 可以少一次查询。代价是前端不能显示页码，只能显示"还有更多"。

## 自定义查询的边界

当筛选条件涉及多表关联时，JPA 的派生查询会变得难以阅读。这时手写 JPQL 或原生 SQL 更清楚，但要自己保证 count 与列表的条件完全一致。

## 选择标准

先问页面需不需要总数，再问条件复不复杂。两个问题都有答案之后，分页方式其实不需要争论。
`
  },
  {
    id: 7,
    slug: 'reading-2026',
    title: '上半年读过的五本书',
    excerpt:
      '技术书两本、非技术书三本。它们没有直接教我写代码，但改变了我看待工作的方式。',
    categorySlug: 'notes',
    tagSlugs: ['reading'],
    publishedAt: '2026-06-18',
    updatedAt: '2026-06-18',
    views: 390,
    pinned: false,
    status: 'published',
    cover: { from: '#3a2a18', to: '#1a2340', image: demoCover('reading-2026') },
    content: `
## 两本技术书

《Designing Data-Intensive Applications》读得很慢，但值得。它让我第一次把"数据库"当成一组权衡，而不是一个需要背下来的产品。

另一本是《Refactoring》。读完之后最大的变化不是会用多少重构手法，而是开始愿意在提交前多看一眼命名。

## 三本非技术书

《写作这回事》解决了我对"灵感"的迷信。写作是可以安排的工作，不是等状态。

《深度工作》让我把上午的两小时设为不可打扰，效果比任何工具都明显。

《置身事内》让我对"系统如何运转"多了一点耐心，也更愿意接受一些不完美的现实。

## 读书笔记的方式

我不再摘抄整段，只记三类东西：一个改变我行为的观点、一个可以验证的方法、一个我不同意的结论。三条都写不出来，说明这本书暂时和我无关。

## 下一步

下半年想补一点数学基础，也想重读一遍《计算机程序的构造和解释》。读不完也没关系，重要的是别停下来。
`
  },
  {
    id: 8,
    slug: 'build-in-public',
    title: '公开构建：把半成品也拿出来',
    excerpt:
      '等做完再发布，通常意味着永远不会发布。公开构建不是展示成果，而是暴露过程。',
    categorySlug: 'notes',
    tagSlugs: ['writing', 'tools'],
    publishedAt: '2026-04-05',
    updatedAt: '2026-04-07',
    views: 322,
    pinned: false,
    status: 'published',
    cover: { from: '#1a2c3f', to: '#33203d', image: demoCover('build-in-public') },
    content: `
## 完美主义的成本

一个功能做完八成再公开，听起来稳妥，实际上把反馈推迟到了最难改的时候。公开构建把节奏提前：先让一小部分人看到，再决定往哪里投入。

## 公开什么

不是所有过程都值得公开。我通常只公开三类内容：

- 已经能跑通的最小版本
- 明确知道哪里还不对的地方
- 下一步准备怎么改

计划、情绪和未经整理的想法不在此列。

## 公开的代价

会有人看到你的半成品，也会有人给出你已经知道的建议。接受这两件事，公开构建才有意义。

## 一个小实践

这个博客的评论系统就是先上线、再补审核的。第一版只支持留言，没有回复；两周后才加上博主回复。如果等全部做完，可能现在还停在设计稿上。

公开构建的核心不是勇气，而是把"完成"重新定义成"可以继续"。
`
  },
  {
    id: 9,
    slug: 'http-cache',
    title: 'HTTP 缓存：从 Cache-Control 说起',
    excerpt:
      '强缓存、协商缓存与 CDN 的分层策略，用一张请求时间线把它们串起来。',
    categorySlug: 'frontend',
    tagSlugs: ['performance'],
    publishedAt: '2025-11-22',
    updatedAt: '2025-11-25',
    views: 705,
    pinned: false,
    status: 'published',
    cover: { from: '#12303f', to: '#2c1a42', image: demoCover('http-cache') },
    content: `
## 缓存解决的是什么

缓存不是"让请求变快"，而是"让请求不要发生"。理解这句话，才能理解强缓存与协商缓存的区别。

## 强缓存

\`Cache-Control: max-age=31536000, immutable\` 表示在一年内直接使用本地副本，不再询问服务器。适合文件名带哈希的静态资源。

~~~http
Cache-Control: public, max-age=31536000, immutable
~~~

## 协商缓存

HTML 文件不适合长缓存，因为它决定了引用哪个版本的资源。使用 \`no-cache\` 配合 ETag，让浏览器每次询问，但内容没变时只返回 304。

~~~http
Cache-Control: no-cache
ETag: "a1b2c3"
~~~

## 分层策略

一个常见的组合：

| 资源 | 策略 |
| --- | --- |
| HTML | no-cache + ETag |
| JS / CSS（带哈希） | 一年强缓存 + immutable |
| 图片 | 30 天强缓存 |
| 接口 | 按业务决定，默认 no-store |

## 容易忽略的两件事

第一，\`no-store\` 和 \`no-cache\` 不是一回事。第二，CDN 的缓存键如果不包含查询参数，带参数的请求可能互相覆盖。

缓存配置的收益很高，但错误也更隐蔽。上线前用浏览器网络面板确认一次命中情况，比事后猜测可靠。
`
  },
  {
    id: 10,
    slug: 'spring-security-session',
    title: '单管理员博客的会话与登录保护',
    excerpt:
      '不需要复杂的权限体系，但登录失败锁定、会话失效与 CSRF 防护一个都不能少。',
    categorySlug: 'backend',
    tagSlugs: ['spring-boot', 'java'],
    publishedAt: '2025-08-16',
    updatedAt: '2025-08-18',
    views: 611,
    pinned: false,
    status: 'published',
    cover: { from: '#1d2945', to: '#123a33', image: demoCover('spring-security-session') },
    content: `
## 单账号也需要认真对待

只有一个管理员，不代表可以省掉安全设计。相反，后台是所有内容的入口，一旦被攻破，损失比普通账号更大。

## 会话策略

后台会话 30 分钟无操作失效。前端在收到 401 后跳转登录页，并保留原路径，登录成功后回到原页面。

## 登录失败锁定

连续 5 次失败锁定 10 分钟，锁定期间不校验密码，直接返回剩余时间。这里要注意：错误信息不能区分"账号不存在"和"密码错误"。

## CSRF 与跨域

前后端分离时，如果使用 Cookie 会话，就需要 CSRF token 或 SameSite 策略。我的选择是：

- Cookie 设置 \`HttpOnly\`、\`SameSite=Lax\`
- 写操作要求自定义请求头
- 后端只允许配置过的来源跨域

## 不要自己发明加密

密码使用成熟框架提供的编码器，不手写哈希。会话存储使用服务端 Session，不把用户信息塞进前端可读的 Token。

这些做法都不复杂，难的是在项目初期就决定好，而不是等上线前再补。
`
  },
  {
    id: 11,
    slug: 'first-post',
    title: '第一篇：为什么又重新开始写博客',
    excerpt:
      '第三次开始写博客。这次不追求更新频率，只想把值得留下的东西写清楚。',
    categorySlug: 'notes',
    tagSlugs: ['writing', 'reading'],
    publishedAt: '2024-12-01',
    updatedAt: '2024-12-03',
    views: 804,
    pinned: false,
    status: 'published',
    cover: { from: '#2b2438', to: '#13303a', image: demoCover('first-post') },
    content: `
## 前两次为什么停了

第一次是因为想写"大文章"，结果每篇都写不完。第二次是因为在意阅读量，写着写着开始迎合并不存在的读者。

第三次开始之前，我给自己定了两条规则：每篇不超过一次完整的思考；发布前不预测数据。

## 写什么

技术问题的排查过程、读过的书、做过的决定。它们共同点是：过一段时间回看，仍然有用。

## 怎么写

用 Markdown，存在自己的数据库里，不依赖任何平台。排版保持简单：标题、段落、代码块，偶尔一张图。

## 更新频率

不设固定频率。有想清楚的事就写，没有就空着。博客不是产品，不需要日活。

## 期待

如果这些文章能让未来的自己少踩一次坑，或者让某个陌生人少走一段弯路，就已经足够。
`
  },
  {
    id: 12,
    slug: 'dev-env-2024',
    title: '我的开发环境清单',
    excerpt:
      '编辑器、终端、浏览器插件与几个长期使用的工具，以及它们解决的具体问题。',
    categorySlug: 'engineering',
    tagSlugs: ['tools'],
    publishedAt: '2024-06-20',
    updatedAt: '2024-06-22',
    views: 566,
    pinned: false,
    status: 'published',
    cover: { from: '#23303c', to: '#2a1c36', image: demoCover('dev-env-2024') },
    content: `
## 编辑器

VS Code 配 Volar、ESLint、Prettier。主题使用暗色，字号 15，行高 1.7。插件只留常用的，避免启动变慢。

## 终端

Windows Terminal + PowerShell 7，字体使用等宽连字字体。常用命令做了几个别名，减少重复输入。

## 浏览器

Chrome 开发者工具是主要工作区，常用面板固定为 Elements、Console、Network、Performance。插件只装了三类：广告拦截、JSON 格式化、颜色取色。

## 笔记与任务

笔记用 Markdown 文件，按项目分目录，不做复杂标签。任务用一张简单的清单，每天只保留三件事。

## 一条原则

工具的作用是减少摩擦，不是增加仪式感。如果一个工具需要每周维护，那它大概率不值得留下。
`
  }
]

export const comments = [
  {
    id: 1,
    slug: 'hello-blog-v2',
    nickname: '阿澈',
    content: '“先写一句能被反驳的话”这个标准很实用，准备用到自己的周报里。',
    createdAt: '2026-10-29 09:12',
    status: 'approved',
    reply: {
      content: '周报里尤其有效，能逼自己删掉一半形容词。',
      createdAt: '2026-10-29 10:05'
    }
  },
  {
    id: 2,
    slug: 'hello-blog-v2',
    nickname: 'momo',
    content: '请问发布前的删减一般会删到什么程度？我总是舍不得。',
    createdAt: '2026-10-28 21:40',
    status: 'approved',
    reply: null
  },
  {
    id: 3,
    slug: 'vue3-performance',
    nickname: 'K',
    content: 'CSS 变量这个思路很好，但滚动位置需要触发请求时还是要响应式，作者有推荐的做法吗？',
    createdAt: '2026-10-23 14:20',
    status: 'approved',
    reply: {
      content: '用 requestAnimationFrame 做节流，再把请求结果放进响应式状态，滚动位置本身仍然留在 DOM 层。',
      createdAt: '2026-10-23 18:02'
    }
  },
  {
    id: 4,
    slug: 'vue3-performance',
    nickname: '小满',
    content: 'Performance 面板确实比猜靠谱，之前一直以为是数据量的问题。',
    createdAt: '2026-10-21 11:05',
    status: 'approved',
    reply: null
  },
  {
    id: 5,
    slug: 'springboot-api-design',
    nickname: 'Lee',
    content: '时间统一 ISO 8601 这条太重要了，我们项目现在还在传时间戳。',
    createdAt: '2026-10-13 16:33',
    status: 'approved',
    reply: null
  },
  {
    id: 6,
    slug: 'http-cache',
    nickname: 'Nora',
    content: '想看 CDN 缓存键那部分的展开，尤其是带查询参数的接口。',
    createdAt: '2025-11-24 10:18',
    status: 'approved',
    reply: null
  }
]

export const messages = [
  {
    id: 1,
    nickname: '阿远',
    content: '从 RSS 过来的，文章排版很舒服。想问侧栏的分类是手写还是从后台读的？',
    createdAt: '2026-10-27 19:20',
    status: 'approved',
    reply: {
      content: '从后台读的，前台只认已发布的分类。',
      createdAt: '2026-10-28 08:40'
    }
  },
  {
    id: 2,
    nickname: '木木',
    content: '第一次来，博客的暗色配色看着很舒服，代码块也清楚。',
    createdAt: '2026-10-24 15:02',
    status: 'approved',
    reply: null
  },
  {
    id: 3,
    nickname: 'Chen',
    content: '请问会写 Spring Security 的完整配置示例吗？想跟着做一遍。',
    createdAt: '2026-10-18 09:45',
    status: 'approved',
    reply: {
      content: '在计划里，会配合最小可运行的项目一起写。',
      createdAt: '2026-10-19 20:10'
    }
  },
  {
    id: 4,
    nickname: '路人甲',
    content: '路过，留个脚印。祝博客越写越好。',
    createdAt: '2026-10-10 22:31',
    status: 'approved',
    reply: null
  }
]

export const links = [
  {
    id: 1,
    name: 'Overreacted',
    url: 'https://overreacted.io/',
    domain: 'overreacted.io',
    avatarText: 'O',
    description: 'Dan Abramov 的博客，关于 React、抽象与学习方式的长文。',
    order: 1,
    visible: true
  },
  {
    id: 2,
    name: 'Julia Evans',
    url: 'https://jvns.ca/',
    domain: 'jvns.ca',
    avatarText: 'J',
    description: '用漫画和直白的语言解释调试、网络与系统知识。',
    order: 2,
    visible: true
  },
  {
    id: 3,
    name: '阮一峰的网络日志',
    url: 'https://www.ruanyifeng.com/blog/',
    domain: 'ruanyifeng.com',
    avatarText: '阮',
    description: '每周科技爱好者周刊，长期稳定的中文技术写作。',
    order: 3,
    visible: true
  },
  {
    id: 4,
    name: 'CSS-Tricks',
    url: 'https://css-tricks.com/',
    domain: 'css-tricks.com',
    avatarText: 'C',
    description: '前端布局与 CSS 技巧的资料库，遇到问题常来查。',
    order: 4,
    visible: true
  },
  {
    id: 5,
    name: 'Martin Fowler',
    url: 'https://martinfowler.com/',
    domain: 'martinfowler.com',
    description: '软件设计与重构的经典文章集合，值得慢慢读。',
    order: 5,
    visible: true
  }
]

export const viewLog = {}
