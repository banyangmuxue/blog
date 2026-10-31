import { reactive } from 'vue'

const toasts = reactive([])
let seed = 0

export function dismissToast(id) {
  const index = toasts.findIndex((item) => item.id === id)
  if (index >= 0) toasts.splice(index, 1)
}

export function showToast(message, type = 'success', duration = 3000) {
  const id = (seed += 1)
  toasts.push({ id, message, type })
  if (duration > 0) {
    setTimeout(() => dismissToast(id), duration)
  }
  return id
}

export function useToast() {
  return { toasts, showToast, dismissToast }
}
