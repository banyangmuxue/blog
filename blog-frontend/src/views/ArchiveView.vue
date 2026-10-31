<template>
  <div class="archive page-enter">
      <PageHero
        eyebrow="ARCHIVE"
        title="归档"
        :description="summary"
        :parent="{ label: '文章', to: '/articles' }"
      />

    <div class="container">
      <SkeletonList v-if="loading" :rows="4" />

      <div v-else-if="error" class="error-box">
        <p>{{ error }}</p>
        <button type="button" class="retry" @click="load">重新加载</button>
      </div>

      <div v-else-if="groups.length" class="archive-layout">
        <aside class="years" aria-label="年份导航">
          <a v-for="group in groups" :key="group.year" :href="`#year-${group.year}`" class="year-link">
            <span>{{ group.year }}</span>
            <span class="mono">{{ group.count }}</span>
          </a>
        </aside>

        <div class="groups">
          <section v-for="group in groups" :id="`year-${group.year}`" :key="group.year" class="year-block">
            <div class="year-head">
              <h2>{{ group.year }}</h2>
              <span class="mono">{{ group.count }} ARTICLES</span>
            </div>

            <div v-for="month in group.months" :key="month.month" class="month-block">
              <h3 class="month-title">{{ month.label }}</h3>
              <ul class="entries">
                <li v-for="post in month.items" :key="post.slug">
                  <time class="mono date" :datetime="post.publishedAt">{{ post.publishedAt.slice(5) }}</time>
                  <router-link :to="`/post/${post.slug}`" class="entry-title">{{ post.title }}</router-link>
                  <span v-if="post.category" class="cat">{{ post.category.name }}</span>
                </li>
              </ul>
            </div>
          </section>
        </div>
      </div>

      <EmptyState v-else title="归档还是空的" description="发布第一篇文章后，这里会按时间自动整理。" />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/api'
import PageHero from '@/components/common/PageHero.vue'
import SkeletonList from '@/components/common/SkeletonList.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const groups = ref([])
const loading = ref(true)
const error = ref('')

const total = computed(() => groups.value.reduce((sum, group) => sum + group.count, 0))
const summary = computed(() => `按年份与月份浏览全部已发布文章。共 ${total.value} 篇记录。`)

async function load() {
  loading.value = true
  error.value = ''
  try {
    groups.value = await api.getArchive()
  } catch (err) {
    error.value = err.message || '归档加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
.archive-layout {
  display: grid;
  grid-template-columns: 132px minmax(0, 1fr);
  gap: 44px;
  align-items: start;
}

.years {
  position: sticky;
  top: calc(var(--header-h) + 32px);
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-left: 12px;
  border-left: 1px solid var(--line);
  will-change: transform;
}

.year-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 7px 8px;
  border-radius: 6px;
  color: var(--muted);
  font-family: var(--mono);
  font-size: 12px;
  transition: 0.16s;

  .mono {
    color: var(--faint);
    font-size: 9.5px;
  }

  &:hover {
    color: var(--cyan);
    background: rgba(62, 231, 255, 0.05);
    text-decoration: none;
  }
}

.year-block {
  padding-bottom: 28px;

  & + & {
    margin-top: 10px;
    padding-top: 28px;
    border-top: 1px solid var(--line);
  }
}

.year-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 14px;

  h2 {
    font-family: var(--mono);
    font-size: 24px;
    font-weight: 600;
    letter-spacing: -0.02em;
  }

  .mono {
    color: var(--faint);
    font-size: 9.5px;
  }
}

.month-block {
  margin-bottom: 16px;
}

.month-title {
  margin-bottom: 4px;
  color: var(--faint);
  font-family: var(--mono);
  font-size: 10.5px;
  font-weight: 500;
  letter-spacing: 0.1em;
}

.entries {
  margin: 0;
  padding: 0;
  list-style: none;
}

.entries li {
  display: flex;
  align-items: baseline;
  gap: 14px;
  padding: 9px 6px;
  border-bottom: 1px dashed var(--line);
  transition: background 0.16s;

  &:hover {
    background: rgba(255, 255, 255, 0.022);
  }
}

.date {
  flex: none;
  width: 42px;
  color: var(--faint);
  font-size: 10px;
}

.entry-title {
  color: var(--text);
  font-size: 13.5px;
  line-height: 1.5;

  &:hover {
    color: var(--cyan);
    text-decoration: none;
  }
}

.cat {
  margin-left: auto;
  flex: none;
  color: var(--faint);
  font-family: var(--mono);
  font-size: 9.5px;
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

@media (max-width: 768px) {
  .archive-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .years {
    position: static;
    flex-direction: row;
    flex-wrap: wrap;
    padding-left: 0;
    border-left: 0;
    gap: 8px;
  }

  .year-link {
    gap: 8px;
    padding: 4px 10px;
    border: 1px solid var(--line);
    border-radius: 999px;
  }

  .entries li {
    flex-wrap: wrap;
    gap: 6px 10px;
  }

  .cat {
    margin-left: 0;
    width: 100%;
  }
}
</style>
