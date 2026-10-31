<template>
  <div class="search-page page-enter">
    <PageHero
      eyebrow="SEARCH"
      title="搜索"
      description="按标题、摘要、正文与标签查找文章。结果会高亮匹配的关键词。"
      :parent="{ label: '文章', to: '/articles' }"
    />

    <div class="container">
      <form class="search-box" role="search" @submit.prevent="submitSearch">
        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
          <circle cx="10.5" cy="10.5" r="6.5" fill="none" stroke="currentColor" stroke-width="1.6" />
          <path d="m15.5 15.5 4 4" stroke="currentColor" stroke-width="1.6" />
        </svg>
        <input
          ref="inputRef"
          v-model="keyword"
          type="search"
          maxlength="50"
          placeholder="输入关键词，例如 Vue、性能、Spring Boot…"
          aria-label="搜索关键词"
        />
        <button type="submit">搜索</button>
      </form>

      <div v-if="!activeKeyword" class="idle">
        <p class="hint">输入关键词开始搜索。也可以从热门标签入手：</p>
        <div class="chips">
          <TagPill v-for="tag in popularTags" :key="tag.slug" :tag="tag" show-count />
        </div>
      </div>

      <SkeletonList v-else-if="loading" :rows="4" />

      <div v-else-if="error" class="error-box">
        <p>{{ error }}</p>
        <button type="button" class="retry" @click="load">重新搜索</button>
      </div>

      <template v-else>
        <div class="result-head">
          <p>
            搜索 <strong>“{{ activeKeyword }}”</strong>
            <span class="mono">{{ total }} 条结果</span>
          </p>
        </div>

        <div v-if="items.length" class="result-list">
          <router-link
            v-for="post in items"
            :key="post.slug"
            :to="`/post/${post.slug}`"
            class="result"
          >
            <div class="result-meta">
              <span v-if="post.category" class="cat">{{ post.category.name }}</span>
              <time class="mono">{{ formatDate(post.publishedAt) }}</time>
              <span class="mono">{{ post.readingTime }} MIN READ</span>
            </div>
            <h2 class="result-title">
              <template v-for="(part, index) in highlight(post.title)" :key="index">
                <span :class="{ mark: part.match }">{{ part.text }}</span>
              </template>
            </h2>
            <p class="result-snippet">
              <template v-for="(part, index) in highlight(post.snippet || post.excerpt)" :key="index">
                <span :class="{ mark: part.match }">{{ part.text }}</span>
              </template>
            </p>
          </router-link>
        </div>

        <EmptyState
          v-else
          :title="`没有找到与“${activeKeyword}”相关的内容`"
          description="试试更短的关键词、同义词，或从热门标签进入。"
          action="浏览全部文章"
          to="/articles"
        />

        <AppPagination :page="page" :pages="pages" @update:page="changePage" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useSite } from '@/composables/useSite'
import { formatDate } from '@/utils/format'
import PageHero from '@/components/common/PageHero.vue'
import TagPill from '@/components/common/TagPill.vue'
import SkeletonList from '@/components/common/SkeletonList.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import AppPagination from '@/components/common/AppPagination.vue'

const route = useRoute()
const router = useRouter()
const { state: site } = useSite()

const keyword = ref(route.query.q || '')
const activeKeyword = computed(() => (route.query.q || '').trim())
const page = ref(Number(route.query.page) || 1)
const items = ref([])
const total = ref(0)
const pages = ref(1)
const loading = ref(false)
const error = ref('')
const inputRef = ref(null)
const popularTags = computed(() => site.tags.slice(0, 8))

function highlight(text = '') {
  const needle = activeKeyword.value
  if (!needle) return [{ text, match: false }]
  const lower = text.toLowerCase()
  const target = needle.toLowerCase()
  const parts = []
  let cursor = 0
  while (cursor < text.length) {
    const index = lower.indexOf(target, cursor)
    if (index < 0) {
      parts.push({ text: text.slice(cursor), match: false })
      break
    }
    if (index > cursor) parts.push({ text: text.slice(cursor, index), match: false })
    parts.push({ text: text.slice(index, index + needle.length), match: true })
    cursor = index + needle.length
  }
  return parts.filter((part) => part.text)
}

async function load() {
  if (!activeKeyword.value) {
    items.value = []
    total.value = 0
    pages.value = 1
    return
  }
  loading.value = true
  error.value = ''
  try {
    const result = await api.getPosts({
      q: activeKeyword.value,
      page: page.value,
      size: 10
    })
    items.value = result.items
    total.value = result.total
    pages.value = result.pages
    page.value = result.page
  } catch (err) {
    error.value = err.message || '搜索失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function submitSearch() {
  const value = keyword.value.trim()
  router.push({ name: 'search', query: value ? { q: value } : {} })
  if (!value) inputRef.value?.focus()
}

function changePage(next) {
  page.value = next
  router.push({ name: 'search', query: { ...route.query, page: next } })
}

watch(
  () => [route.query.q, route.query.page],
  () => {
    keyword.value = route.query.q || ''
    page.value = Number(route.query.page) || 1
    load()
  }
)

onMounted(() => {
  load()
})
</script>

<style scoped lang="scss">
.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 50px;
  padding: 0 6px 0 16px;
  border: 1px solid var(--line);
  border-radius: 11px;
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
    font-size: 14px;
    outline: none;

    &::placeholder {
      color: var(--faint);
    }
  }

  button {
    height: 38px;
    padding: 0 20px;
    border: 1px solid rgba(62, 231, 255, 0.45);
    border-radius: 8px;
    background: linear-gradient(180deg, #54ecff, #25cfe8);
    color: #04121a;
    font-size: 13px;
    font-weight: 600;
  }
}

.idle {
  margin-top: 36px;
  padding: 26px 24px;
  border: 1px dashed var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.3);

  .hint {
    margin-bottom: 14px;
    color: var(--muted);
    font-size: 12.5px;
  }
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.result-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 28px 0 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--line);

  p {
    color: var(--muted);
    font-size: 12.5px;

    strong {
      color: var(--text);
      font-weight: 600;
    }
  }

  .mono {
    margin-left: 8px;
    color: var(--faint);
    font-size: 10px;
  }
}

.result-list {
  display: flex;
  flex-direction: column;
}

.result {
  display: block;
  padding: 18px 14px;
  border-bottom: 1px solid var(--line);
  border-radius: 8px;
  transition: background 0.16s;

  &:hover {
    background: rgba(255, 255, 255, 0.025);
    text-decoration: none;

    .result-title {
      color: var(--cyan);
    }
  }
}

.result-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  color: var(--faint);

  .cat {
    padding: 1px 7px;
    border: 1px solid rgba(139, 92, 246, 0.28);
    border-radius: 4px;
    background: rgba(139, 92, 246, 0.08);
    color: #b6a4ff;
    font-family: var(--mono);
    font-size: 9.5px;
  }

  .mono {
    font-size: 9.5px;
  }
}

.result-title {
  margin: 7px 0 5px;
  color: var(--text);
  font-size: 17px;
  font-weight: 600;
  line-height: 1.45;
  transition: color 0.16s;
}

.result-snippet {
  display: -webkit-box;
  overflow: hidden;
  color: var(--muted);
  font-size: 12.5px;
  line-height: 1.7;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.mark {
  border-radius: 3px;
  background: rgba(62, 231, 255, 0.16);
  color: var(--cyan);
  padding: 0 2px;
}

.error-box {
  margin-top: 28px;
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

.retry {
  height: 34px;
  padding: 0 16px;
  border: 1px solid rgba(62, 231, 255, 0.32);
  border-radius: 7px;
  background: rgba(62, 231, 255, 0.08);
  color: var(--cyan);
  font-size: 12px;
}
</style>
