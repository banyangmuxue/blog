<template>
  <div class="admin-shell">
    <header class="admin-header">
      <div class="admin-header-inner">
        <router-link :to="{ name: 'admin-posts' }" class="admin-brand">
          <span class="brand-mark">
            <img v-if="logoUrl" :src="logoUrl" alt="站点 Logo" />
            <template v-else>{{ logoText }}</template>
          </span>
          <span class="brand-text">
            <strong>管理后台</strong>
            <small>BLOG CONSOLE</small>
          </span>
        </router-link>

        <nav class="admin-nav" aria-label="管理导航">
          <router-link :to="{ name: 'admin-posts' }">文章管理</router-link>
          <router-link :to="{ name: 'admin-profile' }">个人资料</router-link>
          <router-link :to="{ name: 'admin-site' }">站点设置</router-link>
        </nav>

        <div class="admin-actions">
          <router-link to="/" target="_blank" class="admin-btn ghost small">
            查看博客 ↗
          </router-link>
          <button type="button" class="admin-btn ghost small" @click="logout">退出登录</button>
        </div>
      </div>
    </header>

    <main class="admin-main">
      <router-view />
    </main>

    <ToastHost />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import { useSite } from '@/composables/useSite'
import ToastHost from '@/components/common/ToastHost.vue'

const router = useRouter()
const { state: site, loadSite } = useSite()

const logoUrl = computed(() => site.config.logoUrl)
const logoText = computed(() => site.config.logoText || 'B/')

onMounted(() => {
  if (!site.loaded) loadSite()
})

async function logout() {
  try {
    await api.adminLogout()
  } catch {
    // 会话已失效时也直接回到登录页
  }
  router.replace({ name: 'admin-login' })
}
</script>

<style scoped lang="scss">
.admin-shell {
  display: flex;
  min-height: 100vh;
  flex-direction: column;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 30;
  border-bottom: 1px solid var(--line);
  background: rgba(6, 9, 15, 0.82);
  backdrop-filter: blur(16px);
}

.admin-header-inner {
  display: flex;
  width: 100%;
  min-height: var(--header-h);
  max-width: var(--container);
  margin: 0 auto;
  padding: 0 28px;
  align-items: center;
  gap: 34px;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 11px;
  color: var(--text);

  &:hover {
    text-decoration: none;
  }
}

.brand-mark {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border-radius: 9px;
  background: linear-gradient(135deg, var(--cyan), var(--violet));
  color: #04121a;
  font-family: var(--mono);
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 0 22px -10px var(--cyan);
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;

  strong {
    font-size: 14px;
    letter-spacing: 0.02em;
  }

  small {
    margin-top: 3px;
    color: var(--faint);
    font-family: var(--mono);
    font-size: 9px;
    letter-spacing: 0.14em;
  }
}

.admin-nav {
  display: flex;
  align-items: center;
  gap: 4px;

  a {
    padding: 7px 12px;
    border-radius: 7px;
    color: var(--muted);
    font-size: 13px;

    &:hover {
      color: var(--text);
      text-decoration: none;
    }

    &.router-link-active {
      color: var(--cyan);
      background: rgba(62, 231, 255, 0.07);
    }
  }
}

.admin-actions {
  display: flex;
  margin-left: auto;
  align-items: center;
  gap: 8px;
}

.admin-main {
  flex: 1;
  padding: 38px 0 80px;
}

@media (max-width: 720px) {
  .admin-header-inner {
    padding: 0 18px;
    gap: 16px;
  }

  .brand-text small {
    display: none;
  }

  .admin-nav {
    display: none;
  }

  .admin-actions .ghost:first-child {
    display: none;
  }

  .admin-main {
    padding-top: 26px;
  }
}
</style>
