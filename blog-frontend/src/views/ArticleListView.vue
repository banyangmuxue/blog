<template>
  <div class="list-view page-enter">
    <PageHero
      eyebrow="ARTICLES"
      title="文章"
      description="按时间排列的全部记录：前端工程、后端笔记、阅读与写作。"
    >
      <template #actions>
        <span class="total mono">共 {{ total }} 篇</span>
      </template>
    </PageHero>

    <div class="container">
      <div class="toolbar">
        <div class="filters">
          <router-link to="/articles" class="filter" :class="{ active: true }">全部</router-link>
          <router-link
            v-for="category in categories"
            :key="category.slug"
            :to="`/categories/${category.slug}`"
            class="filter"
          >
            {{ category.name }}
          </router-link>
        </div>

        <label class="sort">
          <span>排序</span>
          <select v-model="sort" aria-label="排序方式" @change="onSortChange">
            <option value="latest">最新发布</option>
            <option value="oldest">最早发布</option>
            <option value="views">阅读最多</option>
          </select>
        </label>
      </div>

      <SkeletonList v-if="loading" :rows="5" />

      <div v-else-if="error" class="error-box">
        <p>{{ error }}</p>
        <button type="button" class="retry" @click="load">重新加载</button>
      </div>

      <template v-else-if="items.length">
        <div class="post-list">
          <PostCard v-for="post in items" :key="post.slug" :post="post" />
        </div>
        <AppPagination :page="page" :pages="pages" @update:page="changePage" />
      </template>

      <EmptyState
        v-else
        title="这里还没有文章"
        description="换个分类看看，或者回到首页。"
        action="回到首页"
        to="/"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { loadSite, useSite } from '@/composables/useSite'
import PageHero from '@/components/common/PageHero.vue'
import PostCard from '@/components/common/PostCard.vue'
import SkeletonList from '@/components/common/SkeletonList.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const { state: site } = useSite()

const categories = computed(() => site.categories)
const items = ref([])
const total = ref(0)
const pages = ref(1)
const page = ref(Number(route.query.page) || 1)
const sort = ref(route.query.sort || 'latest')
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await api.getPosts({ page: page.value, size: 10, sort: sort.value })
    items.value = result.items
    total.value = result.total
    pages.value = result.pages
    page.value = result.page
  } catch (err) {
    error.value = err.message || '文章加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function changePage(next) {
  page.value = next
  router.push({ name: 'articles', query: { ...route.query, page: next } })
}

function onSortChange() {
  page.value = 1
  router.push({ name: 'articles', query: { sort: sort.value } })
}

watch([page, sort], load)

onMounted(async () => {
  await loadSite()
  load()
})
</script>

<style scoped lang="scss">
.total {
  color: var(--faint);
  font-size: 10.5px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line);
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.filter {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 11px;
  border: 1px solid transparent;
  border-radius: 999px;
  color: var(--muted);
  font-size: 12px;
  transition: 0.16s;

  &:hover {
    color: var(--text);
    background: rgba(255, 255, 255, 0.03);
    text-decoration: none;
  }

  &.active {
    border-color: rgba(62, 231, 255, 0.3);
    background: rgba(62, 231, 255, 0.08);
    color: var(--cyan);
  }
}

.sort {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: none;
  color: var(--faint);
  font-size: 11.5px;

  select {
    height: 30px;
    padding: 0 26px 0 9px;
    border: 1px solid var(--line);
    border-radius: 7px;
    background: rgba(13, 21, 32, 0.9);
    color: var(--text);
    font-size: 12px;
    cursor: pointer;
  }
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.error-box {
  padding: 44px 24px;
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

@media (max-width: 640px) {
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
