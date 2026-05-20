// WebSocket 相关类型定义
export interface WebSocketConfig {
  url: string
  autoConnect?: boolean
  reconnectDelay?: number
  debug?: boolean
  name?: string
}

export interface ConnectionState {
  connected: boolean
  connecting: boolean
  error: Error | null
  lastPing: number | null
}

export type ConnectionStatus =
  | 'connected'
  | 'connecting'
  | 'disconnected'
  | 'error'
  | 'closed'




export type MessageHandler = (data) => void
export type ConnectionHandler = (status: ConnectionStatus, data?: any) => void
