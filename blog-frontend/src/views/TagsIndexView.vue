<template>
  <div class="tags-page page-enter">
    <PageHero
      eyebrow="TAGS"
      title="标签"
      description="按主题聚合文章。标签比分类更细，适合跨分类地追一条线索。"
      :parent="{ label: '文章', to: '/articles' }"
    >
      <template #actions>
        <span class="count mono">{{ tags.length }} TAGS</span>
      </template>
    </PageHero>

    <div class="container">
      <div class="category-row">
        <router-link
          v-for="category in categories"
          :key="category.slug"
          :to="`/categories/${category.slug}`"
          class="category-chip"
        >
          <span>{{ category.name }}</span>
          <span class="mono">{{ category.count }}</span>
        </router-link>
      </div>

      <div v-if="tags.length" class="tag-grid">
        <router-link
          v-for="tag in tags"
          :key="tag.slug"
          :to="`/tags/${tag.slug}`"
          class="tag-card"
        >
          <span class="hash mono">#</span>
          <span class="name">{{ tag.name }}</span>
          <span class="count mono">{{ tag.count }} 篇</span>
        </router-link>
      </div>

      <EmptyState
        v-if="site.loaded && !tags.length"
        title="还没有标签"
        description="发布文章并添加标签后，这里会自动聚合。"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { loadSite, useSite } from '@/composables/useSite'
import PageHero from '@/components/common/PageHero.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const { state: site } = useSite()
const tags = computed(() => site.tags)
const categories = computed(() => site.categories)

onMounted(() => {
  loadSite()
})
</script>

<style scoped lang="scss">
.count {
  color: var(--faint);
  font-size: 10.5px;
}

.category-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 28px;
  padding-bottom: 22px;
  border-bottom: 1px solid var(--line);
}

.category-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 30px;
  padding: 0 12px;
  border: 1px solid var(--line);
  border-radius: 999px;
  color: var(--muted);
  font-size: 12px;
  transition: 0.16s;

  .mono {
    color: var(--faint);
    font-size: 9.5px;
  }

  &:hover {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.36);
    background: rgba(62, 231, 255, 0.06);
    text-decoration: none;
  }
}

.tag-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.tag-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 18px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: rgba(13, 21, 32, 0.4);
  transition: border-color 0.16s, background 0.16s;

  .hash {
    color: var(--cyan);
    font-size: 12px;
  }

  .name {
    color: var(--text);
    font-size: 13.5px;
  }

  .count {
    margin-left: auto;
    color: var(--faint);
    font-size: 9.5px;
  }

  &:hover {
    border-color: var(--line-strong);
    background: rgba(17, 27, 41, 0.62);
    text-decoration: none;

    .name {
      color: var(--cyan);
    }
  }
}

@media (max-width: 900px) {
  .tag-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .tag-grid {
    grid-template-columns: 1fr;
  }
}
</style>
