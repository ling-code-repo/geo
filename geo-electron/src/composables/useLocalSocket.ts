import { onMounted, onUnmounted, ref } from "vue";
import {
  initSocket,
  closeSocket,
  subscribeEvent,
  subscribeConnection,
  sendMessage,
  isSocketConnected,
  getSocketStatus
} from '@/utils/socket'
import { client_id_key } from "@/utils/constants"
import { getLocalServerAddress } from "@/utils/electronAPI";


const local_base_url = ref('')

// 获取 Python 服务地址的函数
/**
 * 本地 WebSocket 组合式函数
 * 用于连接本地 WebSocket 服务器
 */
export function useLocalSocket() {
  const SOCKET_NAME = 'Local'

  const initLocalSocket = async () => {
    if (local_base_url.value === ''){
      let temp_url = await getLocalServerAddress()
      local_base_url.value = temp_url.substring(temp_url.indexOf('//') + 2)
    }
    const server = 'ws://' + local_base_url.value + '/local_server?clientId=' + client_id_key
    initSocket(server, SOCKET_NAME)
    console.log(`[LocalSocket] 正在连接: ${server}`)
  }

  const closeLocalSocket = () => {
    closeSocket(SOCKET_NAME)
  }

  // 存储所有订阅的取消函数
  const unsubscribers: Array<() => void> = []

  /**
   * 订阅消息事件
   */
  const subscribeLocalEvent = (
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
  const onLocalStatusChange = (
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
  const localSend = (type: string, content?: any) => {
    return sendMessage(SOCKET_NAME, type, content)
  }

  /**
   * 获取连接状态
   */
  const getLocalStatus = () => {
    return getSocketStatus(SOCKET_NAME)
  }
  // 组件卸载时自动取消所有订阅
  onUnmounted(() => {
    unsubscribers.forEach(fn => fn())
    unsubscribers.length = 0
  })

  return {
    localConnected: isSocketConnected(SOCKET_NAME),
    subscribeLocalEvent,
    onLocalStatusChange,
    localSend,
    closeLocalSocket,
    initLocalSocket,
    getLocalStatus
  }
}
