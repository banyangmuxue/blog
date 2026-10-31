import { reactive } from 'vue'
import { api } from '@/api'

const defaultConfig = {
  name: '记录代码与生活',
  shortName: 'B/',
  logoText: 'B/',
  logoUrl: '',
  title: '记录代码与生活',
  subtitle: '前端、后端与持续学习 — 一个个人博客',
  description: '',
  author: '',
  announcement: '',
  footerText: '',
  icp: '',
  foundedAt: '',
  socials: [],
  features: { comments: true, review: true, guestbook: true, search: true }
}

const state = reactive({
  config: { ...defaultConfig },
  profile: null,
  categories: [],
  tags: [],
  loaded: false,
  error: ''
})

let loadPromise = null

async function load() {
  try {
    const [config, profile, categories, tags] = await Promise.all([
      api.getSiteConfig(),
      api.getProfile(),
      api.getCategories(),
      api.getTags()
    ])
    state.config = { ...defaultConfig, ...config }
    state.profile = profile
    state.categories = categories
    state.tags = tags
    state.error = ''
  } catch (error) {
    state.error = error.message || '站点信息加载失败'
  } finally {
    state.loaded = true
  }
  return state
}

export function loadSite(force = false) {
  if (force) {
    loadPromise = null
    state.loaded = false
  }
  if (!loadPromise) loadPromise = load()
  return loadPromise
}

export function useSite() {
  return { state, loadSite }
}
