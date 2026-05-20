import { reactive } from 'vue'
import type {
  WebSocketConfig,
  ConnectionState,
  ConnectionStatus,
  MessageHandler,
  ConnectionHandler
} from "@/types/socket";

export class WebSocketManager {
  private ws: WebSocket | null = null
  private wsUrl: string = ''
  private reconnectDelay: number
  private reconnectAttempts: number = 0
  private pingInterval: number | null = null
  private messageHandlers = new Map<string, Set<MessageHandler>>()
  private connectionHandlers = new Set<ConnectionHandler>()
  private autoReconnect = true
  private debug: boolean
  private config: WebSocketConfig

  public readonly state = reactive<ConnectionState>({
    connected: false,
    connecting: false,
    error: null,
    lastPing: null
  })

  constructor(config: WebSocketConfig) {
    this.config = config
    this.reconnectDelay = config.reconnectDelay || 3000
    this.debug = config.debug || process.env.NODE_ENV === 'development'

    if (config.autoConnect !== false) {
      this.wsUrl = config.url
      this.connect(this.wsUrl)
    }
  }

  async connect(url: string): Promise<void> {
    if (this.state.connected) {
      this.debugLog('WebSocket 已连接')
      return
    }

    if (this.state.connecting) {
      this.debugLog('WebSocket 正在连接中...')
      return new Promise((resolve) => {
        const check = () => {
          if (this.state.connected) {
            resolve()
          } else if (!this.state.connecting) {
            resolve()
          } else {
            setTimeout(check, 100)
          }
        }
        check()
      })
    }

    if (url) {
      this.wsUrl = url
    }

    this.state.connecting = true
    this.state.error = null

    return new Promise((resolve, reject) => {
      try {
        this.debugLog(`正在连接到 WebSocket: ${this.wsUrl}`)

        this.ws = new WebSocket(this.wsUrl)

        this.ws.onopen = () => this.handleOpen(resolve)
        this.ws.onmessage = (event) => this.handleMessage(event)
        this.ws.onerror = (error) => this.handleError(error, reject)
        this.ws.onclose = (event) => this.handleClose(event)

        setTimeout(() => {
          if (!this.state.connected && this.state.connecting) {
            this.state.connecting = false
            const error = new Error('连接超时')
            this.state.error = error
            this.ws?.close()
            reject(error)
          }
        }, 10000)

      } catch (error) {
        this.state.connecting = false
        this.state.error = error as Error
        this.notifyConnectionChange('error', error)
        reject(error)
      }
    })
  }

  send(type,content): boolean {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.warn(this.config.url + 'WebSocket 未连接，消息发送失败:', type,content)
      return false
    }

    try {

      const data = JSON.stringify({
        type,
        content:JSON.stringify(content)
      })
      this.ws.send(data)
      return true
    } catch (error) {
      console.error('消息发送失败:', error)
      return false
    }
  }

  subscribe(eventType: string, handler: MessageHandler): () => void {
    if (!this.messageHandlers.has(eventType)) {
      this.messageHandlers.set(eventType, new Set())
    }

    this.messageHandlers.get(eventType)!.add(handler)

    return () => {
      const handlers = this.messageHandlers.get(eventType)
      if (handlers) {
        handlers.delete(handler)
      }
    }
  }

  onConnectionChange(handler: ConnectionHandler): () => void {
    this.connectionHandlers.add(handler)

    return () => {
      this.connectionHandlers.delete(handler)
    }
  }

  close(): void {
    this.autoReconnect = false

    if (this.ws) {
      this.ws.close(1000, '正常关闭')
      this.ws = null
    }

    this.stopHeartbeat()
    this.state.connected = false
    this.state.connecting = false

    this.notifyConnectionChange('closed')
    this.clearAllHandlers()
  }

  // Private methods
  private handleOpen(resolve: () => void): void {
    this.debugLog('WebSocket 连接成功')

    this.state.connected = true
    this.state.connecting = false
    this.reconnectAttempts = 0

    this.notifyConnectionChange('connected')
    this.startHeartbeat()

    resolve()
  }

  private handleMessage(messageEvent: MessageEvent): void {
    try {
      // console.log(`[${this.config.name}Socket] ${messageEvent.data}`)
      let data = JSON.parse(messageEvent.data)
      const { type } = data

      this.debugLog('收到消息:', data)
      if (data === 'pong' || type === 'pong') {
        this.state.lastPing = Date.now()
        console.log(`[${this.config.name}Socket] 心跳响应`)
        return
      }
      if (typeof data.content === 'string') {
        data = JSON.parse(data.content)
      }else if (typeof data.content === 'object') {
        data = data.content
      }else {
        data = data.content
      }


      if (type) {
        this.dispatchMessage(type, data)
      }

    } catch (error) {
      console.error('消息解析失败:', error, messageEvent.data)
    }
  }

  private handleError(error: Event, reject?: (reason?: any) => void): void {
    console.error('WebSocket 错误:', error)

    const err = new Error(this.config.name + 'WebSocket 连接错误')
    this.state.error = err
    this.state.connecting = false

    this.notifyConnectionChange('error', error)

    if (reject) {
      reject(error)
    }
  }

  private handleClose(event: CloseEvent): void {
    this.debugLog('WebSocket 连接关闭:', event.code, event.reason)

    this.state.connected = false
    this.state.connecting = false

    this.stopHeartbeat()
    this.notifyConnectionChange('disconnected', event)

    if (this.autoReconnect) {
      this.scheduleReconnect()
    }
  }

  private dispatchMessage(eventType: string, data: any): void {
    const handlers = this.messageHandlers.get(eventType)
    if (handlers) {
      handlers.forEach(handler => {
        try {
          handler(data)
        } catch (error) {
          console.error(this.config.name + `消息处理器错误 (${eventType}):`, error)
        }
      })
    }
  }

  private notifyConnectionChange(status: ConnectionStatus, data?: any): void {
    this.connectionHandlers.forEach(handler => {
      try {
        handler(status, data)
      } catch (error) {
        console.error(this.config.name + '连接状态处理器错误:', error)
      }
    })
  }

  private startHeartbeat(): void {
    this.stopHeartbeat()

    this.pingInterval = window.setInterval(() => {
      if (this.state.connected && this.ws?.readyState === WebSocket.OPEN) {
        this.send('geo-ping',{
          timestamp: Date.now()
        })
      }
    }, 30000)
  }

  private stopHeartbeat(): void {
    if (this.pingInterval) {
      clearInterval(this.pingInterval)
      this.pingInterval = null
    }
  }

  private scheduleReconnect(): void {
    const delay = this.reconnectDelay

    this.reconnectAttempts++

    this.debugLog(`将在 ${delay}ms 后尝试重连 (第 ${this.reconnectAttempts} 次)`)

    setTimeout(() => {
      if (!this.state.connected && this.autoReconnect) {
        this.connect(this.wsUrl).catch((error) => {
          console.error(this.config.name + ' socket重连失败:', error)
          this.scheduleReconnect()
        })
      }
    }, delay)
  }

  private clearAllHandlers(): void {
    this.messageHandlers.clear()
    this.connectionHandlers.clear()
  }

  private debugLog(...args: any[]): void {
    if (this.debug) {
      console.log('[' + this.config.name + 'WebSocket]', ...args)
    }
  }
}

// 多实例管理
const socketManagers = new Map<string, WebSocketManager>()


/**
 * 获取指定名称的 WebSocketManager
 */
export function getSocketManager(name: string): WebSocketManager | undefined {
  return socketManagers.get(name)
}

/**
 * 初始化指定名称的 Socket
 */
export function initSocket(url: string, name: string, config?: Partial<WebSocketConfig>): WebSocketManager {
  const existing = socketManagers.get(name)
  if (existing) {
    return existing
  }

  console.log("process.env.NODE_ENV",process.env.NODE_ENV);

  const wsConfig: WebSocketConfig = {
    url,
    name,
    autoConnect: true,
    reconnectDelay: 3000,
    debug: process.env.NODE_ENV === 'development',
    ...config
  }

  const manager = new WebSocketManager(wsConfig)
  socketManagers.set(name, manager)
  return manager
}

/**
 * 关闭指定名称的 Socket
 */
export function closeSocket(name: string): void {
  const manager = socketManagers.get(name)
  if (manager) {
    manager.close()
    socketManagers.delete(name)
  }
}

/**
 * 发送消息到指定 Socket
 */
export function sendMessage(name: string, type: string, content?: any): boolean {
  const manager = socketManagers.get(name)
  if (!manager) {
    console.warn(`[Socket] "${name}" 不存在`)
    return false
  }
  return manager.send( type, content)
}

/**
 * 订阅指定 Socket 的消息事件
 */
export function subscribeEvent(name: string, eventType: string, handler: MessageHandler): () => void {
  const manager = socketManagers.get(name)
  if (!manager) {
    console.warn(`[Socket] "${name}" 不存在`)
    return () => {}
  }
  return manager.subscribe(eventType, handler)
}

/**
 * 订阅指定 Socket 的连接状态变化
 */
export function subscribeConnection(
  name: string,
  handler: (status: 'connected' | 'disconnected' | 'error') => void
): () => void {
  const manager = socketManagers.get(name)
  if (!manager) {
    console.warn(`[Socket] "${name}" 不存在`)
    return () => {}
  }
  return manager.onConnectionChange((status) => {
    if (status === 'connected' || status === 'disconnected' || status === 'error') {
      handler(status)
    }
  })
}

/**
 * 检查指定 Socket 是否已连接
 */
export function isSocketConnected(name: string): boolean {
  const manager = socketManagers.get(name)
  return manager?.state.connected || false
}

/**
 * 获取指定 Socket 的连接状态
 */
export function getSocketStatus(name: string): ConnectionState {
  const manager = socketManagers.get(name)
  return manager ? manager.state : {
    connected: false,
    connecting: false,
    error: null,
    lastPing: null
  }
}

/**
 * 获取所有 Socket 名称
 */
export function getAllSocketNames(): string[] {
  return Array.from(socketManagers.keys())
}

/**
 * 关闭所有 Socket
 */
export function closeAllSockets(): void {
  socketManagers.forEach((manager, name) => {
    manager.close()
  })
  socketManagers.clear()
}
