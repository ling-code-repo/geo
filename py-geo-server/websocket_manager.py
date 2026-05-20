import asyncio
import json
from typing import Dict, Any

from loguru import logger
from starlette.websockets import WebSocket


class ConnectionManager:
    def __init__(self):
        self.active_connection_map: Dict[str, WebSocket] = {}
        self.active_socket_connection_map: Dict[WebSocket, str] = {}
        self.lock = asyncio.Lock()

    async def connect(self, client_id: str, websocket: WebSocket):
        """接受 WebSocket 连接"""
        try:
            # 关键：必须先调用 accept()
            await websocket.accept()
            await self.put(client_id, websocket)
            logger.info(f"新客户端 {client_id} 连接成功，当前连接数: {len(self.active_connection_map)}")
        except Exception as e:
            logger.info(f"连接接受失败: {e}")
            raise

    async def disconnect(self, websocket: WebSocket):
        """断开连接 - 增加异常处理"""
        async with self.lock:
            try:
                # 先检查websocket是否在映射中
                if websocket in self.active_socket_connection_map:
                    client_id = self.active_socket_connection_map[websocket]
                    del self.active_socket_connection_map[websocket]

                    # 再删除client_id映射
                    if client_id in self.active_connection_map:
                        del self.active_connection_map[client_id]

                    logger.info(f"客户端 {client_id} 断开，剩余连接数: {len(self.active_connection_map)}")
                else:
                    logger.info("尝试断开不存在的连接")
            except KeyError as e:
                logger.info(f"断开连接时出现KeyError: {e}")
            except Exception as e:
                logger.info(f"断开连接时出错: {e}")

    async def remove_by_client_id(self, client_id: str):
        """根据client_id移除连接"""
        async with self.lock:
            try:
                if client_id and client_id in self.active_connection_map:
                    websocket = self.active_connection_map[client_id]
                    del self.active_connection_map[client_id]
                    if websocket in self.active_socket_connection_map:
                        del self.active_socket_connection_map[websocket]
            except Exception as e:
                logger.info(f"移除连接时出错: {e}")

    async def get_by_client_id(self, client_id: str) -> WebSocket:
        """根据client_id获取连接"""
        async with self.lock:
            return self.active_connection_map.get(client_id)

    async def put(self, client_id: str, websocket: WebSocket):
        """添加新连接"""
        async with self.lock:
            self.active_connection_map[client_id] = websocket
            self.active_socket_connection_map[websocket] = client_id

    async def sendMessage(self, client_id: str, data: Dict[str, Any]):
        """发送消息给指定客户端"""
        try:
            if client_id in self.active_connection_map:
                websocket = self.active_connection_map[client_id]
                await websocket.send_text(json.dumps(data))
                return True
            return False
        except Exception as e:
            logger.info(f"发送消息失败: {e}")
            # 发送失败时尝试清理连接
            await self.remove_by_client_id(client_id)
            return False

    async def broadcast(self, message: str, exclude: WebSocket = None):
        """广播消息给所有客户端"""
        if not self.active_connection_map:
            return

        # 复制连接列表避免迭代时修改
        async with self.lock:
            connections = list(self.active_connection_map.items())

        failed_clients = []
        for client_id, connection in connections:
            if connection != exclude:
                try:
                    await connection.send_text(message)
                except Exception as e:
                    logger.info(f"向客户端 {client_id} 广播失败: {e}")
                    failed_clients.append(client_id)

        # 清理失败的连接
        for client_id in failed_clients:
            await self.remove_by_client_id(client_id)


# 创建管理器实例
manager = ConnectionManager()