<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-bottom">
        <span>© {{ year }} {{ site.config.author || site.config.name }}</span>
        <span class="mono">{{ site.config.icp }}</span>
        <span class="build">Vue 3 · Spring Boot</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useSite } from '@/composables/useSite'

const { state: site } = useSite()
const year = computed(() => new Date().getFullYear())
</script>

<style scoped lang="scss">
.footer {
  border-top: 1px solid var(--line);
  background: rgba(6, 9, 15, 0.6);
  padding: 40px 0 28px;
}

.footer-bottom {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 24px;
  color: var(--faint);
  font-size: 11.5px;

  .mono {
    color: var(--faint);
    font-size: 10.5px;
  }

  .build {
    margin-left: auto;
    font-family: var(--mono);
    font-size: 10px;
    letter-spacing: 0.06em;
  }
}

@media (max-width: 768px) {
  .footer {
    padding-top: 28px;
  }

  .footer-bottom {
    .build {
      margin-left: 0;
      width: 100%;
    }
  }
}
</style>
