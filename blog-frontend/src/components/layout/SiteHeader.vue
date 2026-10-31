<template>
  <header class="header">
    <div class="container header-inner">
      <router-link to="/" class="brand" aria-label="返回首页">
        <span class="logo">
          <img v-if="site.config.logoUrl" :src="site.config.logoUrl" alt="站点 Logo" />
          <template v-else>{{ site.config.logoText || 'B/' }}</template>
        </span>
        <span class="brand-text">
          <strong>{{ site.config.shortName }} {{ site.config.title || '记录代码与生活' }}</strong>
          <small>PERSONAL BLOG · EST. {{ site.config.foundedAt || '2023' }}</small>
        </span>
      </router-link>

      <nav class="nav" aria-label="主导航">
        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="nav-link"
          :class="{ active: isActive(item) }"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="actions">
        <router-link to="/search" class="icon-btn" aria-label="搜索文章">
          <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
            <circle cx="10.5" cy="10.5" r="6.5" fill="none" stroke="currentColor" stroke-width="1.6" />
            <path d="m15.5 15.5 4 4" stroke="currentColor" stroke-width="1.6" />
          </svg>
        </router-link>
        <button
          class="icon-btn menu-btn"
          type="button"
          :aria-expanded="mobileOpen"
          aria-label="打开导航菜单"
          @click="toggleMenu"
        >
          <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
            <path d="M4 7h16M4 12h16M4 17h16" stroke="currentColor" stroke-width="1.6" />
          </svg>
        </button>
      </div>
    </div>

    <transition name="drawer">
      <div v-if="mobileOpen" class="mobile-panel">
        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="mobile-link"
          :class="{ active: isActive(item) }"
        >
          {{ item.label }}
        </router-link>
        <router-link to="/search" class="mobile-link">搜索文章</router-link>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { useSite } from '@/composables/useSite'

const route = useRoute()
const { state: site } = useSite()
const mobileOpen = ref(false)

const navItems = [
  { label: '首页', to: '/', match: ['/'] },
  { label: '文章', to: '/articles', match: ['/articles', '/post', '/categories', '/tags', '/search'] },
  { label: '归档', to: '/archive', match: ['/archive'] },
  { label: '友链', to: '/links', match: ['/links'] },
  { label: '留言板', to: '/guestbook', match: ['/guestbook'] },
  { label: '关于', to: '/about', match: ['/about'] }
]

function isActive(item) {
  return item.match.some((prefix) =>
    prefix === '/' ? route.path === '/' : route.path.startsWith(prefix)
  )
}

function toggleMenu() {
  mobileOpen.value = !mobileOpen.value
}

watch(mobileOpen, (open) => {
  document.body.style.overflow = open ? 'hidden' : ''
})

watch(
  () => route.path,
  () => {
    mobileOpen.value = false
  }
)

onBeforeUnmount(() => {
  document.body.style.overflow = ''
})
</script>

<style scoped lang="scss">
.header {
  position: sticky;
  top: 0;
  z-index: 40;
  background: rgba(6, 9, 15, 0.94);
  border-bottom: 1px solid var(--line);
  will-change: transform;
}

.header-inner {
  display: flex;
  height: var(--header-h);
  align-items: center;
  gap: 28px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text);

  &:hover {
    text-decoration: none;
  }
}

.logo {
  display: grid;
  width: 34px;
  height: 34px;
  flex: none;
  place-items: center;
  border-radius: 9px;
  background: linear-gradient(135deg, var(--cyan), var(--violet));
  color: #04121a;
  font-family: var(--mono);
  font-size: 15px;
  font-weight: 700;
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
  line-height: 1.25;

  strong {
    font-size: 14px;
    font-weight: 600;
    letter-spacing: -0.01em;
  }

  small {
    font-family: var(--mono);
    font-size: 9px;
    letter-spacing: 0.08em;
    color: var(--faint);
  }
}

.nav {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.nav-link {
  position: relative;
  padding: 8px 12px;
  color: var(--muted);
  font-size: 13px;
  transition: color 0.16s;

  &::after {
    content: '';
    position: absolute;
    left: 12px;
    right: 12px;
    bottom: 2px;
    height: 1px;
    background: var(--cyan);
    transform: scaleX(0);
    transform-origin: left;
    transition: transform 0.2s var(--ease);
  }

  &:hover {
    color: var(--text);
    text-decoration: none;
  }

  &.active {
    color: var(--cyan);

    &::after {
      transform: scaleX(1);
    }
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.icon-btn {
  display: grid;
  width: 36px;
  height: 36px;
  place-items: center;
  border: 1px solid var(--line);
  border-radius: 8px;
  color: var(--muted);
  transition: color 0.16s, border-color 0.16s, background 0.16s;

  &:hover {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.35);
    background: rgba(62, 231, 255, 0.06);
    text-decoration: none;
  }
}

.menu-btn {
  display: none;
}

.mobile-panel {
  display: none;
  padding: 8px 18px 18px;
  border-top: 1px solid var(--line);
  background: rgba(8, 13, 21, 0.98);
}

.mobile-link {
  display: block;
  padding: 12px 4px;
  border-bottom: 1px solid var(--line);
  color: var(--muted);
  font-size: 14px;

  &.active {
    color: var(--cyan);
  }

  &:hover {
    text-decoration: none;
    color: var(--text);
  }
}

.drawer-enter-active,
.drawer-leave-active {
  transition: opacity 0.18s, transform 0.18s var(--ease);
}

.drawer-enter-from,
.drawer-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (max-width: 900px) {
  .nav {
    display: none;
  }

  .actions {
    margin-left: auto;
  }

  .menu-btn {
    display: grid;
  }

  .mobile-panel {
    display: block;
  }

  .brand-text small {
    display: none;
  }
}

@media (max-width: 480px) {
  .header-inner {
    gap: 12px;
  }

  .brand-text strong {
    font-size: 13px;
  }
}
</style>
