<template>
  <div class="admin-container page-enter site-page">
    <header class="admin-page-head">
      <div>
        <p class="eyebrow">SITE</p>
        <h1 class="admin-page-title">站点设置</h1>
        <p class="admin-page-desc">这些内容会显示在前台头部、首页、侧边栏和页脚。</p>
      </div>

      <button
        type="button"
        class="admin-btn primary"
        :disabled="saving || loading"
        @click="save"
      >
        {{ saving ? '保存中…' : '保存设置' }}
      </button>
    </header>

    <div v-if="loading" class="admin-card loading-card">
      <div class="skeleton line w30"></div>
      <div class="skeleton line w60"></div>
      <div class="skeleton block"></div>
    </div>

    <p v-else-if="loadError" class="admin-error-box">{{ loadError }}</p>

    <div v-else class="settings-grid">
      <div class="settings-main">
        <section class="admin-card settings-card">
          <h2>站点 Logo</h2>

          <div class="logo-editor">
            <div class="logo-preview">
              <img v-if="form.logoUrl" :src="form.logoUrl" alt="Logo 预览" />
              <span v-else>{{ form.logoText || 'B/' }}</span>
            </div>

            <div class="logo-controls">
              <label class="admin-field">
                <span>Logo 文字 <em>没有图片时显示</em></span>
                <input
                  v-model="form.logoText"
                  class="admin-input"
                  type="text"
                  maxlength="20"
                  placeholder="例如 B/"
                />
              </label>

              <div class="logo-actions">
                <button
                  type="button"
                  class="admin-btn small"
                  :disabled="uploadingLogo"
                  @click="pickLogo"
                >
                  {{ uploadingLogo ? '上传中…' : '上传图片' }}
                </button>
                <button
                  v-if="form.logoUrl"
                  type="button"
                  class="admin-btn small ghost"
                  @click="removeLogo"
                >
                  移除图片
                </button>
                <input
                  ref="logoInput"
                  type="file"
                  accept="image/jpeg,image/png,image/gif,image/webp"
                  hidden
                  @change="onLogoChange"
                />
              </div>

              <p class="settings-hint">
                图片优先，保存后同步显示在前台头部和后台登录页；建议使用正方形图片，不超过 5MB。
              </p>
            </div>
          </div>
        </section>

        <section class="admin-card settings-card">
          <h2>基本信息</h2>

          <div class="field-grid">
            <label class="admin-field">
              <span>站点全名</span>
              <input
                v-model="form.name"
                class="admin-input"
                type="text"
                maxlength="50"
                placeholder="例如 B/ 记录代码与生活"
              />
            </label>

            <label class="admin-field">
              <span>站点简称</span>
              <input
                v-model="form.shortName"
                class="admin-input"
                type="text"
                maxlength="20"
                placeholder="例如 B/"
              />
            </label>
          </div>

          <div class="field-grid">
            <label class="admin-field">
              <span>首页标题</span>
              <input
                v-model="form.title"
                class="admin-input"
                type="text"
                maxlength="50"
                placeholder="例如 记录代码与生活"
              />
            </label>

            <label class="admin-field">
              <span>建站月份 <em>格式 YYYY-MM</em></span>
              <input
                v-model="form.foundedAt"
                class="admin-input"
                type="text"
                maxlength="7"
                placeholder="例如 2023-06"
              />
            </label>
          </div>

          <label class="admin-field">
            <span>副标题</span>
            <input
              v-model="form.subtitle"
              class="admin-input"
              type="text"
              maxlength="100"
              placeholder="例如 前端、后端与持续学习"
            />
          </label>

          <label class="admin-field">
            <span>站点描述</span>
            <textarea
              v-model="form.description"
              class="admin-textarea"
              maxlength="500"
              rows="3"
              placeholder="用于首页和搜索结果的站点介绍"
            ></textarea>
          </label>

          <p class="settings-hint">
            前台头部显示为「站点简称 + 首页标题」，尾部显示「PERSONAL BLOG · EST. 建站月份」。
          </p>
        </section>

        <section class="admin-card settings-card">
          <h2>展示内容</h2>

          <div class="field-grid">
            <label class="admin-field">
              <span>博主名</span>
              <input
                v-model="form.author"
                class="admin-input"
                type="text"
                maxlength="50"
                placeholder="页脚版权署名"
              />
            </label>

            <label class="admin-field">
              <span>备案号</span>
              <input
                v-model="form.icp"
                class="admin-input"
                type="text"
                maxlength="50"
                placeholder="例如 京 ICP 备 2026000000 号"
              />
            </label>
          </div>

          <label class="admin-field">
            <span>公告</span>
            <input
              v-model="form.announcement"
              class="admin-input"
              type="text"
              maxlength="255"
              placeholder="显示在首页的公告文案"
            />
          </label>

          <label class="admin-field">
            <span>页脚文案 <em>留空时使用副标题</em></span>
            <input
              v-model="form.footerText"
              class="admin-input"
              type="text"
              maxlength="100"
              placeholder="例如 用代码记录思考，用文字整理生活。"
            />
          </label>
        </section>
      </div>

      <aside class="settings-side">
        <section class="admin-card settings-card">
          <h2>功能开关</h2>

          <label class="switch-row">
            <span>
              <strong>文章评论</strong>
              <em>允许访客在文章下提交评论</em>
            </span>
            <input v-model="form.features.comments" type="checkbox" />
          </label>

          <label class="switch-row">
            <span>
              <strong>留言先审后发</strong>
              <em>留言审核后才展示；文章评论提交即公开</em>
            </span>
            <input v-model="form.features.review" type="checkbox" />
          </label>

          <label class="switch-row">
            <span>
              <strong>留言板</strong>
              <em>开放访客留言入口</em>
            </span>
            <input v-model="form.features.guestbook" type="checkbox" />
          </label>

          <label class="switch-row">
            <span>
              <strong>搜索</strong>
              <em>开放站内文章搜索</em>
            </span>
            <input v-model="form.features.search" type="checkbox" />
          </label>
        </section>

        <section class="admin-card settings-card">
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

          <p class="settings-hint">
            显示在前台页脚；链接可以填写网址、mailto: 邮箱或站内路径（如 /archive）。
          </p>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useSite } from '@/composables/useSite'
import { showToast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const { loadSite } = useSite()

const form = reactive({
  name: '',
  shortName: '',
  logoText: 'B/',
  logoUrl: '',
  title: '',
  subtitle: '',
  description: '',
  author: '',
  announcement: '',
  footerText: '',
  icp: '',
  foundedAt: '',
  socials: [],
  features: {
    comments: true,
    review: true,
    guestbook: true,
    search: true
  }
})

const loading = ref(true)
const saving = ref(false)
const uploadingLogo = ref(false)
const loadError = ref('')
const logoInput = ref(null)

async function loadConfig() {
  loading.value = true
  loadError.value = ''
  try {
    const data = await api.getAdminSite()
    applyConfig(data)
  } catch (err) {
    if (handleError(err)) {
      return
    }
    loadError.value = err.message || '站点配置加载失败'
  } finally {
    loading.value = false
  }
}

function applyConfig(data) {
  form.name = data.name || ''
  form.shortName = data.shortName || ''
  form.logoText = data.logoText || 'B/'
  form.logoUrl = data.logoUrl || ''
  form.title = data.title || ''
  form.subtitle = data.subtitle || ''
  form.description = data.description || ''
  form.author = data.author || ''
  form.announcement = data.announcement || ''
  form.footerText = data.footerText || ''
  form.icp = data.icp || ''
  form.foundedAt = data.foundedAt || ''
  form.socials = (data.socials || []).map((item) => ({
    label: item.label || '',
    url: item.url || ''
  }))
  form.features = {
    comments: Boolean(data.features && data.features.comments),
    review: Boolean(data.features && data.features.review),
    guestbook: Boolean(data.features && data.features.guestbook),
    search: Boolean(data.features && data.features.search)
  }
}

function pickLogo() {
  logoInput.value?.click()
}

async function onLogoChange(event) {
  const file = event.target.files && event.target.files[0]
  event.target.value = ''
  if (!file) {
    return
  }

  uploadingLogo.value = true
  try {
    const url = await api.uploadLogo(file)
    form.logoUrl = url || ''
    showToast('Logo 图片已上传，保存设置后生效', 'success')
  } catch (err) {
    handleError(err)
  } finally {
    uploadingLogo.value = false
  }
}

function removeLogo() {
  form.logoUrl = ''
}

function addSocial() {
  form.socials.push({ label: '', url: '' })
}

function removeSocial(index) {
  form.socials.splice(index, 1)
}

function validate() {
  if (!form.name.trim()) {
    return '请填写站点全名'
  }
  if (!form.shortName.trim()) {
    return '请填写站点简称'
  }
  if (!form.title.trim()) {
    return '请填写首页标题'
  }
  if (form.foundedAt && !/^\d{4}-\d{2}$/.test(form.foundedAt.trim())) {
    return '建站月份格式应为 YYYY-MM，例如 2023-06'
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
    const data = await api.updateAdminSite({
      name: form.name.trim(),
      shortName: form.shortName.trim(),
      logoText: form.logoText.trim(),
      logoUrl: form.logoUrl.trim(),
      title: form.title.trim(),
      subtitle: form.subtitle.trim(),
      description: form.description.trim(),
      author: form.author.trim(),
      announcement: form.announcement.trim(),
      footerText: form.footerText.trim(),
      icp: form.icp.trim(),
      foundedAt: form.foundedAt.trim(),
      socials: form.socials
        .filter((item) => item.label.trim() && item.url.trim())
        .map((item) => ({ label: item.label.trim(), url: item.url.trim() })),
      featuresComments: form.features.comments,
      featuresReview: form.features.review,
      featuresGuestbook: form.features.guestbook,
      featuresSearch: form.features.search
    })
    applyConfig(data)
    showToast('站点设置已保存', 'success')
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

onMounted(loadConfig)
</script>

<style scoped lang="scss">
.site-page {
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

.settings-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 18px;
  align-items: start;
}

.settings-main,
.settings-side {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.settings-card {
  display: flex;
  padding: 22px;
  flex-direction: column;
  gap: 16px;

  h2 {
    font-size: 13.5px;
    font-weight: 600;
  }
}

.logo-editor {
  display: flex;
  align-items: center;
  gap: 18px;
}

.logo-preview {
  display: grid;
  width: 64px;
  height: 64px;
  flex: none;
  place-items: center;
  overflow: hidden;
  border: 1px solid rgba(62, 231, 255, 0.32);
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.35), rgba(139, 92, 246, 0.5));
  color: #04121a;
  font-family: var(--mono);
  font-size: 22px;
  font-weight: 700;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.logo-controls {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 10px;
}

.logo-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.admin-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--line);
  border-radius: 8px;
  outline: none;
  background: rgba(6, 9, 15, 0.52);
  color: var(--text);
  font-family: inherit;
  font-size: 12.5px;
  line-height: 1.7;
  resize: vertical;
  transition: border-color 0.16s, box-shadow 0.16s;

  &:focus {
    border-color: rgba(62, 231, 255, 0.42);
    box-shadow: 0 0 0 3px rgba(62, 231, 255, 0.06);
  }
}

.settings-hint {
  color: var(--faint);
  font-size: 11.5px;
  line-height: 1.7;
}

.switch-row {
  display: flex;
  padding: 10px 0;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  cursor: pointer;

  & + & {
    border-top: 1px dashed var(--line);
  }

  > span {
    display: flex;
    flex-direction: column;
    gap: 3px;
  }

  strong {
    color: var(--text);
    font-size: 12.5px;
    font-weight: 500;
  }

  em {
    color: var(--faint);
    font-size: 11px;
    font-style: normal;
  }

  input {
    position: relative;
    width: 34px;
    height: 20px;
    flex: none;
    border: 1px solid var(--line);
    border-radius: 999px;
    appearance: none;
    background: rgba(6, 9, 15, 0.7);
    cursor: pointer;
    transition: 0.18s;

    &::after {
      position: absolute;
      top: 2px;
      left: 2px;
      width: 14px;
      height: 14px;
      border-radius: 50%;
      background: var(--faint);
      content: '';
      transition: 0.18s;
    }

    &:checked {
      border-color: rgba(62, 231, 255, 0.5);
      background: rgba(62, 231, 255, 0.14);

      &::after {
        left: 16px;
        background: var(--cyan);
      }
    }
  }
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

@media (max-width: 980px) {
  .settings-grid {
    grid-template-columns: 1fr;
  }

  .settings-side {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .field-grid {
    grid-template-columns: 1fr;
  }

  .logo-editor {
    align-items: flex-start;
    flex-direction: column;
  }

  .settings-card {
    padding: 16px;
  }

  .settings-side {
    display: flex;
  }

  .social-row {
    grid-template-columns: 1fr auto;

    .admin-input:first-child {
      grid-column: 1 / -1;
    }
  }
}
</style>
