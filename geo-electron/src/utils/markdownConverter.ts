/**
 * Markdown 与富文本互转工具函数
 * 适用于芋道源码 Vue3 项目
 *
 * 使用方法:
 * import { markdownToHtml, htmlToMarkdown } from '@/utils/markdownConverter'
 */

/**
 * Markdown 转 HTML (富文本)
 * @param {string} md - Markdown 文本
 * @returns {string} HTML 文本
 */
export function markdownToHtml(md) {
  if (!md) return ''

  let html = md

  // 1. 标题转换 (需要从大到小转换，避免重复匹配)
  html = html.replace(/^###### (.*$)/gim, '<h6>$1</h6>')
  html = html.replace(/^##### (.*$)/gim, '<h5>$1</h5>')
  html = html.replace(/^#### (.*$)/gim, '<h4>$1</h4>')
  html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>')
  html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>')
  html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>')

  // 2. 代码块 (需要在行内代码之前处理)
  html = html.replace(/```(\w+)?\n([\s\S]*?)```/g, '<pre><code class="language-$1">$2</code></pre>')

  // 3. 行内代码
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>')

  // 4. 粗体 (需要在斜体之前处理)
  html = html.replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/\_\_(.+?)\_\_/g, '<strong>$1</strong>')

  // 5. 斜体
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>')
  html = html.replace(/\_(.+?)\_/g, '<em>$1</em>')

  // 6. 删除线
  html = html.replace(/~~(.+?)~~/g, '<del>$1</del>')

  // 7. 图片 (需要在链接之前处理)
  html = html.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, '<img src="$2" alt="$1" />')

  // 8. 链接
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>')

  // 9. 无序列表
  html = html.replace(/^\* (.+)$/gim, '<li>$1</li>')
  html = html.replace(/^\- (.+)$/gim, '<li>$1</li>')
  html = html.replace(/^\+ (.+)$/gim, '<li>$1</li>')

  // 将连续的 li 标签包裹在 ul 中
  html = html.replace(/(<li>[\s\S]+?<\/li>)(?!\n<li>)/g, (match) => {
    const items = match.match(/<li>[\s\S]+?<\/li>/g)
    if (items && items.length > 0) {
      return '<ul>' + items.join('') + '</ul>'
    }
    return match
  })

  // 10. 有序列表
  html = html.replace(/^\d+\. (.+)$/gim, '<li>$1</li>')

  // 11. 引用块
  html = html.replace(/^\> (.+)$/gim, '<blockquote>$1</blockquote>')
  // 合并连续的引用块
  html = html.replace(/(<\/blockquote>\n<blockquote>)/g, '\n')

  // 12. 水平线
  html = html.replace(/^---$/gim, '<hr />')
  html = html.replace(/^\*\*\*$/gim, '<hr />')
  html = html.replace(/^___$/gim, '<hr />')

  // 13. 换行
  html = html.replace(/  $/gim, '<br />')

  // 14. 段落处理
  html = html.replace(/\n\n/g, '</p><p>')
  html = '<p>' + html + '</p>'

  // 15. 清理多余的 p 标签
  html = html.replace(/<p><\/p>/g, '')
  html = html.replace(/<p>(<h[1-6]>)/g, '$1')
  html = html.replace(/(<\/h[1-6]>)<\/p>/g, '$1')
  html = html.replace(/<p>(<ul>)/g, '$1')
  html = html.replace(/(<\/ul>)<\/p>/g, '$1')
  html = html.replace(/<p>(<ol>)/g, '$1')
  html = html.replace(/(<\/ol>)<\/p>/g, '$1')
  html = html.replace(/<p>(<blockquote>)/g, '$1')
  html = html.replace(/(<\/blockquote>)<\/p>/g, '$1')
  html = html.replace(/<p>(<pre>)/g, '$1')
  html = html.replace(/(<\/pre>)<\/p>/g, '$1')
  html = html.replace(/<p>(<hr \/>)/g, '$1')
  html = html.replace(/(<hr \/>)<\/p>/g, '$1')

  return html
}

/**
 * HTML (富文本) 转 Markdown
 * @param {string} html - HTML 文本
 * @returns {string} Markdown 文本
 */
export function htmlToMarkdown(html) {
  if (!html) return ''

  let md = html

  // 1. 标题转换
  md = md.replace(/<h1[^>]*>(.*?)<\/h1>/gi, '# $1\n\n')
  md = md.replace(/<h2[^>]*>(.*?)<\/h2>/gi, '## $1\n\n')
  md = md.replace(/<h3[^>]*>(.*?)<\/h3>/gi, '### $1\n\n')
  md = md.replace(/<h4[^>]*>(.*?)<\/h4>/gi, '#### $1\n\n')
  md = md.replace(/<h5[^>]*>(.*?)<\/h5>/gi, '##### $1\n\n')
  md = md.replace(/<h6[^>]*>(.*?)<\/h6>/gi, '###### $1\n\n')

  // 2. 粗体和斜体
  md = md.replace(/<strong><em>(.*?)<\/em><\/strong>/gi, '***$1***')
  md = md.replace(/<em><strong>(.*?)<\/strong><\/em>/gi, '***$1***')
  md = md.replace(/<strong[^>]*>(.*?)<\/strong>/gi, '**$1**')
  md = md.replace(/<b[^>]*>(.*?)<\/b>/gi, '**$1**')
  md = md.replace(/<em[^>]*>(.*?)<\/em>/gi, '*$1*')
  md = md.replace(/<i[^>]*>(.*?)<\/i>/gi, '*$1*')

  // 3. 删除线
  md = md.replace(/<del[^>]*>(.*?)<\/del>/gi, '~~$1~~')
  md = md.replace(/<s[^>]*>(.*?)<\/s>/gi, '~~$1~~')

  // 4. 代码块
  md = md.replace(
    /<pre[^>]*><code[^>]*class="language-(\w+)"[^>]*>([\s\S]*?)<\/code><\/pre>/gi,
    '\n```$1\n$2```\n'
  )
  md = md.replace(/<pre[^>]*><code[^>]*>([\s\S]*?)<\/code><\/pre>/gi, '\n```\n$1```\n')

  // 5. 行内代码
  md = md.replace(/<code[^>]*>(.*?)<\/code>/gi, '`$1`')

  // 6. 图片
  md = md.replace(/<img[^>]+src="([^"]+)"[^>]*alt="([^"]*)"[^>]*>/gi, '![$2]($1)')
  md = md.replace(/<img[^>]+alt="([^"]*)"[^>]*src="([^"]+)"[^>]*>/gi, '![$1]($2)')
  md = md.replace(/<img[^>]+src="([^"]+)"[^>]*>/gi, '![]($1)')

  // 7. 链接
  md = md.replace(/<a[^>]+href="([^"]+)"[^>]*>(.*?)<\/a>/gi, '[$2]($1)')

  // 8. 无序列表
  md = md.replace(/<ul[^>]*>([\s\S]*?)<\/ul>/gi, (_, content) => {
    return content.replace(/<li[^>]*>(.*?)<\/li>/gi, '- $1\n')
  })

  // 9. 有序列表
  md = md.replace(/<ol[^>]*>([\s\S]*?)<\/ol>/gi, (_, content) => {
    let index = 1
    return content.replace(/<li[^>]*>(.*?)<\/li>/gi, () => {
      return `${index++}. $1\n`
    })
  })

  // 10. 引用块
  md = md.replace(/<blockquote[^>]*>(.*?)<\/blockquote>/gi, '> $1\n')

  // 11. 水平线
  md = md.replace(/<hr\s*\/?>/gi, '\n---\n')

  // 12. 换行
  md = md.replace(/<br\s*\/?>/gi, '\n')

  // 13. 段落
  md = md.replace(/<p[^>]*>(.*?)<\/p>/gi, '$1\n\n')
  md = md.replace(/<div[^>]*>(.*?)<\/div>/gi, '$1\n\n')

  // 14. 清理所有剩余的 HTML 标签
  md = md.replace(/<[^>]+>/g, '')

  // 15. HTML 实体解码
  md = md.replace(/&nbsp;/g, ' ')
  md = md.replace(/&lt;/g, '<')
  md = md.replace(/&gt;/g, '>')
  md = md.replace(/&amp;/g, '&')
  md = md.replace(/&quot;/g, '"')
  md = md.replace(/&#39;/g, "'")
  md = md.replace(/&ldquo;/g, '"')
  md = md.replace(/&rdquo;/g, '"')
  md = md.replace(/&lsquo;/g, "'")
  md = md.replace(/&rsquo;/g, "'")

  // 16. 清理多余的空行
  md = md.replace(/\n{3,}/g, '\n\n')
  md = md.trim()

  return md
}

/**
 * 从 Markdown 中提取元信息
 * @param {string} markdown - Markdown 文本
 * @returns {Object} { title, content, tags, category, cover }
 */
export function parseMarkdownMetadata(markdown) {
  // const lines:any = markdown.split('\n')
  let title = ''
  let content = markdown
  let tags = []
  let category = ''
  let cover = ''

  // 提取标题 (第一个 # 标题)
  const titleMatch = markdown.match(/^#\s+(.+)$/m)
  if (titleMatch) {
    title = titleMatch[1].trim()
  }

  // 提取标签 (格式: <!-- tags: JavaScript, Vue, Node.js -->)
  const tagsMatch = markdown.match(/<!--\s*tags:\s*(.+?)\s*-->/)
  if (tagsMatch) {
    tags = tagsMatch[1].split(',').map((t) => t.trim())
    content = content.replace(tagsMatch[0], '').trim()
  }

  // 提取分类 (格式: <!-- category: 前端开发 -->)
  const categoryMatch = markdown.match(/<!--\s*category:\s*(.+?)\s*-->/)
  if (categoryMatch) {
    category = categoryMatch[1].trim()
    content = content.replace(categoryMatch[0], '').trim()
  }

  // 提取封面图 (格式: <!-- cover: https://example.com/cover.jpg -->)
  const coverMatch = markdown.match(/<!--\s*cover:\s*(.+?)\s*-->/)
  if (coverMatch) {
    cover = coverMatch[1].trim()
    content = content.replace(coverMatch[0], '').trim()
  }

  return {
    title,
    content,
    tags,
    category,
    cover
  }
}

/**
 * 添加元信息到 Markdown
 * @param {string} markdown - Markdown 文本
 * @param {Object} metadata - { tags, category, cover }
 * @returns {string} 带元信息的 Markdown
 */
export function addMarkdownMetadata(markdown, metadata) {
  let result = markdown

  if (metadata.tags && metadata.tags.length > 0) {
    result += `\n\n<!-- tags: ${metadata.tags.join(', ')} -->`
  }

  if (metadata.category) {
    result += `\n<!-- category: ${metadata.category} -->`
  }

  if (metadata.cover) {
    result += `\n<!-- cover: ${metadata.cover} -->`
  }

  return result
}

/**
 * 处理 Markdown 中的图片路径
 * @param {string} markdown - Markdown 文本
 * @param {Function} handler - 图片处理函数 (path) => newPath
 * @returns {Promise<string>} 处理后的 Markdown
 */
export async function processMarkdownImages(markdown, handler) {
  const imageRegex = /!\[([^\]]*)\]\(([^)]+)\)/g
  let processedMarkdown = markdown
  const matches = [...markdown.matchAll(imageRegex)]

  for (const match of matches) {
    const [fullMatch, alt, imagePath] = match

    // 如果是本地路径或相对路径，调用处理函数
    if (!imagePath.startsWith('http://') && !imagePath.startsWith('https://')) {
      try {
        const newPath = await handler(imagePath)
        processedMarkdown = processedMarkdown.replace(fullMatch, `![${alt}](${newPath})`)
      } catch (error) {
        console.error(`处理图片失败: ${imagePath}`, error)
      }
    }
  }

  return processedMarkdown
}

interface MarkdownImage {
  alt: string
  src: string
}
/**
 * 提取 Markdown 中的所有图片链接
 * @param {string} markdown - Markdown 文本
 * @returns {Array} 图片链接数组 [{ alt, src }, ...]
 */
export function extractMarkdownImages(markdown) {
  const imageRegex = /!\[([^\]]*)\]\(([^)]+)\)/g
  const images:MarkdownImage[] = []
  let match

  while ((match = imageRegex.exec(markdown)) !== null) {
    images.push({
      alt: match[1],
      src: match[2]
    })
  }

  return images
}

/**
 * 计算 Markdown 的字数 (排除代码块和标记语法)
 * @param {string} markdown - Markdown 文本
 * @returns {number} 字数
 */
export function countMarkdownWords(markdown) {
  let text = markdown

  // 移除代码块
  text = text.replace(/```[\s\S]*?```/g, '')

  // 移除行内代码
  text = text.replace(/`[^`]+`/g, '')

  // 移除标题标记
  text = text.replace(/^#+\s+/gim, '')

  // 移除加粗和斜体标记
  text = text.replace(/[*_]{1,3}/g, '')

  // 移除链接和图片
  text = text.replace(/!?\[([^\]]+)\]\([^)]+\)/g, '$1')

  // 移除引用标记
  text = text.replace(/^>\s+/gim, '')

  // 移除列表标记
  text = text.replace(/^[\*\-\+]\s+/gim, '')
  text = text.replace(/^\d+\.\s+/gim, '')

  // 计算中文字符
  const chineseChars = (text.match(/[\u4e00-\u9fa5]/g) || []).length

  // 计算英文单词
  const englishWords = (text.match(/[a-zA-Z]+/g) || []).length

  return chineseChars + englishWords
}

// 默认导出
export default {
  markdownToHtml,
  htmlToMarkdown,
  parseMarkdownMetadata,
  addMarkdownMetadata,
  processMarkdownImages,
  extractMarkdownImages,
  countMarkdownWords
}
