<template>
  <div class="guestbook page-enter">
    <PageHero
      eyebrow="GUESTBOOK"
      title="留言板"
      description="不用注册即可留言。可以说点什么，也可以只留个脚印。"
      :parent="{ label: '关于', to: '/about' }"
    />

    <div class="container">
      <div class="narrow">
        <section class="form-section">
          <div class="section-head">
            <h2>留下你的信息</h2>
            <span v-if="site.config.features.review" class="review mono">先审后发 · 通常一天内处理</span>
          </div>
          <CommentForm
            target="guestbook"
            :disabled="!site.config.features.guestbook"
            :review-required="site.config.features.review"
            :max-length="300"
            placeholder="想说的话、建议，或者只是一个问候…"
            @submitted="reload"
          />
        </section>

        <section class="list-section">
          <div class="section-head">
            <h2>全部留言 <span class="mono">{{ total }}</span></h2>
            <span class="mono page-info">{{ page }} / {{ pages }}</span>
          </div>

          <div v-if="loading" class="loading">
            <div class="skeleton line w30"></div>
            <div class="skeleton line w80"></div>
            <div class="skeleton line w60"></div>
          </div>

          <div v-else-if="error" class="error-box">
            <p>{{ error }}</p>
            <button type="button" class="retry" @click="load">重新加载</button>
          </div>

          <template v-else-if="messages.length">
            <CommentList :comments="messages" />
            <AppPagination :page="page" :pages="pages" @update:page="changePage" />
          </template>

          <p v-else class="empty-hint">还没有公开留言，来写第一条吧。</p>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { loadSite, useSite } from '@/composables/useSite'
import PageHero from '@/components/common/PageHero.vue'
import CommentForm from '@/components/common/CommentForm.vue'
import CommentList from '@/components/common/CommentList.vue'
import AppPagination from '@/components/common/AppPagination.vue'

const { state: site } = useSite()

const messages = ref([])
const total = ref(0)
const pages = ref(1)
const page = ref(1)
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await api.getMessages({ page: page.value, size: 10 })
    messages.value = result.items
    total.value = result.total
    pages.value = result.pages
    page.value = result.page
  } catch (err) {
    error.value = err.message || '留言加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function changePage(next) {
  page.value = next
  load()
}

async function reload() {
  page.value = 1
  await load()
}

onMounted(async () => {
  await loadSite()
  load()
})
</script>

<style scoped lang="scss">
.narrow {
  max-width: 820px;
  margin: 0 auto;
}

.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;

  h2 {
    font-size: 19px;

    .mono {
      margin-left: 6px;
      color: var(--faint);
      font-size: 10px;
    }
  }
}

.review {
  color: var(--faint);
  font-size: 9.5px;
  letter-spacing: 0.05em;
}

.page-info {
  color: var(--faint);
  font-size: 9.5px;
}

.list-section {
  margin-top: 36px;
  padding-top: 26px;
  border-top: 1px solid var(--line);
}

.loading {
  padding: 12px 0;

  .line {
    height: 10px;
    margin-bottom: 10px;
  }

  .w30 {
    width: 30%;
  }

  .w60 {
    width: 60%;
  }

  .w80 {
    width: 80%;
  }
}

.empty-hint {
  padding: 28px 0;
  color: var(--faint);
  font-size: 12.5px;
  text-align: center;
}

.error-box {
  padding: 32px 20px;
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
