from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import List, Optional
import numpy as np
from loguru import logger

from embed_module import get_embeddings

router = APIRouter(prefix="/api", tags=["embeddings"])


class EmbeddingRequest(BaseModel):
    """Embedding请求模型"""
    texts: List[str] = Field(..., description="要编码的文本列表", min_items=1)
    # max_length: int = Field(512, description="最大文本长度", ge=1, le=8192)

    class Config:
        json_schema_extra = {
            "example": {
                "texts": ["中国的首都是哪里？", "北京是中华人民共和国的首都。"],
                # "max_length": 512
            }
        }


class EmbeddingResponse(BaseModel):
    """Embedding响应模型"""
    embeddings: List[List[float]] = Field(..., description="embedding向量列表")
    dimension: int = Field(..., description="embedding维度")
    count: int = Field(..., description="文本数量")

    class Config:
        json_schema_extra = {
            "example": {
                "embeddings": [[0.1, 0.2, 0.3, ...], [0.4, 0.5, 0.6, ...]],
                "dimension": 1024,
                "count": 2
            }
        }


@router.post("/embeddings", response_model=EmbeddingResponse)
async def create_embeddings(request: EmbeddingRequest):
    """
    创建文本embeddings

    - **texts**: 要编码的文本列表
    - **max_length**: 最大文本长度（默认512）

    返回每个文本对应的embedding向量
    """
    try:
        logger.info(f"收到embedding请求，文本数量: {len(request.texts)}")

        # 获取embeddings
        embeddings = get_embeddings(request.texts)

        # 转换为列表格式
        embeddings_list = embeddings.tolist()

        response = EmbeddingResponse(
            embeddings=embeddings_list,
            dimension=embeddings.shape[1],
            count=len(embeddings_list)
        )

        logger.info(f"成功生成{response.count}个embedding，维度: {response.dimension}")
        return response

    except Exception as e:
        logger.error(f"生成embedding时出错: {str(e)}")
        raise HTTPException(status_code=500, detail=f"生成embedding失败: {str(e)}")


@router.get("/health")
async def health_check():
    """健康检查接口"""
    return {"status": "ok", "message": "Embedding service is running"}


@router.get("/")
async def root():
    """根路径"""
    return {
        "message": "Embedding API",
        "version": "1.0.0",
        "endpoints": {
            "embeddings": "/api/embeddings",
            "health": "/api/health"
        }
    }
