<template>
  <div class="links-page page-enter">
    <PageHero
      eyebrow="LINKS"
      title="友链"
      description="一些我长期阅读的博客与站点。它们大多和我在做同一件事：把想法写清楚。"
    >
      <template #actions>
        <span class="count mono">{{ links.length }} SITES</span>
      </template>
    </PageHero>

    <div class="container">
      <div v-if="loading" class="link-grid">
        <div v-for="index in 4" :key="index" class="link-card skeleton-card">
          <div class="skeleton avatar-skeleton"></div>
          <div class="skeleton line w40"></div>
          <div class="skeleton line w80"></div>
        </div>
      </div>

      <div v-else-if="error" class="error-box">
        <p>{{ error }}</p>
        <button type="button" class="retry" @click="load">重新加载</button>
      </div>

      <div v-else-if="links.length" class="link-grid">
        <a
          v-for="link in links"
          :key="link.id"
          :href="link.url"
          target="_blank"
          rel="noopener noreferrer"
          class="link-card"
        >
          <div class="card-top">
            <span class="avatar">{{ link.avatarText || link.name.slice(0, 1) }}</span>
            <span class="arrow" aria-hidden="true">↗</span>
          </div>
          <h3>{{ link.name }}</h3>
          <p class="domain mono">{{ link.domain }}</p>
          <p class="desc">{{ link.description }}</p>
        </a>
      </div>

      <EmptyState v-else title="还没有友链" description="友链会在这里展示，欢迎稍后再来。" />

      <div class="apply">
        <div>
          <span class="eyebrow">EXCHANGE</span>
          <h3>交换友链</h3>
          <p>如果你的博客也在持续更新，欢迎来信。请附上站点名称、地址与一句简介。</p>
        </div>
        <a :href="applyMail" class="apply-btn">写信申请 →</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/api'
import { loadSite, useSite } from '@/composables/useSite'
import PageHero from '@/components/common/PageHero.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const { state: site } = useSite()
const links = ref([])
const loading = ref(true)
const error = ref('')

const applyMail = computed(() => {
  const item = (site.config.socials || []).find((social) => social.url.startsWith('mailto:'))
  const address = item ? item.url.replace('mailto:', '') : 'hi@example.com'
  return `mailto:${address}?subject=${encodeURIComponent('友链申请')}`
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    links.value = await api.getLinks()
  } catch (err) {
    error.value = err.message || '友链加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

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

.link-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.link-card {
  display: flex;
  flex-direction: column;
  padding: 18px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.42);
  transition: border-color 0.16s, transform 0.16s var(--ease), background 0.16s;

  &:hover {
    border-color: var(--line-strong);
    background: rgba(17, 27, 41, 0.62);
    transform: translateY(-2px);
    text-decoration: none;

    h3 {
      color: var(--cyan);
    }

    .arrow {
      color: var(--cyan);
    }
  }

  h3 {
    margin: 12px 0 2px;
    color: var(--text);
    font-size: 14.5px;
    font-weight: 600;
    transition: color 0.16s;
  }

  .domain {
    color: var(--faint);
    font-size: 9.5px;
  }

  .desc {
    margin-top: 9px;
    color: var(--muted);
    font-size: 12px;
    line-height: 1.7;
  }
}

.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.avatar {
  display: grid;
  width: 36px;
  height: 36px;
  place-items: center;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.32), rgba(139, 92, 246, 0.42));
  color: var(--text);
  font-family: var(--mono);
  font-size: 13px;
}

.arrow {
  color: var(--faint);
  font-size: 14px;
  transition: color 0.16s;
}

.skeleton-card {
  gap: 10px;

  &:hover {
    transform: none;
  }
}

.avatar-skeleton {
  width: 36px;
  height: 36px;
  border-radius: 10px;
}

.line {
  height: 10px;
}

.w40 {
  width: 40%;
}

.w80 {
  width: 80%;
}

.apply {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-top: 36px;
  padding: 22px 24px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.42);

  h3 {
    margin-top: 6px;
    font-size: 17px;
  }

  p {
    max-width: 44em;
    margin-top: 4px;
    color: var(--muted);
    font-size: 12px;
  }
}

.apply-btn {
  flex: none;
  display: inline-flex;
  align-items: center;
  height: 34px;
  padding: 0 15px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  color: var(--muted);
  font-size: 12px;
  transition: 0.16s;

  &:hover {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.36);
    background: rgba(62, 231, 255, 0.06);
    text-decoration: none;
  }
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

@media (max-width: 900px) {
  .link-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 600px) {
  .link-grid {
    grid-template-columns: 1fr;
  }

  .apply {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
