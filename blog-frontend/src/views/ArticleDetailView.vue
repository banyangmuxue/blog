<template>
  <div class="detail page-enter">
    <div v-if="loading" class="container skeleton-page">
      <div class="skeleton line w30"></div>
      <div class="skeleton line title"></div>
      <div class="skeleton line w50"></div>
      <div class="skeleton block"></div>
      <div class="skeleton block short"></div>
    </div>

    <div v-else-if="error" class="container">
      <div class="state-box">
        <h2>文章加载失败</h2>
        <p>{{ error }}</p>
        <div class="state-actions">
          <button type="button" class="btn primary" @click="load">重新加载</button>
          <router-link to="/articles" class="btn ghost">返回文章列表</router-link>
        </div>
      </div>
    </div>

    <div v-else-if="!post" class="container">
      <div class="state-box">
        <span class="code mono">404</span>
        <h2>这篇文章不存在</h2>
        <p>它可能已下线或被删除。你可以回到文章列表，或者搜索其他内容。</p>
        <div class="state-actions">
          <router-link to="/articles" class="btn primary">浏览文章</router-link>
          <router-link to="/search" class="btn ghost">搜索</router-link>
        </div>
      </div>
    </div>

    <template v-else>
      <div class="container">
        <nav class="crumbs" aria-label="面包屑">
          <router-link to="/">首页</router-link>
          <span aria-hidden="true">/</span>
          <router-link to="/articles">文章</router-link>
          <span aria-hidden="true">/</span>
          <router-link v-if="post.category" :to="`/categories/${post.category.slug}`">
            {{ post.category.name }}
          </router-link>
          <span v-if="post.category" aria-hidden="true">/</span>
          <span class="current">{{ post.title }}</span>
        </nav>

        <div class="article-layout">
          <article class="article-main">
            <header class="article-head">
              <router-link
                v-if="post.category"
                :to="`/categories/${post.category.slug}`"
                class="category"
              >
                {{ post.category.name }}
              </router-link>
              <h1>{{ post.title }}</h1>
              <p class="excerpt">{{ post.excerpt }}</p>

              <div class="meta">
                <span class="author">
                  <span class="avatar">
                    <img
                      v-if="site.profile?.avatarUrl"
                      :src="site.profile.avatarUrl"
                      :alt="site.profile?.name || '博主头像'"
                    />
                    <template v-else>{{ authorInitial }}</template>
                  </span>
                  {{ site.profile?.name || site.config.author || '博主' }}
                </span>
                <span class="sep" aria-hidden="true">·</span>
                <time :datetime="post.publishedAt">{{ formatDate(post.publishedAt) }}</time>
                <span class="sep" aria-hidden="true">·</span>
                <span class="mono">{{ post.readingTime }} MIN READ</span>
                <span class="sep" aria-hidden="true">·</span>
                <span class="mono">{{ formatNumber(viewCount) }} 次阅读</span>
              </div>
            </header>

            <details class="toc-mobile">
              <summary>目录 · {{ headings.length }} 节</summary>
              <ul>
                <li v-for="heading in headings" :key="heading.id" :class="`l${heading.level}`">
                  <a :href="`#${heading.id}`">{{ heading.text }}</a>
                </li>
              </ul>
            </details>

            <MarkdownView :html="html" class="article-body" />

            <footer class="article-foot">
              <div class="tags">
                <span class="label mono">TAGS</span>
                <TagPill v-for="tag in post.tags" :key="tag.slug" :tag="tag" />
              </div>
              <p class="updated mono">UPDATED {{ formatDate(post.updatedAt) }}</p>
            </footer>

            <nav class="adjacent" aria-label="上下篇">
              <router-link
                v-if="adjacent.prev"
                :to="`/post/${adjacent.prev.slug}`"
                class="adj-card"
              >
                <span class="dir mono">← 上一篇</span>
                <strong>{{ adjacent.prev.title }}</strong>
              </router-link>
              <span v-else class="adj-card disabled">
                <span class="dir mono">← 上一篇</span>
                <strong>没有更早的文章</strong>
              </span>

              <router-link
                v-if="adjacent.next"
                :to="`/post/${adjacent.next.slug}`"
                class="adj-card right"
              >
                <span class="dir mono">下一篇 →</span>
                <strong>{{ adjacent.next.title }}</strong>
              </router-link>
              <span v-else class="adj-card right disabled">
                <span class="dir mono">下一篇 →</span>
                <strong>已经是最新一篇</strong>
              </span>
            </nav>

            <section id="comments" class="comments">
              <div class="comments-head">
                <div>
                  <span class="eyebrow">COMMENTS</span>
                  <h2>评论 <span class="mono">{{ comments.length }}</span></h2>
                </div>
              </div>

              <CommentForm
                target="post"
                :target-id="post.slug"
                :disabled="!site.config.features.comments"
                :review-required="false"
                :max-length="500"
                placeholder="说说你的看法，或补充一个细节…"
                @submitted="reloadComments"
              />

              <div class="comment-body">
                <CommentList v-if="comments.length" :comments="comments" />
                <p v-else class="no-comment">还没有评论。第一条可以是你。</p>
              </div>
            </section>
          </article>

          <aside class="toc" aria-label="文章目录">
            <div class="toc-inner">
              <p class="toc-title mono">ON THIS PAGE</p>
              <ul v-if="headings.length">
                <li
                  v-for="heading in headings"
                  :key="heading.id"
                  :class="[`l${heading.level}`, { active: activeId === heading.id }]"
                >
                  <a :href="`#${heading.id}`" @click="activeId = heading.id">
                    {{ heading.text }}
                  </a>
                </li>
              </ul>
              <p v-else class="toc-empty">本文没有小节标题。</p>
            </div>
          </aside>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '@/api'
import { useSite } from '@/composables/useSite'
import { renderMarkdown } from '@/utils/markdown'
import { formatDate, formatNumber } from '@/utils/format'
import MarkdownView from '@/components/common/MarkdownView.vue'
import TagPill from '@/components/common/TagPill.vue'
import CommentForm from '@/components/common/CommentForm.vue'
import CommentList from '@/components/common/CommentList.vue'

const route = useRoute()
const { state: site } = useSite()

const post = ref(null)
const adjacent = ref({ prev: null, next: null })
const comments = ref([])
const loading = ref(true)
const error = ref('')
const viewCount = ref(0)
const activeId = ref('')
let observer = null

const rendered = computed(() =>
  post.value ? renderMarkdown(post.value.content) : { html: '', headings: [] }
)
const html = computed(() => rendered.value.html)
const headings = computed(() => rendered.value.headings)
const authorInitial = computed(
  () => site.profile?.avatarText || site.profile?.name?.slice(0, 1) || 'B'
)

async function loadComments() {
  if (!site.config.features.comments) return
  try {
    comments.value = await api.getComments(post.value.slug)
  } catch (err) {
    comments.value = []
  }
}

async function reloadComments() {
  await loadComments()
}

async function load() {
  loading.value = true
  error.value = ''
  post.value = null
  try {
    const slug = route.params.slug
    const [detail, around] = await Promise.all([api.getPost(slug), api.getAdjacentPosts(slug)])
    if (!detail) {
      loading.value = false
      return
    }
    post.value = detail
    adjacent.value = around
    viewCount.value = detail.views
    document.title = `${detail.title} · ${site.config.title || '记录代码与生活'}`
    const increased = await api.increaseViews(slug)
    if (increased) viewCount.value = detail.views + 1
    await loadComments()
    await nextTick()
    setupObserver()
  } catch (err) {
    error.value = err.message || '文章加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function setupObserver() {
  if (observer) observer.disconnect()
  const nodes = headings.value
    .map((heading) => document.getElementById(heading.id))
    .filter(Boolean)
  if (!nodes.length) return
  observer = new IntersectionObserver(
    (entries) => {
      const visible = entries
        .filter((entry) => entry.isIntersecting)
        .sort((a, b) => a.boundingClientRect.top - b.boundingClientRect.top)
      if (visible.length) {
        activeId.value = visible[0].target.id
      }
    },
    { rootMargin: '-96px 0px -66% 0px', threshold: 0 }
  )
  nodes.forEach((node) => observer.observe(node))
}

onMounted(() => {
  load()
})

watch(
  () => route.params.slug,
  (slug) => {
    if (slug) {
      activeId.value = ''
      load()
    }
  }
)

onBeforeUnmount(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped lang="scss">
.detail {
  padding-top: 36px;
}

.crumbs {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
  margin-bottom: 26px;
  font-family: var(--mono);
  font-size: 10.5px;
  color: var(--faint);

  a {
    color: var(--faint);

    &:hover {
      color: var(--cyan);
      text-decoration: none;
    }
  }

  .current {
    max-width: 28em;
    overflow: hidden;
    color: var(--muted);
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.article-layout {
  display: grid;
  grid-template-columns: minmax(0, 760px) 200px;
  gap: 64px;
  justify-content: center;
  align-items: start;
}

.article-head {
  padding-bottom: 24px;
  border-bottom: 1px solid var(--line);
}

.category {
  display: inline-block;
  margin-bottom: 12px;
  padding: 2px 8px;
  border: 1px solid rgba(139, 92, 246, 0.28);
  border-radius: 5px;
  background: rgba(139, 92, 246, 0.08);
  color: #b6a4ff;
  font-family: var(--mono);
  font-size: 10px;
  letter-spacing: 0.06em;

  &:hover {
    text-decoration: none;
    border-color: rgba(139, 92, 246, 0.55);
  }
}

h1 {
  font-size: clamp(27px, 3.6vw, 38px);
  font-weight: 700;
  line-height: 1.22;
  letter-spacing: -0.03em;
}

.excerpt {
  margin-top: 12px;
  color: var(--muted);
  font-size: 13.5px;
  line-height: 1.8;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 18px;
  color: var(--faint);
  font-size: 11px;

  .author {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    color: var(--muted);
  }

  .avatar {
    display: grid;
    overflow: hidden;
    width: 20px;
    height: 20px;
    place-items: center;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(62, 231, 255, 0.4), rgba(139, 92, 246, 0.5));
    color: #04121a;
    font-family: var(--mono);
    font-size: 9px;
    font-weight: 700;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .sep {
    color: var(--faint);
  }

  .mono {
    font-size: 10px;
  }
}

.article-body {
  padding: 28px 0 8px;
}

.article-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 30px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

.tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;

  .label {
    margin-right: 3px;
    color: var(--faint);
    font-size: 9.5px;
  }
}

.updated {
  color: var(--faint);
  font-size: 9.5px;
}

.adjacent {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 28px;
}

.adj-card {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 13px 15px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: rgba(13, 21, 32, 0.4);
  transition: border-color 0.16s, background 0.16s;

  .dir {
    color: var(--faint);
    font-size: 9.5px;
  }

  strong {
    color: var(--text);
    font-size: 12.5px;
    font-weight: 500;
    line-height: 1.5;
  }

  &:hover {
    border-color: var(--line-strong);
    background: rgba(17, 27, 41, 0.6);
    text-decoration: none;
  }

  &.right {
    text-align: right;
  }

  &.disabled {
    opacity: 0.45;

    strong {
      color: var(--faint);
      font-weight: 400;
    }
  }
}

.comments {
  margin-top: 44px;
  padding-top: 28px;
  border-top: 1px solid var(--line);
}

.comments-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;

  h2 {
    margin-top: 5px;
    font-size: 21px;

    .mono {
      margin-left: 4px;
      color: var(--faint);
      font-size: 10px;
    }
  }
}

.comment-body {
  margin-top: 12px;
  padding: 2px 4px;
}

.no-comment {
  padding: 20px 0;
  color: var(--faint);
  font-size: 12.5px;
  text-align: center;
}

.toc {
  position: sticky;
  top: calc(var(--header-h) + 28px);
  align-self: start;
  will-change: transform;
}

.toc-inner {
  padding-left: 14px;
  border-left: 1px solid var(--line);
  max-height: calc(100vh - 140px);
  overflow: auto;
}

.toc-title {
  margin-bottom: 10px;
  color: var(--faint);
  font-size: 9.5px;
  letter-spacing: 0.14em;
}

.toc ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.toc li {
  a {
    display: block;
    padding: 4px 0;
    color: var(--faint);
    font-size: 11.5px;
    line-height: 1.55;
    transition: color 0.16s;

    &:hover {
      color: var(--text);
      text-decoration: none;
    }
  }

  &.l3 a {
    padding-left: 12px;
    font-size: 11px;
  }

  &.active a {
    color: var(--cyan);
  }
}

.toc-empty {
  color: var(--faint);
  font-size: 11px;
}

.toc-mobile {
  display: none;
  margin: 18px 0 0;
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: rgba(13, 21, 32, 0.5);

  summary {
    color: var(--muted);
    font-size: 12px;
    cursor: pointer;
  }

  ul {
    margin: 10px 0 0;
    padding-left: 18px;

    .l3 {
      margin-left: 12px;
    }

    a {
      color: var(--muted);
      font-size: 12px;

      &:hover {
        color: var(--cyan);
      }
    }
  }
}

.skeleton-page {
  padding-top: 70px;
}

.skeleton-page .line {
  margin-bottom: 14px;
}

.title {
  height: 26px;
  width: 62%;
}

.w30 {
  width: 30%;
}

.w50 {
  width: 50%;
}

.block {
  height: 180px;
  margin-top: 30px;
}

.block.short {
  height: 90px;
}

.state-box {
  max-width: 540px;
  margin: 60px auto;
  padding: 44px 32px;
  border: 1px dashed var(--line);
  border-radius: var(--radius-card);
  text-align: center;

  .code {
    display: block;
    margin-bottom: 8px;
    color: var(--faint);
    font-size: 12px;
    letter-spacing: 0.2em;
  }

  h2 {
    font-size: 22px;
  }

  p {
    margin: 10px 0 20px;
    color: var(--muted);
    font-size: 13px;
  }
}

.state-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn {
  display: inline-flex;
  align-items: center;
  height: 36px;
  padding: 0 18px;
  border-radius: var(--radius);
  font-size: 12.5px;

  &.primary {
    border: 1px solid rgba(62, 231, 255, 0.45);
    background: linear-gradient(180deg, #54ecff, #25cfe8);
    color: #04121a;
    font-weight: 600;

    &:hover {
      filter: brightness(1.07);
      text-decoration: none;
    }
  }

  &.ghost {
    border: 1px solid var(--line);
    color: var(--muted);

    &:hover {
      color: var(--text);
      border-color: var(--line-strong);
      text-decoration: none;
    }
  }
}

@media (max-width: 1180px) {
  .article-layout {
    grid-template-columns: minmax(0, 760px);
    gap: 0;
  }

  .toc {
    display: none;
  }

  .toc-mobile {
    display: block;
  }
}

@media (max-width: 640px) {
  .adjacent {
    grid-template-columns: 1fr;
  }

  .adj-card.right {
    text-align: left;
  }

  .comments-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 6px;
  }
}
</style>
