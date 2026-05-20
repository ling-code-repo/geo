# routers/api.py
from datetime import datetime

from fastapi import APIRouter
from loguru import logger

from chrome.authorization_task import authorization
from chrome.publish import publish_task
from models.model import AccountAuthorization, error1, ok1, ok3, PublishTask

# 创建路由器实例
router = APIRouter(
    prefix="/api",      # 所有路由都会自动加上 /api 前缀
    tags=["API"],       # OpenAPI 文档中的标签
    responses={404: {"description": "Not found"}}  # 通用响应
)


# ========== GET 请求示例 ==========

@router.get("/")
async def read_root():
    """根端点"""
    return {"message": "API 服务运行中", "timestamp": datetime.now()}

@router.post("/account/authorization")
async def account_authorization(item:AccountAuthorization):
    try:
        flag,msg = authorization(item)
        if flag:
           return ok3("執行授权！")
        else:
            return error1(msg)
    except Exception as e:
        logger.exception("something went wrong", e)
    return error1(f"{item.platform}授权失败！")


@router.post("/publish/execute")
async def publish_execute(item:PublishTask):
    try:
        flag,msg = publish_task(item)
        if flag:
           return ok3("开始执行！")
        else:
            return error1(msg)
    except Exception as e:
        logger.exception("publish_execute something went wrong", e)
    return error1(f"执行失败！")