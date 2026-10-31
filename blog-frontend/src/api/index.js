import * as mock from '@/mock'

const USE_MOCK = process.env.VUE_APP_USE_MOCK !== 'false'
const BASE = process.env.VUE_APP_API_BASE || '/api'

function buildQuery(params = {}) {
  const query = Object.entries(params)
    .filter(([, value]) => value !== undefined && value !== null && value !== '')
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&')
  return query ? `?${query}` : ''
}

async function request(path, options = {}) {
  const { method = 'GET', params, body, formData } = options
  const response = await fetch(
    `${BASE}${path}${buildQuery(params)}`,
    {
      method,
      credentials: 'include',
      headers: body ? { 'Content-Type': 'application/json' } : undefined,
      body: formData || (body ? JSON.stringify(body) : undefined)
    }
  )
  const payload = await response.json().catch(() => ({}))
  if (!response.ok || payload.code) {
    const error = new Error(payload.message || `请求失败（${response.status}）`)
    error.status = response.status
    throw error
  }
  return payload.data
}

export const api = {
  getSiteConfig: () =>
    USE_MOCK ? mock.getSiteConfig() : request('/site'),

  getProfile: () =>
    USE_MOCK ? mock.getProfile() : request('/profile'),

  getCategories: () =>
    USE_MOCK ? mock.getCategories() : request('/categories'),

  getTags: () =>
    USE_MOCK ? mock.getTags() : request('/tags'),

  getPosts: (params) =>
    USE_MOCK ? mock.getPosts(params) : request('/posts', { params }),

  getPost: (slug) =>
    USE_MOCK ? mock.getPost(slug) : request(`/posts/${slug}`),

  getAdjacentPosts: (slug) =>
    USE_MOCK ? mock.getAdjacentPosts(slug) : request(`/posts/${slug}/adjacent`),

  increaseViews: (slug) =>
    USE_MOCK
      ? mock.increaseViews(slug)
      : request(`/posts/${slug}/view`, { method: 'POST' }),

  getComments: (slug) =>
    USE_MOCK ? mock.getComments(slug) : request(`/posts/${slug}/comments`),

  submitComment: (payload) =>
    USE_MOCK
      ? mock.submitComment(payload)
      : request(`/posts/${payload.slug}/comments`, { method: 'POST', body: payload }),

  getMessages: (params) =>
    USE_MOCK ? mock.getMessages(params) : request('/messages', { params }),

  submitMessage: (payload) =>
    USE_MOCK ? mock.submitMessage(payload) : request('/messages', { method: 'POST', body: payload }),

  getLinks: () =>
    USE_MOCK ? mock.getLinks() : request('/links'),

  getArchive: () =>
    USE_MOCK ? mock.getArchive() : request('/archive'),

  getLatestPosts: (limit = 6) =>
    USE_MOCK ? mock.getLatestPosts(limit) : request('/posts/latest', { params: { limit } }),

  // 管理后台接口不走 Mock，必须连接真实后端
  getAdminSession: () => request('/admin/session'),

  adminLogin: (payload) =>
    request('/admin/login', { method: 'POST', body: payload }),

  adminLogout: () =>
    request('/admin/logout', { method: 'POST' }),

  getAdminPosts: (params) =>
    request('/admin/posts', { params }),

  getAdminPost: (id) =>
    request(`/admin/posts/${id}`),

  createAdminPost: (payload) =>
    request('/admin/posts', { method: 'POST', body: payload }),

  updateAdminPost: (id, payload) =>
    request(`/admin/posts/${id}`, { method: 'PUT', body: payload }),

  updateAdminPostStatus: (id, status) =>
    request(`/admin/posts/${id}/status`, { method: 'PUT', body: { status } }),

  deleteAdminPost: (id) =>
    request(`/admin/posts/${id}`, { method: 'DELETE' }),

  getAdminComments: (postId, params) =>
    request(`/admin/posts/${postId}/comments`, { params }),

  deleteAdminComment: (id) =>
    request(`/admin/comments/${id}`, { method: 'DELETE' }),

  getAdminSite: () => request('/admin/site'),

  updateAdminSite: (payload) =>
    request('/admin/site', { method: 'PUT', body: payload }),

  getAdminProfile: () => request('/admin/profile'),

  updateAdminProfile: (payload) =>
    request('/admin/profile', { method: 'PUT', body: payload }),

  uploadAvatar: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request('/admin/upload/avatar', { method: 'POST', formData })
  },

  uploadCoverImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request('/admin/upload/cover', { method: 'POST', formData })
  },

  uploadLogo: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request('/admin/upload/logo', { method: 'POST', formData })
  }
}
