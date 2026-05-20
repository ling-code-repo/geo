import asyncio
import json

from loguru import logger
from starlette.websockets import WebSocket, WebSocketDisconnect
from websocket_manager import manager

async def send_ws_message(client_id: str, data: dict):
    try:
        await manager.sendMessage(client_id, data)
    except Exception as e:
        logger.error(f"WebSocket 发送失败: {e}")

async def websocket_endpoint(websocket: WebSocket):
    """WebSocket 端点"""

    client_id = websocket.query_params.get("clientId")

    try:
        if not client_id:
            import uuid
            client_id = str(uuid.uuid4())

        # 1. 连接到管理器
        await manager.connect(client_id, websocket)

        # 3. 处理消息循环
        while True:
            try:
                # 接收消息 - 增加超时时间到5分钟,避免频繁超时
                data = await asyncio.wait_for(
                    websocket.receive_text(),
                    timeout=300
                )

                # 处理接收到的消息
                data = data.strip()
                data = json.loads(data)
                type = data['type']
                content = json.loads(data['content'])
                logger.info(f"收到来自 {client_id} 的消息: type : {type}, content:{content}")
                    # 这里可以添加你的业务逻辑

            except asyncio.TimeoutError:
                # 超时后发送心跳保持连接
                try:
                    await websocket.send_text(json.dumps({
                        "type": "pong",
                        "content":{
                            "timestamp": int(asyncio.get_event_loop().time() * 1000)
                        }
                    }))
                except Exception as e:
                    logger.error(f"发送心跳失败: {e}")
                    break

            except WebSocketDisconnect:
                # 客户端主动断开,正常退出
                logger.info(f"客户端 {client_id} 正常断开")
                break

            except Exception as e:
                logger.exception(f"接收消息错误: {e}")
                break

    except WebSocketDisconnect:
        logger.info(f"客户端 {client_id} 在握手阶段断开")
    except Exception as e:
        logger.exception(f"WebSocket 连接错误: {e}")
    finally:
        # 清理连接 - 确保一定会执行
        try:
            await manager.disconnect(websocket)
            logger.info(f"客户端 {client_id} 连接已清理")
        except Exception as e:
            logger.error(f"清理连接时出错: {e}")