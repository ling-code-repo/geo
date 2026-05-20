import { onUnmounted } from 'vue'
import {
  initSocket,
  closeSocket,
  subscribeEvent,
  subscribeConnection,
  sendMessage,
  isSocketConnected,
  getSocketStatus
} from '@/utils/socket'
import { getRefreshToken } from "@/utils/auth"

/**
 * 全局 WebSocket 组合式函数
 * 用于在 Vue 组件中使用全局 WebSocket（连接服务器端）
 */
export function useGlobalSocket() {
  const SOCKET_NAME = 'Global'

  const initGlobalSocket = () => {
    const token = getRefreshToken()
    if (!token) {
      console.error('[GlobalSocket] 未找到 token，无法连接 WebSocket')
      return
    }

    const server = (
      import.meta.env.VITE_BASE_URL + '/infra/ws'
    ).replace('http', 'ws').replace('https', 'wss') + '?token=' + token
    initSocket(server, SOCKET_NAME)
    console.log(`[GlobalSocket] 正在连接: ${server}`)
  }

  const closeGlobalSocket = () => {
    closeSocket(SOCKET_NAME)
  }

  // 存储所有订阅的取消函数
  const unsubscribers: Array<() => void> = []

  /**
   * 订阅消息事件
   */
  const subscribeGlobalEvent = (
    eventType: string,
    handler: (data: any) => void,
    autoUnsubscribe = true
  ) => {
    const unsubscribe = subscribeEvent(SOCKET_NAME, eventType, handler)

    if (autoUnsubscribe) {
      unsubscribers.push(unsubscribe)
    }

    return unsubscribe
  }

  /**
   * 订阅连接状态变化
   */
  const onGlobalStatusChange = (
    handler: (status: 'connected' | 'disconnected' | 'error') => void,
    autoUnsubscribe = true
  ) => {
    const unsubscribe = subscribeConnection(SOCKET_NAME, handler)

    if (autoUnsubscribe) {
      unsubscribers.push(unsubscribe)
    }

    return unsubscribe
  }

  /**
   * 发送消息到服务器
   */
  const globalSend = (type: string, content?: any) => {
    return sendMessage(SOCKET_NAME, type, content)
  }

  /**
   * 获取连接状态
   */
  const getGlobalStatus = () => {
    return getSocketStatus(SOCKET_NAME)
  }

  // 组件卸载时自动取消所有订阅
  onUnmounted(() => {
    unsubscribers.forEach(fn => fn())
    unsubscribers.length = 0
  })

  return {
    globalConnected: isSocketConnected(SOCKET_NAME),
    subscribeGlobalEvent,
    onGlobalStatusChange,
    globalSend,
    closeGlobalSocket,
    initGlobalSocket,
    getGlobalStatus
  }
}
