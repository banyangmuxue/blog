<template>
  <div class="admin-container page-enter">
    <header class="admin-page-head">
      <div>
        <p class="eyebrow">POSTS</p>
        <h1 class="admin-page-title">文章管理</h1>
        <p class="admin-page-desc">共 {{ total }} 篇，草稿与已发布文章都在这里。</p>
      </div>

      <router-link :to="{ name: 'admin-post-new' }" class="admin-btn primary">
        <span>＋</span> 新建文章
      </router-link>
    </header>

    <section class="admin-card list-panel">
      <div class="list-toolbar">
        <label class="status-filter">
          <select v-model="status" class="admin-input" @change="search">
            <option value="">全部状态</option>
            <option value="published">已发布</option>
            <option value="draft">草稿</option>
          </select>
        </label>

        <div class="search-box">
          <input
            v-model="keyword"
            class="admin-input"
            type="search"
            placeholder="搜索标题、Slug 或摘要"
            @keyup.enter="search"
          />
          <button type="button" class="admin-btn" @click="search">搜索</button>
        </div>
      </div>

      <div v-if="loading" class="list-loading" aria-label="加载中">
        <div v-for="row in 4" :key="row" class="skeleton loading-row"></div>
      </div>

      <div v-else-if="error" class="list-error">
        <p class="admin-error-box">{{ error }}</p>
        <button type="button" class="admin-btn small" @click="load">重新加载</button>
      </div>

      <div v-else-if="!items.length" class="admin-empty">
        <strong>还没有符合条件的文章</strong>
        <span>换个筛选条件，或者创建一篇新文章。</span>
      </div>

      <div v-else class="table-wrap">
        <table class="post-table">
          <thead>
            <tr>
              <th>文章</th>
              <th>状态</th>
              <th>分类</th>
              <th>阅读</th>
              <th>更新时间</th>
              <th class="actions-col">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in items" :key="post.id">
              <td class="post-cell">
                <div class="post-title">
                  <span v-if="post.pinned" class="pin" title="置顶">★</span>
                  <span>{{ post.title }}</span>
                </div>
                <div class="post-slug mono">/post/{{ post.slug }}</div>
                <div v-if="post.tags && post.tags.length" class="post-tags">
                  <span v-for="tag in post.tags.slice(0, 3)" :key="tag.slug" class="mini-tag">
                    #{{ tag.name }}
                  </span>
                </div>
              </td>
              <td>
                <span class="admin-badge" :class="post.status">
                  {{ post.status === 'published' ? '已发布' : '草稿' }}
                </span>
              </td>
              <td class="muted">{{ post.categoryName || '未分类' }}</td>
              <td class="mono">{{ formatNumber(post.views || 0) }}</td>
              <td class="time mono">{{ post.updatedAt || '—' }}</td>
              <td>
                <div class="row-actions">
                  <router-link
                    :to="{ name: 'admin-post-edit', params: { id: post.id } }"
                    class="admin-btn ghost small"
                  >
                    编辑
                  </router-link>
                  <button
                    type="button"
                    class="admin-btn ghost small"
                    @click="toggleStatus(post)"
                  >
                    {{ post.status === 'published' ? '下线' : '发布' }}
                  </button>
                  <button
                    type="button"
                    class="admin-btn ghost small danger-text"
                    @click="removePost(post)"
                  >
                    删除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <AppPagination :page="page" :pages="pages" @update:page="changePage" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { showToast } from '@/composables/useToast'
import { formatNumber } from '@/utils/format'
import AppPagination from '@/components/common/AppPagination.vue'

const route = useRoute()
const router = useRouter()

const items = ref([])
const total = ref(0)
const page = ref(1)
const pages = ref(1)
const status = ref('')
const keyword = ref('')
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await api.getAdminPosts({
      page: page.value,
      size: 10,
      status: status.value,
      keyword: keyword.value.trim()
    })
    items.value = result.items
    total.value = result.total
    pages.value = result.pages
    page.value = result.page
  } catch (err) {
    if (err.status === 401) {
      router.replace({ name: 'admin-login', query: { redirect: route.fullPath } })
      return
    }
    error.value = err.message || '文章列表加载失败'
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  load()
}

function changePage(next) {
  page.value = next
  load()
}

async function toggleStatus(post) {
  const next = post.status === 'published' ? 'draft' : 'published'
  if (next === 'draft' && !window.confirm(`确定下线《${post.title}》？下线后前台将无法访问。`)) {
    return
  }

  try {
    await api.updateAdminPostStatus(post.id, next)
    showToast(next === 'published' ? '文章已发布' : '文章已转为草稿', 'success')
    load()
  } catch (err) {
    if (err.status === 401) {
      router.replace({ name: 'admin-login', query: { redirect: route.fullPath } })
      return
    }
    showToast(err.message || '状态修改失败', 'error')
  }
}

async function removePost(post) {
  if (!window.confirm(`确定删除《${post.title}》？该文章的评论也会一并删除，且无法恢复。`)) {
    return
  }

  try {
    await api.deleteAdminPost(post.id)
    showToast('文章已删除', 'success')
    if (items.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    load()
  } catch (err) {
    if (err.status === 401) {
      router.replace({ name: 'admin-login', query: { redirect: route.fullPath } })
      return
    }
    showToast(err.message || '删除失败', 'error')
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
.list-panel {
  overflow: hidden;
}

.list-toolbar {
  display: flex;
  padding: 16px 18px;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.012);
}

.status-filter {
  flex: none;
  width: 136px;
}

.search-box {
  display: flex;
  max-width: 420px;
  margin-left: auto;
  gap: 8px;

  input {
    min-width: 200px;
  }
}

.list-loading {
  display: flex;
  padding: 18px;
  flex-direction: column;
  gap: 10px;
}

.loading-row {
  height: 46px;
}

.list-error {
  display: flex;
  padding: 24px;
  align-items: center;
  gap: 14px;

  .admin-error-box {
    flex: 1;
  }
}

.table-wrap {
  overflow-x: auto;
}

.post-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;

  th {
    padding: 11px 16px;
    border-bottom: 1px solid var(--line);
    color: var(--faint);
    font-family: var(--mono);
    font-size: 10.5px;
    font-weight: 400;
    letter-spacing: 0.08em;
    text-align: left;
    text-transform: uppercase;
    white-space: nowrap;
  }

  td {
    padding: 15px 16px;
    border-bottom: 1px solid rgba(125, 211, 252, 0.08);
    vertical-align: top;
  }

  tbody tr {
    transition: background 0.15s;

    &:hover {
      background: rgba(62, 231, 255, 0.025);
    }
  }

  tbody tr:last-child td {
    border-bottom: 0;
  }
}

.post-cell {
  min-width: 260px;
  max-width: 440px;
}

.post-title {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  color: var(--text);
  font-size: 14px;
  font-weight: 500;
  line-height: 1.55;
}

.pin {
  flex: none;
  color: var(--amber);
  font-size: 11px;
}

.post-slug {
  margin-top: 4px;
  color: var(--faint);
  font-size: 10.5px;
}

.post-tags {
  display: flex;
  margin-top: 7px;
  flex-wrap: wrap;
  gap: 8px;
}

.mini-tag {
  color: var(--violet);
  font-family: var(--mono);
  font-size: 10.5px;
}

.time {
  color: var(--faint);
  font-size: 10.5px;
  white-space: nowrap;
}

.actions-col {
  text-align: right !important;
}

.row-actions {
  display: flex;
  justify-content: flex-end;
  gap: 2px;
  white-space: nowrap;

  a,
  button {
    padding-right: 7px;
    padding-left: 7px;
  }
}

.danger-text {
  color: var(--red) !important;
}

@media (max-width: 720px) {
  .list-toolbar {
    flex-wrap: wrap;
  }

  .search-box {
    width: 100%;
    max-width: none;
    margin-left: 0;
  }

  .search-box input {
    min-width: 0;
  }
}
</style>
