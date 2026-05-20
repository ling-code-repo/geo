export {}

declare module 'vue' {
  export interface GlobalComponents {
    ContentWrap: typeof import('./components/ContentWrap/index.vue')['default']
    DictTag: typeof import('./components/DictTag/index.vue')['default']
    Pagination: typeof import('./components/Pagination/index.vue')['default']
    // ... 其他组件
  }
}
