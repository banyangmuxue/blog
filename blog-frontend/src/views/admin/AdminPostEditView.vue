<template>
  <div class="admin-container page-enter editor-page">
    <header class="editor-head">
      <div class="editor-heading">
        <router-link :to="{ name: 'admin-posts' }" class="back-link">← 文章列表</router-link>
        <p class="eyebrow">{{ isEdit ? 'EDIT POST' : 'NEW POST' }}</p>
        <h1 class="admin-page-title">{{ isEdit ? '编辑文章' : '新建文章' }}</h1>
      </div>

      <div class="editor-head-actions">
        <button
          v-if="isEdit"
          type="button"
          class="admin-btn danger"
          :disabled="saving"
          @click="removePost"
        >
          删除
        </button>
        <button
          type="button"
          class="admin-btn primary"
          :disabled="saving || loading"
          @click="save"
        >
          {{ saving ? '保存中…' : '保存文章' }}
        </button>
      </div>
    </header>

    <p v-if="loadError" class="admin-error-box editor-error">{{ loadError }}</p>

    <div class="editor-grid">
      <section class="admin-card editor-main">
        <div class="title-fields">
          <label class="admin-field">
            <span>标题 <em>必填</em></span>
            <input
              v-model="form.title"
              class="admin-input title-input"
              type="text"
              maxlength="120"
              placeholder="给这篇文章起一个标题"
            />
          </label>

          <label class="admin-field">
            <span>Slug <em>URL 标识，只能用小写字母、数字和中划线</em></span>
            <div class="slug-row">
              <span class="slug-prefix mono">/post/</span>
              <input
                v-model="form.slug"
                class="admin-input"
                type="text"
                maxlength="100"
                placeholder="例如 vue-render-performance"
                @blur="normalizeSlug"
              />
              <button type="button" class="admin-btn small" @click="generateSlug">用标题生成</button>
            </div>
          </label>

          <label class="admin-field">
            <span>摘要 <em>显示在文章列表卡片上，最多 300 字</em></span>
            <textarea
              v-model="form.excerpt"
              class="admin-input"
              rows="3"
              maxlength="300"
              placeholder="用一两句话概括这篇文章"
            ></textarea>
            <span class="counter mono">{{ form.excerpt.length }} / 300</span>
          </label>
        </div>

        <div class="content-toolbar">
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
          <span class="counter mono">{{ form.content.length }} 字符 · Markdown</span>
        </div>

        <div v-if="mode === 'write'" class="content-write">
          <textarea
            v-model="form.content"
            class="content-textarea"
            placeholder="# 一级标题&#10;&#10;正文支持 Markdown：**加粗**、`代码`、列表和代码块。&#10;&#10;```js&#10;console.log('hello')&#10;```"
          ></textarea>
        </div>

        <div v-else class="content-preview">
          <MarkdownView v-if="form.content" :html="previewHtml" />
          <p v-else class="preview-empty">还没有正文，切换到“正文”开始写作。</p>
        </div>
      </section>

      <aside class="editor-side">
        <section class="admin-card side-card">
          <h2>发布设置</h2>

          <label class="admin-field">
            <span>状态</span>
            <select v-model="form.status" class="admin-input">
              <option value="draft">草稿（仅自己可见）</option>
              <option value="published">已发布（前台可见）</option>
            </select>
          </label>

          <label class="check-row">
            <input v-model="form.pinned" type="checkbox" />
            <span>置顶这篇文章</span>
          </label>

          <p class="side-hint">
            发布后会立即出现在前台；转为草稿后前台将无法访问。
          </p>
        </section>

        <section class="admin-card side-card">
          <h2>分类与标签</h2>

          <label class="admin-field">
            <span>分类</span>
            <select v-model="form.categorySlug" class="admin-input">
              <option value="">未分类</option>
              <option v-for="category in categories" :key="category.slug" :value="category.slug">
                {{ category.name }}
              </option>
            </select>
          </label>

          <div class="tag-field">
            <span class="tag-label">标签</span>
            <div v-if="tagOptions.length" class="tag-options">
              <label
                v-for="tag in tagOptions"
                :key="tag.slug"
                class="tag-option"
                :class="{ checked: form.tagSlugs.includes(tag.slug) }"
              >
                <input v-model="form.tagSlugs" type="checkbox" :value="tag.slug" />
                <span>{{ tag.name }}</span>
              </label>
            </div>
            <p v-else class="side-hint">暂无标签，可稍后再添加。</p>
          </div>
        </section>

        <section class="admin-card side-card">
          <h2>封面</h2>
          <p class="side-hint">可以上传图片作为封面；没有图片时使用下方渐变配色。</p>

          <div class="cover-preview" :style="coverStyle">
            <img v-if="form.coverImage" :src="form.coverImage" alt="封面预览" />
            <span class="cover-text">记录代码与生活</span>
          </div>

          <div class="cover-actions">
            <button
              type="button"
              class="admin-btn small"
              :disabled="uploading"
              @click="pickCoverImage"
            >
              {{ uploading ? '上传中…' : (form.coverImage ? '更换图片' : '上传封面图') }}
            </button>
            <button
              v-if="form.coverImage"
              type="button"
              class="admin-btn small ghost"
              :disabled="uploading"
              @click="removeCoverImage"
            >
              移除图片
            </button>
          </div>

          <input
            ref="coverInput"
            class="cover-file-input"
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            @change="onCoverFileChange"
          />

          <div class="color-fields">
            <label class="color-field">
              <span>起始色</span>
              <div>
                <input v-model="form.coverFrom" type="color" />
                <code>{{ form.coverFrom }}</code>
              </div>
            </label>

            <label class="color-field">
              <span>结束色</span>
              <div>
                <input v-model="form.coverTo" type="color" />
                <code>{{ form.coverTo }}</code>
              </div>
            </label>
          </div>
        </section>

        <section v-if="isEdit && postInfo" class="admin-card side-card">
          <h2>文章信息</h2>
          <dl class="post-info">
            <div>
              <dt>阅读量</dt>
              <dd class="mono">{{ formatNumber(postInfo.views || 0) }}</dd>
            </div>
            <div>
              <dt>发布时间</dt>
              <dd class="mono">{{ postInfo.publishedAt || '尚未发布' }}</dd>
            </div>
            <div>
              <dt>更新时间</dt>
              <dd class="mono">{{ postInfo.updatedAt || '—' }}</dd>
            </div>
          </dl>
        </section>
      </aside>
    </div>

    <section v-if="isEdit" class="admin-card comments-panel">
      <header class="comments-panel-head">
        <div>
          <h2>评论 <span class="mono">{{ commentTotal }}</span></h2>
          <p>按提交时间倒序，每页显示最近 10 条。</p>
        </div>
        <button
          type="button"
          class="admin-btn small"
          :disabled="commentsLoading"
          @click="loadComments"
        >
          {{ commentsLoading ? '加载中…' : '刷新' }}
        </button>
      </header>

      <div v-if="commentsLoading" class="comments-loading">
        <div v-for="row in 3" :key="row" class="skeleton comment-skeleton"></div>
      </div>

      <p v-else-if="commentsError" class="admin-error-box">{{ commentsError }}</p>

      <div v-else-if="!comments.length" class="admin-empty comments-empty">
        <strong>这篇文章还没有评论</strong>
        <span>访客在前台提交的评论会出现在这里。</span>
      </div>

      <ul v-else class="admin-comment-list">
        <li v-for="comment in comments" :key="comment.id" class="admin-comment">
          <div class="comment-head">
            <span class="comment-avatar">{{ comment.nickname.slice(0, 1).toUpperCase() }}</span>
            <div class="comment-who">
              <strong>{{ comment.nickname }}</strong>
              <span class="mono">{{ comment.createdAt }}</span>
            </div>
            <span class="admin-badge" :class="comment.status">
              {{ comment.status === 'approved' ? '已通过' : '待审核' }}
            </span>
            <button
              type="button"
              class="admin-btn small danger"
              :disabled="deletingCommentId === comment.id"
              @click="removeComment(comment)"
            >
              {{ deletingCommentId === comment.id ? '删除中…' : '删除' }}
            </button>
          </div>
          <p class="comment-text">{{ comment.content }}</p>
        </li>
      </ul>

      <div v-if="commentPages > 1" class="comments-pager">
        <button
          type="button"
          class="admin-btn small"
          :disabled="commentPage <= 1 || commentsLoading"
          @click="changeCommentPage(commentPage - 1)"
        >
          上一页
        </button>
        <span class="mono">{{ commentPage }} / {{ commentPages }}</span>
        <button
          type="button"
          class="admin-btn small"
          :disabled="commentPage >= commentPages || commentsLoading"
          @click="changeCommentPage(commentPage + 1)"
        >
          下一页
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { renderMarkdown } from '@/utils/markdown'
import { showToast } from '@/composables/useToast'
import { formatNumber } from '@/utils/format'
import MarkdownView from '@/components/common/MarkdownView.vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => Boolean(route.params.id))

const form = reactive({
  title: '',
  slug: '',
  excerpt: '',
  content: '',
  coverFrom: '#123043',
  coverTo: '#2a1b4d',
  coverImage: '',
  categorySlug: '',
  tagSlugs: [],
  status: 'draft',
  pinned: false
})

const categories = ref([])
const tagOptions = ref([])
const postInfo = ref(null)
const mode = ref('write')
const loading = ref(true)
const saving = ref(false)
const uploading = ref(false)
const coverInput = ref(null)
const loadError = ref('')
const comments = ref([])
const commentTotal = ref(0)
const commentPage = ref(1)
const commentPages = ref(1)
const commentsLoading = ref(false)
const commentsError = ref('')
const deletingCommentId = ref(null)

const previewHtml = computed(() => renderMarkdown(form.content).html)
const coverStyle = computed(() => ({
  background: `linear-gradient(135deg, ${form.coverFrom}, ${form.coverTo})`
}))

async function loadInitial() {
  loading.value = true
  loadError.value = ''
  try {
    const [categoryList, tagList] = await Promise.all([api.getCategories(), api.getTags()])
    categories.value = categoryList
    tagOptions.value = tagList

    if (isEdit.value) {
      const data = await api.getAdminPost(route.params.id)
      applyDetail(data)
      await loadComments()
    }
  } catch (err) {
    if (handleError(err)) {
      return
    }
    loadError.value = err.message || '文章加载失败'
  } finally {
    loading.value = false
  }
}

function applyDetail(data) {
  form.title = data.title || ''
  form.slug = data.slug || ''
  form.excerpt = data.excerpt || ''
  form.content = data.content || ''
  form.coverFrom = data.coverFrom || '#123043'
  form.coverTo = data.coverTo || '#2a1b4d'
  form.coverImage = data.coverImage || ''
  form.categorySlug = data.categorySlug || ''
  form.tagSlugs = data.tagSlugs || []
  form.status = data.status || 'draft'
  form.pinned = Boolean(data.pinned)
  postInfo.value = data
}

function normalizeSlug() {
  form.slug = form.slug.trim().toLowerCase()
}

function pickCoverImage() {
  if (coverInput.value) {
    coverInput.value.click()
  }
}

async function onCoverFileChange(event) {
  const file = event.target.files && event.target.files[0]
  event.target.value = ''
  if (!file) {
    return
  }

  uploading.value = true
  try {
    form.coverImage = await api.uploadCoverImage(file)
    showToast('封面图已上传', 'success')
  } catch (err) {
    handleError(err)
  } finally {
    uploading.value = false
  }
}

function removeCoverImage() {
  form.coverImage = ''
}

function generateSlug() {
  const base = form.title
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
    .slice(0, 80)
  form.slug = base || `post-${Date.now().toString(36)}`
}

async function loadComments() {
  if (!isEdit.value) {
    return
  }
  commentsLoading.value = true
  commentsError.value = ''
  try {
    const data = await api.getAdminComments(route.params.id, {
      page: commentPage.value,
      size: 10
    })
    comments.value = data.items || []
    commentTotal.value = data.total || 0
    commentPage.value = data.page || 1
    commentPages.value = data.pages || 1
  } catch (err) {
    if (handleError(err)) {
      return
    }
    commentsError.value = err.message || '评论加载失败'
  } finally {
    commentsLoading.value = false
  }
}

function changeCommentPage(page) {
  if (page < 1 || page > commentPages.value) {
    return
  }
  commentPage.value = page
  loadComments()
}

async function removeComment(comment) {
  if (!window.confirm(`确定删除 ${comment.nickname} 的这条评论？删除后无法恢复。`)) {
    return
  }

  deletingCommentId.value = comment.id
  const lastOnPage = comments.value.length === 1 && commentPage.value > 1
  try {
    await api.deleteAdminComment(comment.id)
    showToast('评论已删除', 'success')
    if (lastOnPage) {
      commentPage.value -= 1
    }
    await loadComments()
  } catch (err) {
    handleError(err)
  } finally {
    deletingCommentId.value = null
  }
}

function validate() {
  if (!form.title.trim()) {
    return '请填写文章标题'
  }
  normalizeSlug()
  if (!form.slug) {
    return '请填写 Slug，或点击“用标题生成”'
  }
  if (!/^[a-z0-9]+(?:-[a-z0-9]+)*$/.test(form.slug)) {
    return 'Slug 只能包含小写字母、数字和中划线'
  }
  if (form.status === 'published' && !form.content.trim()) {
    return '发布前需要填写正文'
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
  const payload = {
    title: form.title.trim(),
    slug: form.slug.trim().toLowerCase(),
    excerpt: form.excerpt.trim(),
    content: form.content,
    coverFrom: form.coverFrom,
    coverTo: form.coverTo,
    coverImage: form.coverImage,
    categorySlug: form.categorySlug || null,
    tagSlugs: form.tagSlugs,
    status: form.status,
    pinned: form.pinned
  }

  try {
    const data = isEdit.value
      ? await api.updateAdminPost(route.params.id, payload)
      : await api.createAdminPost(payload)
    applyDetail(data)
    showToast(isEdit.value ? '文章已保存' : '文章已创建', 'success')

    if (!isEdit.value) {
      router.replace({ name: 'admin-post-edit', params: { id: data.id } })
    }
  } catch (err) {
    handleError(err)
  } finally {
    saving.value = false
  }
}

async function removePost() {
  if (!window.confirm(`确定删除《${form.title}》？删除后无法恢复。`)) {
    return
  }

  saving.value = true
  try {
    await api.deleteAdminPost(route.params.id)
    showToast('文章已删除', 'success')
    router.push({ name: 'admin-posts' })
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

onMounted(loadInitial)
</script>

<style scoped lang="scss">
.editor-page {
  padding-bottom: 20px;
}

.editor-head {
  display: flex;
  margin-bottom: 24px;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
}

.editor-heading {
  .eyebrow {
    margin-top: 12px;
  }

  .admin-page-title {
    margin-top: 4px;
  }
}

.back-link {
  display: inline-block;
  color: var(--muted);
  font-size: 12.5px;

  &:hover {
    color: var(--cyan);
    text-decoration: none;
  }
}

.editor-head-actions {
  display: flex;
  gap: 8px;
}

.editor-error {
  margin-bottom: 18px;
}

.comments-panel {
  margin-top: 18px;
  padding: 22px;
}

.comments-panel-head {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;

  h2 {
    font-size: 13.5px;
    font-weight: 600;

    .mono {
      color: var(--faint);
      font-size: 11px;
    }
  }

  p {
    margin-top: 5px;
    color: var(--faint);
    font-size: 11.5px;
  }
}

.comments-loading {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-skeleton {
  height: 56px;
  border-radius: 8px;
}

.admin-comment-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.admin-comment + .admin-comment {
  border-top: 1px dashed var(--line);
}

.admin-comment {
  padding: 14px 0;
}

.comment-head {
  display: flex;
  align-items: center;
  gap: 10px;

  .admin-badge {
    margin-left: auto;
  }
}

.comment-avatar {
  display: grid;
  width: 28px;
  height: 28px;
  flex: none;
  place-items: center;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, rgba(62, 231, 255, 0.28), rgba(139, 92, 246, 0.36));
  color: var(--text);
  font-family: var(--mono);
  font-size: 10px;
}

.comment-who {
  display: flex;
  flex-direction: column;
  gap: 2px;

  strong {
    font-size: 12.5px;
    font-weight: 600;
  }

  .mono {
    color: var(--faint);
    font-size: 9.5px;
  }
}

.comment-text {
  margin-top: 9px;
  padding-left: 38px;
  color: var(--muted);
  font-size: 12.5px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

.comments-pager {
  display: flex;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--line);
  align-items: center;
  justify-content: center;
  gap: 14px;

  .mono {
    color: var(--faint);
    font-size: 10.5px;
  }
}

.editor-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 330px;
  gap: 18px;
  align-items: start;
}

.editor-main {
  padding: 22px;
}

.title-fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.title-input {
  font-size: 16px;
  font-weight: 500;
}

.slug-row {
  display: flex;
  align-items: center;
  gap: 8px;

  .admin-input {
    flex: 1;
    min-width: 0;
    font-family: var(--mono);
    font-size: 12px;
  }
}

.slug-prefix {
  flex: none;
  color: var(--faint);
}

.counter {
  align-self: flex-end;
  color: var(--faint);
  font-size: 10.5px;
}

.content-toolbar {
  display: flex;
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
  align-items: center;
  justify-content: space-between;
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

.content-write {
  margin-top: 12px;
}

.content-textarea {
  display: block;
  width: 100%;
  min-height: 460px;
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
  min-height: 460px;
  margin-top: 12px;
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

.editor-side {
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

.side-hint {
  margin-top: 12px;
  color: var(--faint);
  font-size: 11.5px;
  line-height: 1.7;
}

.check-row {
  display: flex;
  margin-top: 14px;
  align-items: center;
  gap: 8px;
  color: var(--muted);
  font-size: 12.5px;
  cursor: pointer;

  input {
    width: 14px;
    height: 14px;
    accent-color: var(--cyan);
  }
}

.tag-field {
  margin-top: 16px;
}

.tag-label {
  display: block;
  margin-bottom: 8px;
  color: var(--muted);
  font-size: 12.5px;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-option {
  display: inline-flex;
  padding: 4px 9px;
  align-items: center;
  border: 1px solid var(--line);
  border-radius: 99px;
  color: var(--muted);
  font-size: 11.5px;
  cursor: pointer;
  transition: 0.15s;

  input {
    position: absolute;
    opacity: 0;
    pointer-events: none;
  }

  &:hover {
    border-color: var(--line-strong);
  }

  &.checked {
    border-color: rgba(139, 92, 246, 0.45);
    background: rgba(139, 92, 246, 0.12);
    color: #c4b5fd;
  }
}

.color-fields {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.color-field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;

  > span {
    color: var(--muted);
    font-size: 12px;
  }

  > div {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  input[type='color'] {
    width: 34px;
    height: 28px;
    padding: 0;
    border: 1px solid var(--line);
    border-radius: 5px;
    background: transparent;
    cursor: pointer;
  }

  code {
    color: var(--faint);
    font-family: var(--mono);
    font-size: 10.5px;
  }
}

.cover-preview {
  position: relative;
  display: flex;
  overflow: hidden;
  aspect-ratio: 16 / 9;
  margin-top: 14px;
  padding: 14px;
  align-items: flex-end;
  border: 1px solid var(--line);
  border-radius: 9px;

  img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .cover-text {
    position: relative;
    z-index: 1;
    color: rgba(255, 255, 255, 0.85);
    font-family: var(--mono);
    font-size: 10px;
    letter-spacing: 0.1em;
  }
}

.cover-actions {
  display: flex;
  margin-top: 10px;
  gap: 8px;
}

.cover-file-input {
  display: none;
}

.post-info {
  display: flex;
  margin: 0;
  flex-direction: column;
  gap: 9px;

  div {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  dt {
    color: var(--faint);
    font-size: 11.5px;
  }

  dd {
    margin: 0;
    color: var(--muted);
    font-size: 10.5px;
    text-align: right;
  }
}

@media (max-width: 980px) {
  .editor-grid {
    grid-template-columns: 1fr;
  }

  .editor-side {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .editor-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .editor-head-actions {
    width: 100%;
  }

  .editor-main {
    padding: 16px;
  }

  .comments-panel {
    padding: 16px;
  }

  .comment-text {
    padding-left: 0;
  }

  .slug-row {
    flex-wrap: wrap;

    .admin-input {
      flex-basis: 100%;
    }

    .slug-prefix {
      display: none;
    }
  }

  .editor-side {
    display: flex;
  }

  .content-textarea {
    min-height: 360px;
  }
}
</style>
