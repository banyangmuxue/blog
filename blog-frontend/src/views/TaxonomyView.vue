<template>
  <div class="taxonomy page-enter">
    <PageHero
      :eyebrow="isTag ? 'TAG' : 'CATEGORY'"
      :title="title"
      :description="description"
      :parent="{ label: '文章', to: '/articles' }"
    >
      <template #actions>
        <span class="count mono">{{ total }} ARTICLES</span>
      </template>
    </PageHero>

    <div class="container">
      <SkeletonList v-if="loading" :rows="4" />

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
        :title="`${title}下还没有文章`"
        description="这个分类暂时空着，换一个分类或回到文章列表看看。"
        action="全部文章"
        to="/articles"
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

const isTag = computed(() => route.name === 'tag')
const slug = computed(() => route.params.slug)
const taxonomy = computed(() => {
  const pool = isTag.value ? site.tags : site.categories
  return pool.find((item) => item.slug === slug.value)
})
const title = computed(() => taxonomy.value?.name || (isTag.value ? '标签' : '分类'))
const description = computed(
  () =>
    taxonomy.value?.description ||
    (isTag.value ? '这个标签下的全部文章。' : '这个分类下的全部文章。')
)

const items = ref([])
const total = ref(0)
const pages = ref(1)
const page = ref(Number(route.query.page) || 1)
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const params = isTag.value
      ? { tag: slug.value, page: page.value, size: 10 }
      : { category: slug.value, page: page.value, size: 10 }
    const result = await api.getPosts(params)
    items.value = result.items
    total.value = result.total
    pages.value = result.pages
    page.value = result.page
  } catch (err) {
    error.value = err.message || '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function changePage(next) {
  page.value = next
  router.push({ query: { page: next } })
}

watch(
  () => [route.name, route.params.slug, route.query.page],
  () => {
    page.value = Number(route.query.page) || 1
    load()
  }
)

onMounted(async () => {
  await loadSite()
  load()
})
</script>

<style scoped lang="scss">
.count {
  color: var(--faint);
  font-size: 10.5px;
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
</style>
