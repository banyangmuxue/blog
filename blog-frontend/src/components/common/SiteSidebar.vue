<template>
  <aside class="sidebar">
    <section class="card author">
      <div class="avatar">
        <img
          v-if="profile?.avatarUrl"
          :src="profile.avatarUrl"
          :alt="profile.name || '博主头像'"
        />
        <span v-else>{{ avatarText }}</span>
      </div>
      <div class="who">
        <h3>{{ profile?.name || '林深' }}</h3>
        <p>{{ profile?.bio || '写代码，也写字。' }}</p>
      </div>
      <router-link to="/about" class="more">关于我 →</router-link>
    </section>

    <section class="card">
      <h4>分类 <span class="mono">{{ categories.length }}</span></h4>
      <ul class="cat-list">
        <li v-for="category in categories" :key="category.slug">
          <router-link :to="`/categories/${category.slug}`">
            <span>{{ category.name }}</span>
            <span class="count">{{ category.count }}</span>
          </router-link>
        </li>
      </ul>
    </section>

    <section class="card">
      <h4>标签</h4>
      <div class="tag-cloud">
        <TagPill v-for="tag in topTags" :key="tag.slug" :tag="tag" show-count />
      </div>
      <router-link to="/tags" class="more">全部标签 →</router-link>
    </section>

    <section class="card">
      <h4>归档</h4>
      <p class="hint">按年、月浏览全部文章。</p>
      <router-link to="/archive" class="more">查看归档 →</router-link>
    </section>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import TagPill from '@/components/common/TagPill.vue'
import { useSite } from '@/composables/useSite'

const { state: site } = useSite()

const profile = computed(() => site.profile)
const categories = computed(() => site.categories)
const topTags = computed(() => site.tags.slice(0, 8))
const avatarText = computed(() => {
  if (site.profile?.avatarText) return site.profile.avatarText
  return site.profile?.name?.slice(0, 1) || 'B'
})
</script>

<style scoped lang="scss">
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card {
  padding: 18px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.5);

  h4 {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    font-family: var(--mono);
    font-size: 10px;
    font-weight: 500;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: var(--faint);
  }
}

.author {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 10px 12px;
  align-items: center;
}

.avatar {
  display: grid;
  overflow: hidden;
  width: 44px;
  height: 44px;
  place-items: center;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.4), rgba(139, 92, 246, 0.5));
  color: #04121a;
  font-family: var(--mono);
  font-size: 15px;
  font-weight: 700;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.who {
  h3 {
    font-size: 14px;
  }

  p {
    margin-top: 3px;
    color: var(--faint);
    font-size: 11.5px;
    line-height: 1.6;
  }
}

.more {
  grid-column: 1 / -1;
  color: var(--muted);
  font-size: 11.5px;

  &:hover {
    color: var(--cyan);
    text-decoration: none;
  }
}

.cat-list {
  margin: 0;
  padding: 0;
  list-style: none;

  li + li {
    border-top: 1px dashed var(--line);
  }

  a {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 7px 0;
    color: var(--muted);
    font-size: 12.5px;

    &:hover {
      color: var(--cyan);
      text-decoration: none;
    }
  }

  .count {
    font-family: var(--mono);
    font-size: 10px;
    color: var(--faint);
  }
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 10px;
}

.hint {
  margin-bottom: 10px;
  color: var(--faint);
  font-size: 11.5px;
}
</style>
