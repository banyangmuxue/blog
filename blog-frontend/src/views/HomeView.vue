<template>
  <div class="home page-enter">
    <section class="hero container">
      <div class="hero-copy">
        <span class="eyebrow">PERSONAL BLOG · EST. {{ site.config.foundedAt || '2023' }}</span>
        <h1>
          {{ titleParts[0] }}<em>{{ titleParts[1] }}</em>
        </h1>
        <p class="lede">{{ site.config.description || site.config.subtitle }}</p>

        <form class="hero-search" role="search" @submit.prevent="goSearch">
          <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
            <circle cx="10.5" cy="10.5" r="6.5" fill="none" stroke="currentColor" stroke-width="1.6" />
            <path d="m15.5 15.5 4 4" stroke="currentColor" stroke-width="1.6" />
          </svg>
          <input
            v-model="keyword"
            type="search"
            placeholder="搜索文章、标签或关键词…"
            aria-label="搜索文章"
            maxlength="50"
          />
          <button type="submit">搜索</button>
        </form>

        <div class="hero-actions">
          <button type="button" class="btn primary" @click="scrollToLatest">浏览文章</button>
          <router-link to="/about" class="btn ghost">关于我 →</router-link>
        </div>
      </div>

      <div class="terminal">
        <div class="term-bar">
          <span class="dot r"></span><span class="dot y"></span><span class="dot g"></span>
          <span class="term-title">~/blog — whoami</span>
        </div>
        <div class="term-body">
          <div class="ln"><span class="k">$ whoami</span><span class="v">{{ site.profile?.name || '林深' }} / {{ site.profile?.skills?.slice(0, 2).join(' + ') || 'frontend + backend' }}</span></div>
          <div class="ln"><span class="k">$ posts</span><span class="v">{{ total }} published</span></div>
          <div class="ln"><span class="k">$ focus</span><span class="v">engineering · writing</span></div>
          <div class="ln"><span class="k">$ status</span><span class="v green">building in public</span></div>
          <div class="ln"><span class="prompt">$</span><span class="v">cat latest.md <span class="blink"></span></span></div>
          <div class="ln">
            <span class="k">$ links</span>
            <span class="v term-links">
              <a
                v-for="item in site.config.socials"
                :key="item.label"
                :href="item.url"
                :target="item.url.startsWith('http') ? '_blank' : undefined"
                rel="noopener noreferrer"
              >
                {{ item.label }}
              </a>
            </span>
          </div>
        </div>
      </div>
    </section>

    <div v-if="showAnnouncement" class="container">
      <div class="announcement">
        <span class="tag">公告</span>
        <p>{{ site.config.announcement }}</p>
        <button type="button" aria-label="关闭公告" @click="dismissAnnouncement">×</button>
      </div>
    </div>

    <section v-if="pinned.length" class="section container">
      <div class="section-head">
        <div>
          <span class="eyebrow">PINNED</span>
          <h2>置顶阅读</h2>
        </div>
        <span class="count mono">{{ pinned.length }} ARTICLES</span>
      </div>
      <div class="pinned-grid">
        <PostCard v-for="post in pinned" :key="post.slug" :post="post" variant="pinned" />
      </div>
    </section>

    <section id="latest" class="section container">
      <div class="section-head">
        <div>
          <span class="eyebrow">LATEST</span>
          <h2>最新文章</h2>
        </div>
        <router-link to="/articles" class="more-link">全部文章 →</router-link>
      </div>

      <div class="latest-layout">
        <div class="latest-main">
          <SkeletonList v-if="loading" :rows="4" />
          <div v-else-if="error" class="error-box">
            <p>{{ error }}</p>
            <button type="button" class="btn primary" @click="load">重试</button>
          </div>
          <div v-else-if="latest.length" class="latest-grid">
            <PostCard v-for="post in latest" :key="post.slug" :post="post" variant="pinned" />
          </div>
          <EmptyState
            v-else
            title="还没有发布的文章"
            description="第一篇文章正在路上，可以先看看关于我。"
            action="关于我"
            to="/about"
          />
        </div>
        <SiteSidebar />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import { loadSite, useSite } from '@/composables/useSite'
import PostCard from '@/components/common/PostCard.vue'
import SiteSidebar from '@/components/common/SiteSidebar.vue'
import SkeletonList from '@/components/common/SkeletonList.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()
const { state: site } = useSite()

const keyword = ref('')
const loading = ref(true)
const error = ref('')
const pinned = ref([])
const latest = ref([])
const total = ref(0)
const showAnnouncement = ref(false)

const titleParts = computed(() => {
  const title = site.config.title || '记录代码与生活'
  const index = title.indexOf('与')
  if (index > 0 && index < title.length - 1) {
    return [title.slice(0, index), title.slice(index)]
  }
  return [title, '']
})

function goSearch() {
  const value = keyword.value.trim()
  if (!value) return
  router.push({ name: 'search', query: { q: value } })
}

function scrollToLatest() {
  document.getElementById('latest')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function dismissAnnouncement() {
  showAnnouncement.value = false
  sessionStorage.setItem('blog-announcement-dismissed', '1')
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [pinnedResult, latestResult] = await Promise.all([
      api.getPosts({ pinned: true, size: 3 }),
      api.getPosts({ page: 1, size: 6 })
    ])
    pinned.value = pinnedResult.items
    latest.value = latestResult.items
    total.value = latestResult.total
  } catch (err) {
    error.value = err.message || '内容加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadSite()
  showAnnouncement.value =
    Boolean(site.config.announcement) && !sessionStorage.getItem('blog-announcement-dismissed')
  load()
})
</script>

<style scoped lang="scss">
.home {
  padding-top: 56px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.65fr);
  gap: 56px;
  align-items: center;
  padding-bottom: 40px;
}

.hero-copy {
  .eyebrow {
    display: inline-block;
    margin-bottom: 18px;
  }
}

h1 {
  font-size: clamp(38px, 5vw, 60px);
  font-weight: 700;
  letter-spacing: -0.045em;
  line-height: 1.05;

  em {
    font-style: normal;
    background: linear-gradient(100deg, var(--cyan) 10%, #b6a4ff 55%, var(--violet));
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }
}

.lede {
  max-width: 32em;
  margin: 18px 0 24px;
  color: var(--muted);
  font-size: 14.5px;
  line-height: 1.85;
}

.hero-search {
  display: flex;
  align-items: center;
  gap: 9px;
  max-width: 480px;
  height: 44px;
  padding: 0 6px 0 14px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.24);
  color: var(--faint);
  transition: border-color 0.16s;

  &:focus-within {
    border-color: rgba(62, 231, 255, 0.45);
  }

  input {
    flex: 1;
    min-width: 0;
    height: 100%;
    border: 0;
    background: transparent;
    color: var(--text);
    font-size: 13px;
    outline: none;

    &::placeholder {
      color: var(--faint);
    }
  }

  button {
    height: 32px;
    padding: 0 15px;
    border-radius: 7px;
    background: rgba(62, 231, 255, 0.14);
    border: 1px solid rgba(62, 231, 255, 0.32);
    color: var(--cyan);
    font-size: 12px;
  }
}

.hero-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 38px;
  padding: 0 20px;
  border-radius: var(--radius);
  font-size: 13px;
  transition: 0.16s;

  &.primary {
    border: 1px solid rgba(62, 231, 255, 0.5);
    background: linear-gradient(180deg, #54ecff, #25cfe8);
    color: #04121a;
    font-weight: 600;

    &:hover {
      filter: brightness(1.07);
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

.terminal {
  overflow: hidden;
  border: 1px solid var(--line-strong);
  border-radius: 16px;
  background: linear-gradient(170deg, rgba(17, 27, 41, 0.94), rgba(8, 13, 20, 0.96));
  box-shadow: 0 30px 70px -30px rgba(0, 0, 0, 0.9), inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.term-bar {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 11px 14px;
  border-bottom: 1px solid var(--line);
  font-family: var(--mono);
  font-size: 10px;
  color: var(--faint);
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #2b3b4e;

  &.r {
    background: #ff6b81;
  }

  &.y {
    background: #f5c451;
  }

  &.g {
    background: #3ddc97;
  }
}

.term-title {
  margin-left: 8px;
}

.term-body {
  padding: 16px 18px 18px;
  font-family: var(--mono);
  font-size: 11.5px;
  line-height: 2;
}

.ln {
  display: flex;
  gap: 14px;
  white-space: nowrap;
}

.k {
  width: 76px;
  flex: none;
  color: var(--cyan);
}

.v {
  overflow: hidden;
  color: var(--muted);
  text-overflow: ellipsis;

  &.green {
    color: var(--green);
  }
}

.prompt {
  color: var(--green);
}

.term-links {
  display: flex;
  gap: 16px;

  a {
    color: var(--muted);
    letter-spacing: 0.04em;

    &:hover {
      color: var(--cyan);
      text-decoration: none;
    }
  }
}

.blink {
  display: inline-block;
  width: 6px;
  height: 12px;
  background: var(--cyan);
  vertical-align: -2px;
  animation: blink 1.1s step-end infinite;
}

@keyframes blink {
  50% {
    opacity: 0;
  }
}

.announcement {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border: 1px solid rgba(139, 92, 246, 0.24);
  border-radius: 9px;
  background: rgba(139, 92, 246, 0.06);

  .tag {
    padding: 0 7px;
    border: 1px solid rgba(139, 92, 246, 0.36);
    border-radius: 4px;
    color: #b6a4ff;
    font-family: var(--mono);
    font-size: 9.5px;
    white-space: nowrap;
  }

  p {
    flex: 1;
    color: var(--muted);
    font-size: 12px;
  }

  button {
    color: var(--faint);
    font-size: 14px;

    &:hover {
      color: var(--text);
    }
  }
}

.section {
  padding-top: 48px;
}

.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 18px;

  h2 {
    margin-top: 6px;
    font-size: 24px;
    letter-spacing: -0.02em;
  }

  .count {
    color: var(--faint);
    font-size: 10px;
  }
}

.pinned-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.more-link {
  color: var(--muted);
  font-size: 12px;
  white-space: nowrap;

  &:hover {
    color: var(--cyan);
    text-decoration: none;
  }
}

.latest-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 292px;
  gap: 28px;
  align-items: start;
}

.latest-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.error-box {
  padding: 40px 24px;
  border: 1px dashed rgba(255, 125, 143, 0.3);
  border-radius: var(--radius-card);
  text-align: center;

  p {
    margin-bottom: 14px;
    color: var(--muted);
    font-size: 13px;
  }
}

@media (max-width: 1024px) {
  .hero {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .latest-layout {
    grid-template-columns: 1fr;
  }

  .pinned-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .home {
    padding-top: 36px;
  }

  .hero {
    padding-bottom: 28px;
  }

  .pinned-grid,
  .latest-grid {
    grid-template-columns: 1fr;
  }

  .section-head {
    align-items: flex-start;
  }
}
</style>
