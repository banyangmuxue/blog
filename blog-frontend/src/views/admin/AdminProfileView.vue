<template>
  <div class="admin-container page-enter profile-page">
    <header class="admin-page-head">
      <div>
        <p class="eyebrow">PROFILE</p>
        <h1 class="admin-page-title">个人资料</h1>
        <p class="admin-page-desc">这里的内容会同步显示在前台的「关于我」页面和侧边栏。</p>
      </div>

      <button
        type="button"
        class="admin-btn primary"
        :disabled="saving || loading"
        @click="save"
      >
        {{ saving ? '保存中…' : '保存资料' }}
      </button>
    </header>

    <div v-if="loading" class="admin-card loading-card">
      <div class="skeleton line w30"></div>
      <div class="skeleton line w60"></div>
      <div class="skeleton block"></div>
    </div>

    <p v-else-if="loadError" class="admin-error-box">{{ loadError }}</p>

    <div v-else class="profile-grid">
      <section class="admin-card main-card">
        <h2>基本资料</h2>

        <div class="field-grid">
          <label class="admin-field">
            <span>博主名 <em>必填</em></span>
            <input
              v-model="form.name"
              class="admin-input"
              type="text"
              maxlength="50"
              placeholder="你的名字"
            />
          </label>

          <label class="admin-field">
            <span>头像文字 <em>没有图片头像时显示，最多 4 个字符</em></span>
            <input
              v-model="form.avatarText"
              class="admin-input"
              type="text"
              maxlength="4"
              placeholder="例如 L"
            />
          </label>
        </div>

        <label class="admin-field">
          <span>一句话简介</span>
          <input
            v-model="form.bio"
            class="admin-input"
            type="text"
            maxlength="255"
            placeholder="写代码，也写字。"
          />
        </label>

        <div class="field-grid">
          <label class="admin-field">
            <span>加入月份 <em>格式 YYYY-MM</em></span>
            <input
              v-model="form.joinedAt"
              class="admin-input"
              type="text"
              maxlength="7"
              placeholder="例如 2023-06"
            />
          </label>

          <label class="admin-field">
            <span>技能 <em>用逗号分隔，最多 20 项</em></span>
            <input
              v-model="form.skillsText"
              class="admin-input"
              type="text"
              placeholder="Vue, TypeScript, Spring Boot"
            />
          </label>
        </div>

        <div class="about-head">
          <h2>关于我正文</h2>
          <div class="content-tabs">
            <button
              type="button"
              :class="{ active: mode === 'write' }"
              @click="mode = 'write'"
            >
              正文
            </button>
            <button
              type="button"
              :class="{ active: mode === 'preview' }"
              @click="mode = 'preview'"
            >
              预览
            </button>
          </div>
        </div>

        <div v-if="mode === 'write'">
          <textarea
            v-model="form.about"
            class="content-textarea"
            placeholder="支持 Markdown，会显示在「关于我」页面。&#10;&#10;## 我是谁&#10;&#10;介绍一下你自己…"
          ></textarea>
        </div>

        <div v-else class="content-preview">
          <MarkdownView v-if="form.about" :html="previewHtml" />
          <p v-else class="preview-empty">还没有正文，切换到「正文」开始写作。</p>
        </div>
      </section>

      <aside class="profile-side">
        <section class="admin-card side-card">
          <h2>头像</h2>

          <div class="avatar-preview">
            <img v-if="form.avatarUrl" :src="form.avatarUrl" alt="头像预览" />
            <span v-else>{{ form.avatarText || form.name.slice(0, 1) || 'B' }}</span>
          </div>

          <div class="avatar-actions">
            <button
              type="button"
              class="admin-btn small"
              :disabled="uploading"
              @click="pickAvatar"
            >
              {{ uploading ? '上传中…' : (form.avatarUrl ? '更换头像' : '上传头像') }}
            </button>
            <button
              v-if="form.avatarUrl"
              type="button"
              class="admin-btn small ghost"
              :disabled="uploading"
              @click="form.avatarUrl = ''"
            >
              移除图片
            </button>
          </div>

          <input
            ref="avatarInput"
            class="file-input"
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            @change="onAvatarChange"
          />

          <p class="side-hint">
            上传图片后，前台会优先显示图片头像；移除图片后使用头像文字。支持 JPG、PNG、GIF、WEBP，最大 5MB。
          </p>
        </section>

        <section class="admin-card side-card">
          <h2>社交链接</h2>

          <div v-for="(item, index) in form.socials" :key="index" class="social-row">
            <input
              v-model="item.label"
              class="admin-input"
              type="text"
              maxlength="50"
              placeholder="名称，如 GitHub"
            />
            <input
              v-model="item.url"
              class="admin-input"
              type="text"
              maxlength="255"
              placeholder="链接，如 https://github.com/you"
            />
            <button
              type="button"
              class="admin-btn small ghost"
              @click="removeSocial(index)"
            >
              移除
            </button>
          </div>

          <button type="button" class="admin-btn small add-social" @click="addSocial">
            ＋ 添加链接
          </button>

          <p class="side-hint">
            链接可以填写网址、mailto: 邮箱或站内路径（如 /archive），名称和地址需要成对填写。
          </p>
        </section>
      </aside>
    </div>

    <footer v-if="!loading && !loadError" class="profile-foot">
      <router-link :to="{ name: 'admin-posts' }" class="admin-btn ghost">返回文章管理</router-link>
      <router-link to="/about" target="_blank" class="admin-btn ghost">查看关于页 ↗</router-link>
      <button
        type="button"
        class="admin-btn primary"
        :disabled="saving"
        @click="save"
      >
        {{ saving ? '保存中…' : '保存资料' }}
      </button>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useSite } from '@/composables/useSite'
import { showToast } from '@/composables/useToast'
import { renderMarkdown } from '@/utils/markdown'
import MarkdownView from '@/components/common/MarkdownView.vue'

const route = useRoute()
const router = useRouter()
const { loadSite } = useSite()

const form = reactive({
  name: '',
  avatarText: '',
  avatarUrl: '',
  bio: '',
  joinedAt: '',
  skillsText: '',
  about: '',
  socials: []
})

const loading = ref(true)
const saving = ref(false)
const uploading = ref(false)
const loadError = ref('')
const mode = ref('write')
const avatarInput = ref(null)

const previewHtml = computed(() => renderMarkdown(form.about).html)

async function loadProfile() {
  loading.value = true
  loadError.value = ''
  try {
    const data = await api.getAdminProfile()
    applyProfile(data)
  } catch (err) {
    if (handleError(err)) {
      return
    }
    loadError.value = err.message || '个人资料加载失败'
  } finally {
    loading.value = false
  }
}

function applyProfile(data) {
  form.name = data.name || ''
  form.avatarText = data.avatarText || ''
  form.avatarUrl = data.avatarUrl || ''
  form.bio = data.bio || ''
  form.joinedAt = data.joinedAt || ''
  form.skillsText = (data.skills || []).join(', ')
  form.about = data.about || ''
  form.socials = (data.socials || []).map((item) => ({
    label: item.label || '',
    url: item.url || ''
  }))
}

function addSocial() {
  form.socials.push({ label: '', url: '' })
}

function removeSocial(index) {
  form.socials.splice(index, 1)
}

function pickAvatar() {
  if (avatarInput.value) {
    avatarInput.value.click()
  }
}

async function onAvatarChange(event) {
  const file = event.target.files && event.target.files[0]
  event.target.value = ''
  if (!file) {
    return
  }

  uploading.value = true
  try {
    form.avatarUrl = await api.uploadAvatar(file)
    showToast('头像已上传，记得保存资料', 'success')
  } catch (err) {
    handleError(err)
  } finally {
    uploading.value = false
  }
}

function parseSkills() {
  return form.skillsText
    .split(/[,，]/)
    .map((item) => item.trim())
    .filter(Boolean)
}

function validate() {
  if (!form.name.trim()) {
    return '请填写博主名'
  }
  if (form.joinedAt && !/^\d{4}-\d{2}$/.test(form.joinedAt.trim())) {
    return '加入月份格式应为 YYYY-MM，例如 2023-06'
  }
  if (form.avatarText.trim().length > 4) {
    return '头像文字最多 4 个字符'
  }
  if (parseSkills().length > 20) {
    return '技能最多填写 20 项'
  }
  const socials = form.socials.filter((item) => item.label.trim() || item.url.trim())
  if (socials.some((item) => !item.label.trim() || !item.url.trim())) {
    return '社交链接的名称和地址需要成对填写'
  }
  return ''
}

async function save() {
  const message = validate()
  if (message) {
    showToast(message, 'error')
    return
  }

  saving.value = true
  try {
    const data = await api.updateAdminProfile({
      name: form.name.trim(),
      avatarText: form.avatarText.trim(),
      avatarUrl: form.avatarUrl,
      bio: form.bio.trim(),
      joinedAt: form.joinedAt.trim(),
      skills: parseSkills(),
      socials: form.socials
        .filter((item) => item.label.trim() && item.url.trim())
        .map((item) => ({ label: item.label.trim(), url: item.url.trim() })),
      about: form.about
    })
    applyProfile(data)
    showToast('个人资料已保存', 'success')
    await loadSite(true)
  } catch (err) {
    handleError(err)
  } finally {
    saving.value = false
  }
}

function handleError(err) {
  if (err.status === 401) {
    router.replace({ name: 'admin-login', query: { redirect: route.fullPath } })
    return true
  }
  showToast(err.message || '操作失败，请稍后重试', 'error')
  return false
}

onMounted(loadProfile)
</script>

<style scoped lang="scss">
.profile-page {
  padding-bottom: 20px;
}

.loading-card {
  padding: 22px;

  .line {
    margin-bottom: 14px;
  }

  .w30 {
    width: 30%;
  }

  .w60 {
    width: 60%;
  }

  .block {
    height: 240px;
    margin-top: 10px;
  }
}

.profile-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 18px;
  align-items: start;
}

.main-card {
  display: flex;
  padding: 22px;
  flex-direction: column;
  gap: 16px;

  h2 {
    font-size: 13.5px;
    font-weight: 600;
  }
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.about-head {
  display: flex;
  margin-top: 4px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.content-tabs {
  display: inline-flex;
  padding: 3px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: rgba(6, 9, 15, 0.5);

  button {
    padding: 5px 14px;
    border-radius: 5px;
    color: var(--muted);
    font-size: 12px;
    transition: 0.15s;

    &.active {
      background: rgba(62, 231, 255, 0.09);
      color: var(--cyan);
    }
  }
}

.content-textarea {
  display: block;
  width: 100%;
  min-height: 420px;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 8px;
  outline: none;
  background: rgba(6, 9, 15, 0.52);
  color: var(--text);
  font-family: var(--mono);
  font-size: 13px;
  line-height: 1.9;
  resize: vertical;
  transition: border-color 0.16s, box-shadow 0.16s;

  &:focus {
    border-color: rgba(62, 231, 255, 0.42);
    box-shadow: 0 0 0 3px rgba(62, 231, 255, 0.06);
  }
}

.content-preview {
  min-height: 420px;
  padding: 16px 20px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: rgba(6, 9, 15, 0.36);
}

.preview-empty {
  padding: 40px 0;
  color: var(--faint);
  text-align: center;
  font-size: 13px;
}

.profile-side {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.side-card {
  padding: 18px;

  h2 {
    margin-bottom: 15px;
    font-size: 13.5px;
    font-weight: 600;
  }
}

.avatar-preview {
  display: grid;
  overflow: hidden;
  width: 104px;
  height: 104px;
  margin: 0 auto;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.45), rgba(139, 92, 246, 0.55));
  color: #04121a;
  font-family: var(--mono);
  font-size: 36px;
  font-weight: 700;
  box-shadow: 0 16px 40px -22px rgba(62, 231, 255, 0.6);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.avatar-actions {
  display: flex;
  margin-top: 14px;
  justify-content: center;
  gap: 8px;
}

.file-input {
  display: none;
}

.side-hint {
  margin-top: 12px;
  color: var(--faint);
  font-size: 11.5px;
  line-height: 1.7;
}

.social-row {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr) auto;
  gap: 6px;

  & + & {
    margin-top: 8px;
  }
}

.add-social {
  margin-top: 10px;
}

.profile-foot {
  display: flex;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 980px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .profile-side {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .field-grid {
    grid-template-columns: 1fr;
  }

  .main-card {
    padding: 16px;
  }

  .profile-side {
    display: flex;
  }

  .social-row {
    grid-template-columns: 1fr auto;

    .admin-input:first-child {
      grid-column: 1 / -1;
    }
  }

  .content-textarea {
    min-height: 340px;
  }
}
</style>
