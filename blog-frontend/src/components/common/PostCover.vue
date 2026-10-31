<template>
  <div
    class="cover"
    :class="[`ratio-${ratio}`, { small }]"
    :style="{ background: gradient }"
    aria-hidden="true"
  >
    <img
      v-if="coverImage && !imageFailed"
      class="cover-image"
      :src="coverImage"
      alt=""
      loading="lazy"
      @error="imageFailed = true"
    />
    <span v-if="coverImage && !imageFailed" class="cover-shade"></span>
    <span class="cover-grid"></span>
    <span class="cover-mark">{{ post.coverLabel || 'B/' }}</span>
    <span class="cover-cat mono">{{ post.category ? post.category.name : 'BLOG' }}</span>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  post: { type: Object, required: true },
  ratio: { type: String, default: 'wide' },
  small: { type: Boolean, default: false }
})

const imageFailed = ref(false)
const coverImage = computed(() => props.post.cover?.image || '')

watch(coverImage, () => {
  imageFailed.value = false
})

const gradient = computed(() => {
  const from = props.post.cover?.from || '#123043'
  const to = props.post.cover?.to || '#2a1b4d'
  return `linear-gradient(135deg, ${from}, ${to})`
})
</script>

<style scoped lang="scss">
.cover {
  position: relative;
  overflow: hidden;
  border-radius: 10px;
  border: 1px solid rgba(125, 211, 252, 0.14);

  &.ratio-wide {
    aspect-ratio: 16 / 9;
  }

  &.ratio-row {
    width: 176px;
    height: 108px;
    flex: none;
  }

  &.small {
    border-radius: 8px;
  }
}

.cover-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(6, 9, 15, 0.08) 0%, rgba(6, 9, 15, 0.5) 100%);
}

.cover-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 22px 22px;
  mask-image: radial-gradient(circle at 30% 20%, #000 20%, transparent 75%);
}

.cover-mark {
  position: absolute;
  right: 12px;
  bottom: 6px;
  font-family: var(--mono);
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.04em;
  color: rgba(232, 238, 247, 0.18);
}

.cover-cat {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 2px 7px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  background: rgba(0, 0, 0, 0.24);
  color: rgba(232, 238, 247, 0.72);
  font-size: 9px;
  letter-spacing: 0.06em;
}

.ratio-row .cover-cat,
.ratio-row .cover-mark {
  display: none;
}
</style>
