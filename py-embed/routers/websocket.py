from fastapi import WebSocket, WebSocketDisconnect
from loguru import logger
import json


async def websocket_endpoint(websocket: WebSocket):
    """WebSocket端点"""
    await websocket.accept()
    logger.info("WebSocket连接已建立")

    try:
        while True:
            # 接收消息
            data = await websocket.receive_text()
            logger.info(f"收到消息: {data}")

            # 处理消息（这里可以添加你的业务逻辑）
            try:
                message = json.loads(data)

                # 示例：简单的回显
                response = {
                    "type": "echo",
                    "data": message
                }

                await websocket.send_json(response)
            except json.JSONDecodeError:
                await websocket.send_json({
                    "type": "error",
                    "message": "Invalid JSON format"
                })

    except WebSocketDisconnect:
        logger.info("WebSocket连接已断开")
    except Exception as e:
        logger.error(f"WebSocket错误: {str(e)}")
        await websocket.close()
