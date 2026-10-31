<template>
  <div class="site-shell">
    <SiteHeader />
    <main class="site-main">
      <router-view />
    </main>
    <SiteFooter />
    <ToastHost />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import SiteHeader from '@/components/layout/SiteHeader.vue'
import SiteFooter from '@/components/layout/SiteFooter.vue'
import ToastHost from '@/components/common/ToastHost.vue'
import { loadSite } from '@/composables/useSite'

onMounted(() => {
  loadSite()
})
</script>

<style scoped lang="scss">
.site-shell {
  display: flex;
  min-height: 100vh;
  flex-direction: column;
}

.site-main {
  flex: 1;
  padding-bottom: 80px;
}
</style>
