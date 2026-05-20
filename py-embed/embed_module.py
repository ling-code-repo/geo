import torch
import torch.nn.functional as F
from transformers import AutoTokenizer, AutoModel
from typing import List
import numpy as np

# 全局变量存储模型和tokenizer
_model = None
_tokenizer = None
_device = None


def init_model(model_dir: str = r"D:\modelscope\hub\models\Xorbits\bge-m3"):
    """初始化模型（懒加载，首次调用时才加载）"""
    global _model, _tokenizer, _device

    if _model is None:
        _device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"Loading model on {_device}...")

        _tokenizer = AutoTokenizer.from_pretrained(model_dir)
        _model = AutoModel.from_pretrained(
            model_dir,
            trust_remote_code=True,
        )

        # 使用GPU可用则使用half精度
        if _device == "cuda":
            _model = _model.half().cuda()

        _model.eval()
        print("Model loaded successfully")

    return _model, _tokenizer, _device


def get_embeddings(texts: List[str], max_length: int = 65535) -> np.ndarray:
    """
    获取文本的embedding向量

    Args:
        texts: 文本列表
        max_length: 最大文本长度

    Returns:
        numpy array of shape (len(texts), embedding_dim)
    """
    model, tokenizer, device = init_model()

    batch_dict = tokenizer(
        texts,
        # max_length=max_length,
        padding=True,
        truncation=True,
        return_tensors="pt"
    ).to(device)

    with torch.no_grad():
        outputs = model(**batch_dict)

    embeddings = outputs.last_hidden_state[:, 0, :]
    embeddings = F.normalize(embeddings, p=2, dim=1)

    # 转换为numpy数组返回
    return embeddings.cpu().numpy()


def get_embeddings_tensor(texts: List[str], max_length: int = 512) -> torch.Tensor:
    """
    获取文本的embedding向量（返回PyTorch tensor）

    Args:
        texts: 文本列表
        max_length: 最大文本长度

    Returns:
        torch.Tensor of shape (len(texts), embedding_dim)
    """
    model, tokenizer, device = init_model()

    batch_dict = tokenizer(
        texts,
        # max_length=max_length,
        padding=True,
        truncation=True,
        return_tensors="pt"
    ).to(device)

    with torch.no_grad():
        outputs = model(**batch_dict)

    embeddings = outputs.last_hidden_state[:, 0, :]
    return F.normalize(embeddings, p=2, dim=1)
