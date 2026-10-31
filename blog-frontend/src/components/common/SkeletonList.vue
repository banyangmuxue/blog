<template>
  <div class="skeleton-list" aria-hidden="true">
    <div v-for="index in rows" :key="index" class="skeleton-row">
      <div class="skeleton cover"></div>
      <div class="content">
        <div class="skeleton line w40"></div>
        <div class="skeleton line w80"></div>
        <div class="skeleton line w60"></div>
        <div class="skeleton line w30"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  rows: { type: Number, default: 4 }
})
</script>

<style scoped lang="scss">
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-row {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.4);
}

.cover {
  width: 176px;
  height: 108px;
  flex: none;
  border-radius: 10px;
}

.content {
  flex: 1;
}

.line {
  height: 10px;
  margin-bottom: 10px;
}

.w30 {
  width: 30%;
}

.w40 {
  width: 40%;
}

.w60 {
  width: 60%;
}

.w80 {
  width: 80%;
}

@media (max-width: 640px) {
  .skeleton-row {
    flex-direction: column;
  }

  .cover {
    width: 100%;
    height: 148px;
  }
}
</style>
