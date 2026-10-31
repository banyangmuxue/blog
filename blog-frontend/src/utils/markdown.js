import { marked } from 'marked'
import DOMPurify from 'dompurify'

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function plainText(value) {
  return String(value)
    .replace(/`([^`]+)`/g, '$1')
    .replace(/[*_~]/g, '')
    .replace(/\[([^\]]+)\]\([^)]*\)/g, '$1')
    .trim()
}

function slugify(value, index) {
  const text = plainText(value)
  const ascii = text
    .toLowerCase()
    .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    .replace(/^-+|-+$/g, '')
  return `heading-${index}${ascii ? `-${ascii}` : ''}`
}

export function renderMarkdown(source = '') {
  const headings = []
  const tokens = marked.lexer(source)
  tokens.forEach((token) => {
    if (token.type === 'heading' && token.depth <= 3 && token.depth >= 2) {
      headings.push({
        id: slugify(token.text, headings.length + 1),
        text: plainText(token.text),
        level: token.depth
      })
    }
  })

  const renderer = new marked.Renderer()
  let headingIndex = 0

  renderer.heading = (text, level) => {
    if (level === 2 || level === 3) {
      const heading = headings[headingIndex]
      headingIndex += 1
      if (heading) {
        return `<h${level} id="${heading.id}">${text}</h${level}>`
      }
    }
    return `<h${level}>${text}</h${level}>`
  }

  renderer.code = (code, infostring) => {
    const lang = (infostring || '').trim().split(/\s+/)[0] || 'code'
    return `<pre data-lang="${escapeHtml(lang)}"><code>${escapeHtml(code)}</code></pre>`
  }

  renderer.link = (href, title, text) => {
    const external = /^https?:\/\//.test(href)
    const attrs = external ? ' target="_blank" rel="noopener noreferrer"' : ''
    const titleAttr = title ? ` title="${escapeHtml(title)}"` : ''
    return `<a href="${escapeHtml(href)}"${titleAttr}${attrs}>${text}</a>`
  }

  const html = marked.parse(source, { renderer, gfm: true, breaks: false })
  return {
    html: DOMPurify.sanitize(html, { ADD_ATTR: ['target', 'data-lang'] }),
    headings
  }
}
