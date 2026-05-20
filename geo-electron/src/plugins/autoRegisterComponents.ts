// src/plugins/autoRegisterComponents.ts
import type { App } from 'vue'
export default {
  install(app: App) {
    // 扫描所有 Vue 组件文件
    const modules = import.meta.glob('@/components/**/*.vue', {
      eager: true,
      import: 'default'
    }) as Record<string, any>

    Object.entries(modules).forEach(([path, component]) => {
      // 获取组件名
      const name = path
        .split('/')
        .pop()!
        .replace(/\.vue$/, '')
        .replace(/^./, char => char.toUpperCase())

      // 优先使用组件的 name 选项
      const componentName = component.name || name

      if (componentName) {
        app.component(componentName, component)
        // console.log(`✅ 注册: ${componentName}`)
      }
    })
  }
}
