<!-- src/views/webview/index.vue -->
<template>
  <div class="webview-page">
    <!-- 控制栏 -->
    <div class="controls">
      <span>{{currentName}}授权</span>
      <button @click="openDevTools">开发者工具</button>
      <button @click="showCachePath">获取路径</button>
      <button @click="close">关闭</button>
    </div>

    <!-- webview 容器 -->
    <div ref="webviewContainer" class="webview-container" ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from "@/hooks/web/useMessage";
import { platforms, getNameAndAvatar, AccountInfo } from "@/utils/geo/platform";
import { AccountApi } from "@/api/geo/account";
import { getDataPath } from "@/utils/electronAPI";
const route = useRoute()
const router = useRouter()


const message = useMessage(); // 消息弹窗

const webviewContainer = ref<HTMLDivElement>()
const webviewElement = ref<any>(null)
const currentUrl = ref('')
const currentName = ref('')
let isWebviewReady = false

const platform = ref(null)

onMounted(async () => {
  const id = route.query.id as string
  platform.value = platforms.filter(p=> (p.id+'') == id)[0]
  currentUrl.value = platform.value.url
  currentName.value = platform.value.type.valueOf()

  // console.log('Webview URL:', currentUrl.value)

  await nextTick()

  // 创建 webview 元素
  createWebview()

  applyFullscreenStyles()
})


// 添加打开 DevTools 的方法
const openDevTools = () => {
  if (webviewElement.value) {
    try {
      webviewElement.value.openDevTools()
      console.log('DevTools 已打开')
    } catch (error) {
      console.error('打开 DevTools 失败:', error)
    }
  } else {
    console.error('Webview 元素不存在')
  }
}

const showCachePath = async () => {
  console.log("path", await getDataPath());
}

// 创建 webview 元素
const createWebview = () => {
  if (!webviewContainer.value) return

  webviewContainer.value.innerHTML = ''

  const webview = document.createElement('webview')
  webview.src = currentUrl.value
  webview.allowpopups = true
  webview.allowfullscreen = true
  webview.setAttribute('webpreferences', 'allowRunningInsecureContent=yes, nodeIntegrationInSubFrames=true')
  webview.className = 'webview-iframe'
  webview.partition="persist:"+platform.value.persist
  // 添加内联样式确保填充
  webview.style.cssText = `
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  `
  // webview.addEventListener('dom-ready', onDomReady)
  // webview.addEventListener('did-finish-load', onLoadFinish)
  // webview.addEventListener('did-fail-load', onLoadFail)

  webviewContainer.value.appendChild(webview)
  webviewElement.value = webview

  // console.log('Webview 元素已创建:', webview)
}

// Webview 事件处理
const onDomReady = () => {
  console.log('Webview DOM ready')
  isWebviewReady = true

  // 等待页面完全加载
  /*setTimeout(() => {
    checkWebviewContent()
  }, 1000)*/
}

const onLoadFinish = () => {
  console.log('Webview 加载完成')
}

const onLoadFail = (event: any) => {
  console.error('Webview 加载失败:', event)
}

// 检查 webview 内容
const checkWebviewContent = async () => {
  console.log('开始检查 webview 内容...')

  if (!webviewElement.value || !isWebviewReady) {
    console.error('Webview 未就绪')
    return
  }

  try {
    // 执行 JavaScript
    const result = await webviewElement.value.executeJavaScript(`
      (function() {
        console.log('Webview 内部脚本执行成功');
        return {
          success: true,
          title: document.title || '无标题',
          url: window.location.href,
          timestamp: Date.now()
        };
      })()
    `, true)

    const result2 = await webviewElement.value.executeJavaScript(`
      (function() {
        console.log('Webview 内部脚本执行成功');
        return document.querySelector("#header-avatar div").getAttribute('style');
      })()
    `, true)
    console.log('成功获取页面信息:', result2)

    //

    console.log('Webview 脚本执行结果:', result)

    if (result && result.success) {
      console.log('成功获取页面信息:', result)
      // 显示页面标题
      if (result.title) {
        alert(`页面标题: ${result.title}\\nURL: ${result.url}`)
      }
    }

  } catch (error: any) {
    console.error('检查 webview 内容失败:', error)
  }
}

// 获取页面元素
const getElements = async () => {
  if (!webviewElement.value || !isWebviewReady) {
    alert('Webview 尚未准备好')
    return
  }

  try {
    const elements = await webviewElement.value.executeJavaScript(`
      (function() {
        // 简化选择器，避免复杂查询
        const simpleElements = {
          title: document.title,
          url: window.location.href,
          buttonCount: document.querySelectorAll('button').length,
          inputCount: document.querySelectorAll('input').length,
          linkCount: document.querySelectorAll('a').length,
          // 获取前几个按钮的文本
          buttons: Array.from(document.querySelectorAll('button')).slice(0, 5).map(btn => ({
            text: btn.textContent?.trim().substring(0, 20) || '无文本',
            className: btn.className.substring(0, 20)
          }))
        };

        console.log('获取到的元素信息:', simpleElements);
        return simpleElements;
      })()
    `, true)

    console.log('元素获取结果:', elements)

    if (elements) {
      const message = `
页面标题: ${elements.title}
URL: ${elements.url}
按钮数量: ${elements.buttonCount}
输入框数量: ${elements.inputCount}
链接数量: ${elements.linkCount}

前5个按钮:
${elements.buttons.map((btn: any, i: number) => `  ${i+1}. ${btn.text} (${btn.className})`).join('\\n')}
      `.trim()

      alert(message)
    }

  } catch (error: any) {
    console.error('获取元素失败:', error)
    alert('获取元素失败: ' + (error.message || '未知错误'))
  }
}

// 调试函数
const debugWebview = () => {
  console.log('=== Webview 调试信息 ===')
  console.log('webviewElement:', webviewElement.value)
  console.log('currentUrl:', currentUrl.value)
  console.log('isWebviewReady:', isWebviewReady)

  if (webviewElement.value) {
    console.log('Webview 属性:')
    console.log('- src:', webviewElement.value.src)
    console.log('- nodeName:', webviewElement.value.nodeName)
    console.log('- tagName:', webviewElement.value.tagName)

    // 尝试获取更多信息
    try {
      console.log('- getWebContentsId?:', typeof webviewElement.value.getWebContentsId)
      if (typeof webviewElement.value.getWebContentsId === 'function') {
        const id = webviewElement.value.getWebContentsId()
        console.log('- WebContents ID:', id)
      }
    } catch (error) {
      console.log('- 无法获取 WebContents ID:', error)
    }
  }
}

// 清理
onUnmounted(() => {
  // if (webviewElement.value) {
  //   webviewElement.value.removeEventListener('dom-ready', onDomReady)
  //   webviewElement.value.removeEventListener('did-finish-load', onLoadFinish)
  //   webviewElement.value.removeEventListener('did-fail-load', onLoadFail)
  // }
  removeAddedStyles()
})



// 原有的样式函数保持不变
const applyFullscreenStyles = () => {
  // 保存原始类名
  const originalClasses = {
    body: document.body.className,
    mainContainer: document.querySelector('.main-container')?.className || '',
    elMain: document.querySelector('.el-main')?.className || '',
    appMain: document.querySelector('.app-main')?.className || ''
  }

  // 保存到 window 对象以便恢复
  window._originalLayoutState = {
    classes: originalClasses,
    styles: new Map()
  }

  // 应用全屏样式
  const styleOverrides = `
    .webview-fullscreen-mode .el-aside,
    .webview-fullscreen-mode .el-header,
    .webview-fullscreen-mode .navbar,
    .webview-fullscreen-mode .pure-admin-tabs,
    .webview-fullscreen-mode .el-breadcrumb {
      display: none !important;
    }

    .webview-fullscreen-mode .main-container,
    .webview-fullscreen-mode .el-main,
    .webview-fullscreen-mode .app-main {
      position: fixed !important;
      top: 0 !important;
      left: 0 !important;
      width: 100vw !important;
      height: 100vh !important;
      margin: 0 !important;
      padding: 0 !important;
      z-index: 9998 !important;
      overflow: hidden !important;
    }

    .webview-fullscreen-mode .el-main {
      padding-top: 0 !important;
    }

    .webview-fullscreen-mode .el-scrollbar__wrap {
      margin: 0 !important;
      flex-wrap: nowrap !important;
    }
  `

  const styleElement = document.createElement('style')
  styleElement.id = 'webview-fullscreen-styles'
  styleElement.textContent = styleOverrides
  document.head.appendChild(styleElement)

  // 添加全屏类
  document.body.classList.add('webview-fullscreen-mode')
  document.querySelector('.main-container')?.classList.add('webview-fullscreen-mode')
  document.querySelector('.el-main')?.classList.add('webview-fullscreen-mode')
  document.querySelector('.app-main')?.classList.add('webview-fullscreen-mode')

  // 设置当前组件样式
  const webviewPage = document.querySelector('.webview-page')
  if (webviewPage) {
    window._originalLayoutState.styles.set(
      webviewPage,
      (webviewPage as HTMLElement).style.cssText
    )
    ;(webviewPage as HTMLElement).style.cssText = `
      position: fixed !important;
      top: 0 !important;
      left: 0 !important;
      width: 100vw !important;
      height: 100vh !important;
      margin: 0 !important;
      padding: 0 !important;
      z-index: 9999 !important;
      background: white !important;
      display: flex !important;
      flex-direction: column !important;
    `
  }
}

const removeAddedStyles = () => {
  // 移除样式标签
  const styleElement = document.getElementById('webview-fullscreen-styles')
  if (styleElement) {
    styleElement.remove()
  }

  // 移除全屏类
  document.body.classList.remove('webview-fullscreen-mode')
  document.querySelectorAll('.webview-fullscreen-mode').forEach(el => {
    el.classList.remove('webview-fullscreen-mode')
  })

  // 恢复原始样式
  if (window._originalLayoutState?.styles) {
    window._originalLayoutState.styles.forEach((originalStyle, element) => {
      (element as HTMLElement).style.cssText = originalStyle
    })
  }

  // 清理
  delete window._originalLayoutState
}

const close = async () => {

  const info:AccountInfo =  await getNameAndAvatar(platform.value.type,webviewElement.value)
  if (!info || !info.name) {
    message.error(platform.value.type + "授权失败！")
  }else{
    try{
      await AccountApi.saveOrUpdate({name: info.name,
        avatar: info.avatarUrl,
        value: platform.value.id})
      message.success(platform.value.type + "授权成功！")
    }catch(error){
      message.error(platform.value.type + "授权失败！")
      console.log("授权失败！",error);
    }

  }
  destroyWebview()
  removeAddedStyles()
  setTimeout(() => {
    router.back()
  }, 50)
}

// 新增：销毁 webview 的方法
const destroyWebview = () => {
  if (webviewElement.value) {
    console.log('🗑️ 正在销毁 webview...')

    // 移除事件监听器
    webviewElement.value.removeEventListener('dom-ready', onDomReady)
    webviewElement.value.removeEventListener('did-finish-load', onLoadFinish)
    webviewElement.value.removeEventListener('did-fail-load', onLoadFail)

    // 从 DOM 中移除
    if (webviewContainer.value) {
      webviewContainer.value.innerHTML = ''
    }

    // 清空引用
    webviewElement.value = null

    console.log('✅ Webview 已销毁')
  }
}

// 暴露调试函数到全局
window.debugWebview = debugWebview
</script>

<style scoped>
.webview-page {
  /* 样式在 JS 中设置 */
}

.controls {
  height: 50px;
  background: white;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 8px;
  flex-shrink: 0;
  justify-content: space-between;
}

/* 添加这个样式 */
.webview-container {
  flex: 1;
  width: 100%;
  overflow: hidden;
  position: relative;
}

.controls button {
  padding: 6px 12px;
  background: #4a5568;
  border: none;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.controls button:hover {
  background: #5a6578;
}

.webview-iframe {
  width: 100%;
  height: 100%;  /* 改为 100% 填充容器 */
  border: none;
  background: white;
}
</style>
