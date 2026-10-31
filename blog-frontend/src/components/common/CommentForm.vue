<template>
  <div class="comment-form">
    <div v-if="disabled" class="closed">
      <span class="mono">COMMENTS CLOSED</span>
      <p>评论功能暂时关闭，已有评论仍然可以阅读。</p>
    </div>

    <form v-else novalidate @submit.prevent="submit">
      <div class="row">
        <div class="field">
          <label :for="`nickname-${uid}`">昵称 <span class="required">*</span></label>
          <input
            :id="`nickname-${uid}`"
            v-model.trim="form.nickname"
            class="input"
            type="text"
            maxlength="20"
            placeholder="2—20 个字"
            :aria-invalid="Boolean(errors.nickname)"
            :aria-describedby="errors.nickname ? `nickname-error-${uid}` : undefined"
          />
          <span v-if="errors.nickname" :id="`nickname-error-${uid}`" class="error">{{ errors.nickname }}</span>
        </div>

        <div class="field">
          <label :for="`email-${uid}`">邮箱 <span class="optional">选填</span></label>
          <input
            :id="`email-${uid}`"
            v-model.trim="form.email"
            class="input"
            type="email"
            placeholder="不会公开显示"
            :aria-invalid="Boolean(errors.email)"
            :aria-describedby="errors.email ? `email-error-${uid}` : undefined"
          />
          <span v-if="errors.email" :id="`email-error-${uid}`" class="error">{{ errors.email }}</span>
        </div>
      </div>

      <div class="field">
        <label :for="`content-${uid}`">内容 <span class="required">*</span></label>
        <textarea
          :id="`content-${uid}`"
          v-model="form.content"
          class="textarea"
          :maxlength="maxLength"
          rows="4"
          :placeholder="placeholder"
          :aria-invalid="Boolean(errors.content)"
          :aria-describedby="errors.content ? `content-error-${uid}` : undefined"
        ></textarea>
        <div class="hint-line">
          <span v-if="errors.content" :id="`content-error-${uid}`" class="error">{{ errors.content }}</span>
          <span v-else class="hint">请勿填写敏感信息，评论将公开展示。</span>
          <span class="counter mono">{{ form.content.length }} / {{ maxLength }}</span>
        </div>
      </div>

      <div class="footer-line">
        <div class="captcha">
          <label :for="`captcha-${uid}`" class="captcha-label">人机校验</label>
          <span class="question mono">{{ captcha.a }} + {{ captcha.b }} =</span>
          <input
            :id="`captcha-${uid}`"
            v-model.trim="form.captcha"
            class="input captcha-input"
            inputmode="numeric"
            maxlength="2"
            :aria-invalid="Boolean(errors.captcha)"
            autocomplete="off"
          />
          <button type="button" class="refresh" aria-label="换一道题" @click="refreshCaptcha">↻</button>
        </div>
        <span v-if="errors.captcha" class="error">{{ errors.captcha }}</span>

        <button type="submit" class="submit" :disabled="submitting || remaining > 0">
          {{ submitLabel }}
        </button>
      </div>

      <p v-if="reviewRequired" class="review-note mono">REVIEW REQUIRED · 审核通过后展示</p>
    </form>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { api } from '@/api'
import { showToast } from '@/composables/useToast'

const props = defineProps({
  target: { type: String, default: 'post' },
  targetId: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  reviewRequired: { type: Boolean, default: true },
  maxLength: { type: Number, default: 300 },
  placeholder: { type: String, default: '写下你的想法…' }
})

const emit = defineEmits(['submitted'])

const uid = Math.random().toString(36).slice(2, 8)
const form = reactive({ nickname: '', email: '', content: '', captcha: '' })
const errors = reactive({ nickname: '', email: '', content: '', captcha: '' })
const captcha = reactive({ a: 3, b: 5 })
const submitting = ref(false)
const remaining = ref(0)
let timer = null

const cooldownKey = computed(() => `blog-cooldown:${props.target}:${props.targetId || 'global'}`)

const submitLabel = computed(() => {
  if (submitting.value) return props.target === 'post' ? '提交中…' : '提交中…'
  if (remaining.value > 0) return `请稍候 ${remaining.value}s`
  return props.target === 'post' ? '提交评论' : '提交留言'
})

function refreshCaptcha() {
  captcha.a = Math.floor(Math.random() * 8) + 1
  captcha.b = Math.floor(Math.random() * 8) + 1
  form.captcha = ''
}

function startTimer() {
  if (timer) clearInterval(timer)
  if (remaining.value <= 0) return
  timer = setInterval(() => {
    remaining.value -= 1
    if (remaining.value <= 0) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

function restoreCooldown() {
  const last = Number(localStorage.getItem(cooldownKey.value) || 0)
  const passed = Date.now() - last
  remaining.value = passed < 60000 ? Math.ceil((60000 - passed) / 1000) : 0
  startTimer()
}

function validate() {
  errors.nickname = !form.nickname
    ? '请填写昵称'
    : form.nickname.length < 2 || form.nickname.length > 20
      ? '昵称需 2—20 个字'
      : ''
  errors.email =
    form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email) ? '邮箱格式不正确' : ''
  const length = form.content.trim().length
  errors.content =
    length < 5 ? '内容至少 5 个字' : length > props.maxLength ? `内容最多 ${props.maxLength} 字` : ''
  errors.captcha = Number(form.captcha) === captcha.a + captcha.b ? '' : '校验答案不正确'
  return !errors.nickname && !errors.email && !errors.content && !errors.captcha
}

async function submit() {
  if (submitting.value || remaining.value > 0) return
  if (!validate()) return
  submitting.value = true
  try {
    const payload = {
      slug: props.targetId,
      nickname: form.nickname,
      email: form.email,
      content: form.content.trim()
    }
    const result =
      props.target === 'post' ? await api.submitComment(payload) : await api.submitMessage(payload)
    const pending = result.status === 'pending'
    showToast(pending ? '已提交，审核通过后展示' : '提交成功，已公开展示', 'success')
    form.content = ''
    form.captcha = ''
    refreshCaptcha()
    localStorage.setItem(cooldownKey.value, String(Date.now()))
    remaining.value = 60
    startTimer()
    emit('submitted', result)
  } catch (error) {
    showToast(error.message || '提交失败，请重试', 'error', 5000)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
  restoreCooldown()
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped lang="scss">
.comment-form {
  padding: 20px;
  border: 1px solid var(--line);
  border-radius: var(--radius-card);
  background: rgba(13, 21, 32, 0.5);
}

.closed {
  text-align: center;
  padding: 12px 0;

  .mono {
    color: var(--faint);
    font-size: 10px;
    letter-spacing: 0.12em;
  }

  p {
    margin-top: 6px;
    color: var(--muted);
    font-size: 12.5px;
  }
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 4px;
}

label {
  color: var(--muted);
  font-size: 11.5px;
}

.required {
  color: var(--red);
}

.optional {
  color: var(--faint);
  font-size: 10px;
}

.input,
.textarea {
  width: 100%;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: rgba(0, 0, 0, 0.24);
  color: var(--text);
  font-size: 13px;
  transition: border-color 0.16s, outline 0.16s;

  &::placeholder {
    color: var(--faint);
  }

  &:focus {
    outline: 2px solid rgba(62, 231, 255, 0.25);
    outline-offset: 1px;
    border-color: rgba(62, 231, 255, 0.5);
  }
}

.input {
  height: 38px;
  padding: 0 11px;
}

.textarea {
  padding: 10px 11px;
  line-height: 1.7;
  resize: vertical;
  min-height: 96px;
}

.error {
  color: var(--red);
  font-size: 11px;
}

.hint-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.hint {
  color: var(--faint);
  font-size: 10.5px;
}

.counter {
  color: var(--faint);
  font-size: 10px;
  white-space: nowrap;
}

.footer-line {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px 14px;
  margin-top: 14px;
}

.captcha {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: rgba(0, 0, 0, 0.2);
}

.captcha-label {
  font-size: 10.5px;
  color: var(--faint);
}

.question {
  color: var(--cyan);
  font-size: 11px;
}

.captcha-input {
  width: 48px;
  height: 28px;
  padding: 0 6px;
  text-align: center;
}

.refresh {
  color: var(--faint);
  font-size: 13px;

  &:hover {
    color: var(--cyan);
  }
}

.submit {
  height: 36px;
  margin-left: auto;
  padding: 0 18px;
  border: 1px solid rgba(62, 231, 255, 0.45);
  border-radius: var(--radius);
  background: linear-gradient(180deg, #54ecff, #25cfe8);
  color: #04121a;
  font-size: 12.5px;
  font-weight: 600;
  transition: filter 0.16s, opacity 0.16s;

  &:hover:not(:disabled) {
    filter: brightness(1.07);
  }

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.review-note {
  margin-top: 10px;
  color: var(--faint);
  font-size: 9.5px;
  letter-spacing: 0.06em;
}

@media (max-width: 640px) {
  .row {
    grid-template-columns: 1fr;
  }

  .submit {
    width: 100%;
    margin-left: 0;
  }
}
</style>
