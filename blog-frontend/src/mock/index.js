import {
  siteConfig,
  profile,
  categories,
  tags,
  posts,
  comments,
  messages,
  links
} from './data'

const wait = (ms = 260) => new Promise((resolve) => setTimeout(resolve, ms))

const publishedPosts = posts.filter((post) => post.status === 'published')

function categoriesOf(post) {
  return categories.find((item) => item.slug === post.categorySlug)
}

function tagsOf(post) {
  return post.tagSlugs
    .map((slug) => tags.find((item) => item.slug === slug))
    .filter(Boolean)
}

function readingTimeOf(content) {
  const chars = content.replace(/\s/g, '').length
  return Math.max(2, Math.round(chars / 380))
}

function excerptOf(content) {
  return content
    .replace(/^#.*$/gm, '')
    .replace(/```[\s\S]*?```/g, '')
    .replace(/[>*_`\-|]/g, '')
    .replace(/\n+/g, ' ')
    .trim()
}

function enrich(post) {
  return {
    ...post,
    category: categoriesOf(post),
    tags: tagsOf(post),
    readingTime: readingTimeOf(post.content),
    coverLabel: post.publishedAt.slice(0, 7).replace('-', '.')
  }
}

function paginate(list, page = 1, size = 10) {
  const total = list.length
  const pages = Math.max(1, Math.ceil(total / size))
  const current = Math.min(Math.max(1, page), pages)
  return {
    items: list.slice((current - 1) * size, current * size),
    total,
    page: current,
    size,
    pages
  }
}

function sortedByDate(list = publishedPosts) {
  return [...list].sort(
    (a, b) => new Date(b.publishedAt) - new Date(a.publishedAt)
  )
}

function sortList(list, sort) {
  if (sort === 'oldest') {
    return [...list].sort((a, b) => new Date(a.publishedAt) - new Date(b.publishedAt))
  }
  if (sort === 'views') {
    return [...list].sort((a, b) => b.views - a.views)
  }
  return sortedByDate(list)
}

function snippetOf(content, keyword) {
  const plain = excerptOf(content)
  const index = plain.toLowerCase().indexOf(keyword.toLowerCase())
  if (index < 0) return plain.slice(0, 90)
  const start = Math.max(0, index - 32)
  return (start > 0 ? '…' : '') + plain.slice(start, index + keyword.length + 48) + '…'
}

export async function getSiteConfig() {
  await wait(160)
  return { ...siteConfig }
}

export async function getProfile() {
  await wait(200)
  return { ...profile }
}

export async function getCategories() {
  await wait(200)
  return categories
    .map((category) => ({
      ...category,
      count: publishedPosts.filter((post) => post.categorySlug === category.slug).length
    }))
    .filter((category) => category.count > 0)
}

export async function getTags() {
  await wait(200)
  return tags
    .map((tag) => ({
      ...tag,
      count: publishedPosts.filter((post) => post.tagSlugs.includes(tag.slug)).length
    }))
    .filter((tag) => tag.count > 0)
    .sort((a, b) => b.count - a.count || a.name.localeCompare(b.name))
}

export async function getPosts(params = {}) {
  await wait(320)
  const { page = 1, size = 10, category, tag, q, pinned, sort } = params
  let list = sortedByDate()

  if (category) list = list.filter((post) => post.categorySlug === category)
  if (tag) list = list.filter((post) => post.tagSlugs.includes(tag))
  if (pinned) list = list.filter((post) => post.pinned)

  if (q) {
    const keyword = q.trim().toLowerCase()
    const matched = list
      .filter((post) => {
        const tagNames = tagsOf(post).map((item) => item.name).join(' ')
        return (
          post.title.toLowerCase().includes(keyword) ||
          post.excerpt.toLowerCase().includes(keyword) ||
          post.content.toLowerCase().includes(keyword) ||
          tagNames.toLowerCase().includes(keyword)
        )
      })
      .sort((a, b) => {
        const aTitle = a.title.toLowerCase().includes(keyword) ? 1 : 0
        const bTitle = b.title.toLowerCase().includes(keyword) ? 1 : 0
        return bTitle - aTitle || new Date(b.publishedAt) - new Date(a.publishedAt)
      })
    const result = paginate(matched.map(enrich), page, size)
    return {
      ...result,
      items: result.items.map((post) => ({
        ...post,
        snippet: snippetOf(post.content, keyword)
      }))
    }
  }

  return paginate(sortList(list, sort).map(enrich), page, size)
}

export async function getPost(slug) {
  await wait(300)
  const post = publishedPosts.find((item) => item.slug === slug)
  return post ? enrich(post) : null
}

export async function getAdjacentPosts(slug) {
  await wait(180)
  const list = sortedByDate()
  const index = list.findIndex((post) => post.slug === slug)
  if (index < 0) return { prev: null, next: null }
  return {
    prev: index < list.length - 1 ? enrich(list[index + 1]) : null,
    next: index > 0 ? enrich(list[index - 1]) : null
  }
}

export async function increaseViews(slug, visitorKey = 'blog-visitor') {
  const logKey = 'blog-views'
  let log = {}
  try {
    log = JSON.parse(localStorage.getItem(logKey) || '{}')
  } catch (error) {
    log = {}
  }
  const recordKey = `${visitorKey}:${slug}`
  const now = Date.now()
  const last = log[recordKey] || 0
  if (now - last < 30 * 60 * 1000) return false
  log[recordKey] = now
  try {
    localStorage.setItem(logKey, JSON.stringify(log))
  } catch (error) {
    // 忽略存储失败，不影响阅读
  }
  const post = posts.find((item) => item.slug === slug)
  if (post) post.views += 1
  return true
}

export async function getComments(slug) {
  await wait(240)
  return comments
    .filter((comment) => comment.slug === slug && comment.status === 'approved')
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
}

export async function submitComment(payload) {
  await wait(500)
  const comment = {
    id: comments.length + 1,
    slug: payload.slug,
    nickname: payload.nickname,
    content: payload.content,
    createdAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
    status: siteConfig.features.review ? 'pending' : 'approved',
    reply: null
  }
  comments.push(comment)
  return { status: comment.status }
}

export async function getMessages(params = {}) {
  await wait(260)
  const list = messages
    .filter((message) => message.status === 'approved')
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  return paginate(list, params.page || 1, params.size || 10)
}

export async function submitMessage(payload) {
  await wait(500)
  const message = {
    id: messages.length + 1,
    nickname: payload.nickname,
    content: payload.content,
    createdAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
    status: siteConfig.features.review ? 'pending' : 'approved',
    reply: null
  }
  messages.push(message)
  return { status: message.status }
}

export async function getLinks() {
  await wait(240)
  return links
    .filter((link) => link.visible)
    .sort((a, b) => a.order - b.order)
}

export async function getArchive() {
  await wait(300)
  const groups = {}
  sortedByDate().forEach((post) => {
    const year = post.publishedAt.slice(0, 4)
    const month = post.publishedAt.slice(5, 7)
    if (!groups[year]) groups[year] = {}
    if (!groups[year][month]) groups[year][month] = []
    groups[year][month].push(enrich(post))
  })
  return Object.keys(groups)
    .sort((a, b) => b.localeCompare(a))
    .map((year) => ({
      year,
      count: Object.values(groups[year]).reduce((sum, list) => sum + list.length, 0),
      months: Object.keys(groups[year])
        .sort((a, b) => b.localeCompare(a))
        .map((month) => ({
          month,
          label: `${Number(month)} 月`,
          items: groups[year][month]
        }))
    }))
}

export async function getLatestPosts(limit = 6) {
  await wait(200)
  return sortedByDate().slice(0, limit).map(enrich)
}
