<template>
  <article class="post-card" :class="variant">
    <router-link
      v-if="variant !== 'compact'"
      :to="`/post/${post.slug}`"
      class="cover-link"
      tabindex="-1"
      aria-hidden="true"
    >
      <PostCover :post="post" :ratio="variant === 'pinned' ? 'wide' : 'row'" />
    </router-link>

    <div class="body">
      <div class="topline">
        <span v-if="post.category" class="cat">{{ post.category.name }}</span>
        <span class="date mono">{{ formatDate(post.publishedAt) }}</span>
      </div>

      <h3 class="title">
        <router-link :to="`/post/${post.slug}`" class="stretched">
          {{ post.title }}
        </router-link>
      </h3>

      <p class="excerpt">{{ post.excerpt }}</p>

      <div class="meta">
        <span class="mono">{{ post.readingTime }} MIN READ</span>
        <span v-if="variant !== 'compact'" class="mono views">{{ formatNumber(post.views) }} 次阅读</span>
        <div class="tags">
          <TagPill v-for="tag in post.tags.slice(0, 3)" :key="tag.slug" :tag="tag" />
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import PostCover from '@/components/common/PostCover.vue'
import TagPill from '@/components/common/TagPill.vue'
import { formatDate, formatNumber } from '@/utils/format'

defineProps({
  post: { type: Object, required: true },
  variant: { type: String, default: 'row' }
})
</script>

<style scoped lang="scss">
.post-card {
  position: relative;
  display: flex;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: linear-gradient(180deg, rgba(17, 27, 41, 0.52), rgba(10, 16, 24, 0.4));
  transition: border-color 0.16s, background 0.16s;

  &:hover {
    border-color: var(--line-strong);
    background: rgba(17, 27, 41, 0.62);

    .title a {
      color: var(--cyan);
    }
  }

  &.row {
    gap: 18px;
    padding: 16px;
  }

  &.pinned {
    display: block;
    overflow: hidden;
    transition: border-color 0.16s, transform 0.16s var(--ease);

    &:hover {
      transform: translateY(-2px);
    }

    .cover-link {
      display: block;
    }

    .body {
      padding: 15px 16px 16px;
    }
  }

  &.compact {
    padding: 16px 18px;
  }
}

.cover-link {
  flex: none;

  &:hover {
    text-decoration: none;
  }
}

.body {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
}

.title {
  margin: 6px 0 6px;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: -0.01em;
  line-height: 1.45;

  a {
    color: var(--text);
    transition: color 0.16s;

    &:hover {
      text-decoration: none;
    }

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      z-index: 0;
    }
  }
}

.topline {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cat {
  padding: 1px 7px;
  border: 1px solid rgba(139, 92, 246, 0.28);
  border-radius: 4px;
  background: rgba(139, 92, 246, 0.08);
  color: #b6a4ff;
  font-family: var(--mono);
  font-size: 9.5px;
  letter-spacing: 0.06em;
}

.date {
  color: var(--faint);
  font-size: 10px;
}

.excerpt {
  display: -webkit-box;
  overflow: hidden;
  color: var(--muted);
  font-size: 12.5px;
  line-height: 1.7;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px 12px;
  margin-top: auto;
  padding-top: 12px;
  color: var(--faint);
  font-size: 10px;

  .views {
    color: var(--faint);
  }

  .tags {
    position: relative;
    z-index: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    margin-left: auto;
  }
}

.compact .body {
  padding: 0;
}

.compact .title {
  margin-top: 0;
}

@media (max-width: 640px) {
  .post-card.row {
    flex-direction: column;
  }

  .post-card.row .cover-link {
    width: 100%;
  }

  :deep(.ratio-row) {
    width: 100%;
    height: 148px;
  }
}
</style>
