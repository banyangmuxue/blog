<template>
  <router-link
    v-if="tag"
    :to="`/tags/${tag.slug}`"
    class="tag"
    :class="{ active }"
  >
    {{ tag.name }}
    <span v-if="showCount && tag.count" class="count">{{ tag.count }}</span>
  </router-link>
</template>

<script setup>
defineProps({
  tag: { type: Object, default: null },
  active: { type: Boolean, default: false },
  showCount: { type: Boolean, default: false }
})
</script>

<style scoped lang="scss">
.tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 24px;
  padding: 0 9px;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.02);
  color: var(--muted);
  font-size: 11px;
  white-space: nowrap;
  transition: color 0.16s, border-color 0.16s, background 0.16s;

  &:hover {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.4);
    background: rgba(62, 231, 255, 0.07);
    text-decoration: none;
  }

  &.active {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.4);
    background: rgba(62, 231, 255, 0.08);
  }

  .count {
    font-family: var(--mono);
    font-size: 9px;
    color: var(--faint);
  }
}
</style>
