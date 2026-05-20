// src/router/modules/remaining.ts
export default {
  path: '/webview',
  name: 'Webview',
  component: () => import('@/views/webview/index.vue'), // 直接加载组件
  meta: {
    title: '网页视图',
    showLink: false
  }
};
