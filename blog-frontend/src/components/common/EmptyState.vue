<template>
  <div class="empty">
    <div class="glyph" aria-hidden="true">
      <span></span><span></span><span></span>
    </div>
    <h3>{{ title }}</h3>
    <p v-if="description">{{ description }}</p>
    <slot name="action">
      <router-link v-if="to" :to="to" class="action">{{ action }}</router-link>
    </slot>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '这里还没有内容' },
  description: { type: String, default: '' },
  action: { type: String, default: '' },
  to: { type: String, default: '' }
})
</script>

<style scoped lang="scss">
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 56px 24px;
  border: 1px dashed var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.35);
  text-align: center;
}

.glyph {
  display: flex;
  gap: 5px;
  margin-bottom: 16px;

  span {
    display: block;
    width: 20px;
    height: 3px;
    border-radius: 2px;
    background: rgba(62, 231, 255, 0.24);
  }

  span:nth-child(2) {
    width: 10px;
    background: rgba(139, 92, 246, 0.34);
  }
}

h3 {
  font-size: 15px;
  font-weight: 600;
}

p {
  max-width: 32em;
  margin: 6px 0 16px;
  color: var(--muted);
  font-size: 12.5px;
  line-height: 1.7;
}

.action {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 13px;
  border: 1px solid rgba(62, 231, 255, 0.32);
  border-radius: var(--radius);
  background: rgba(62, 231, 255, 0.08);
  color: var(--cyan);
  font-size: 12px;

  &:hover {
    background: rgba(62, 231, 255, 0.14);
    text-decoration: none;
  }
}
</style>
