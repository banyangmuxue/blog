<template>
  <div ref="root" class="prose" v-html="html"></div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import '@/assets/styles/prose.scss'

const props = defineProps({
  html: { type: String, default: '' }
})

const root = ref(null)

function decorate() {
  if (!root.value) return
  root.value.querySelectorAll('pre').forEach((pre) => {
    if (pre.querySelector('.copy-code')) return
    const button = document.createElement('button')
    button.type = 'button'
    button.className = 'copy-code'
    button.textContent = '复制'
    button.addEventListener('click', async () => {
      const code = pre.querySelector('code')
      try {
        await navigator.clipboard.writeText(code ? code.textContent : '')
        button.textContent = '已复制'
      } catch (error) {
        button.textContent = '复制失败'
      }
      setTimeout(() => {
        button.textContent = '复制'
      }, 2000)
    })
    pre.appendChild(button)
  })
}

watch(
  () => props.html,
  () => {
    nextTick(decorate)
  },
  { immediate: true }
)
</script>
