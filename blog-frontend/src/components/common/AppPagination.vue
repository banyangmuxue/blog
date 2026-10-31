<template>
  <nav v-if="pages > 1" class="pagination" aria-label="分页">
    <button
      type="button"
      class="page-btn arrow"
      :disabled="page <= 1"
      aria-label="上一页"
      @click="go(page - 1)"
    >
      ‹
    </button>
    <button
      v-for="item in visiblePages"
      :key="item.key"
      type="button"
      class="page-btn"
      :class="{ active: item.page === page, dots: !item.page }"
      :disabled="!item.page"
      @click="item.page && go(item.page)"
    >
      {{ item.page || '…' }}
    </button>
    <button
      type="button"
      class="page-btn arrow"
      :disabled="page >= pages"
      aria-label="下一页"
      @click="go(page + 1)"
    >
      ›
    </button>
  </nav>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  page: { type: Number, default: 1 },
  pages: { type: Number, default: 1 }
})

const emit = defineEmits(['update:page'])

const visiblePages = computed(() => {
  const total = props.pages
  const current = props.page
  if (total <= 7) {
    return Array.from({ length: total }, (_, index) => ({ key: index + 1, page: index + 1 }))
  }
  const items = [{ key: 'p1', page: 1 }]
  const start = Math.max(2, current - 1)
  const end = Math.min(total - 1, current + 1)
  if (start > 2) items.push({ key: 'left-dots', page: 0 })
  for (let index = start; index <= end; index += 1) {
    items.push({ key: `p${index}`, page: index })
  }
  if (end < total - 1) items.push({ key: 'right-dots', page: 0 })
  items.push({ key: `p${total}`, page: total })
  return items
})

function go(next) {
  if (next < 1 || next > props.pages || next === props.page) return
  emit('update:page', next)
}
</script>

<style scoped lang="scss">
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 28px;
}

.page-btn {
  display: grid;
  min-width: 34px;
  height: 34px;
  padding: 0 9px;
  place-items: center;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: transparent;
  color: var(--muted);
  font-family: var(--mono);
  font-size: 11.5px;
  transition: 0.16s;

  &:hover:not(:disabled) {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.36);
    background: rgba(62, 231, 255, 0.06);
  }

  &.active {
    color: var(--cyan);
    border-color: rgba(62, 231, 255, 0.45);
    background: rgba(62, 231, 255, 0.1);
  }

  &.dots {
    border-color: transparent;
    color: var(--faint);
    cursor: default;
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}
</style>
