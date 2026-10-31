<template>
  <transition-group name="toast" tag="div" class="toast-host" aria-live="polite">
    <div
      v-for="item in toasts"
      :key="item.id"
      class="toast"
      :class="item.type"
      role="status"
    >
      <span class="dot" aria-hidden="true"></span>
      <span class="message">{{ item.message }}</span>
      <button type="button" class="close" aria-label="关闭提示" @click="dismissToast(item.id)">
        ×
      </button>
    </div>
  </transition-group>
</template>

<script setup>
import { useToast } from '@/composables/useToast'

const { toasts, dismissToast } = useToast()
</script>

<style scoped lang="scss">
.toast-host {
  position: fixed;
  top: calc(var(--header-h) + 16px);
  right: 24px;
  z-index: 70;
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-width: min(360px, calc(100vw - 36px));
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: 9px;
  padding: 11px 12px 11px 14px;
  border: 1px solid var(--line-strong);
  border-left-width: 2px;
  border-radius: 10px;
  background: rgba(13, 21, 32, 0.97);
  box-shadow: 0 16px 40px -20px #000;
  color: var(--text);
  font-size: 12.5px;
  line-height: 1.6;
  pointer-events: auto;

  .dot {
    width: 6px;
    height: 6px;
    margin-top: 7px;
    flex: none;
    border-radius: 50%;
    background: var(--green);
  }

  &.error {
    border-left-color: var(--red);

    .dot {
      background: var(--red);
    }
  }

  &.info {
    border-left-color: var(--cyan);

    .dot {
      background: var(--cyan);
    }
  }
}

.message {
  flex: 1;
}

.close {
  margin-left: 4px;
  color: var(--faint);
  font-size: 14px;
  line-height: 1.2;

  &:hover {
    color: var(--text);
  }
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.2s var(--ease), transform 0.2s var(--ease);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(12px);
}

@media (max-width: 768px) {
  .toast-host {
    right: 18px;
    left: 18px;
    max-width: none;
  }
}
</style>
