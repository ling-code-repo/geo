# Embedding API 服务

基于 BGE-M3 模型的文本embedding服务，提供REST API接口。

## 功能特性

- 🚀 基于 BGE-M3 多语言embedding模型
- 🎯 支持中英文及其他多语言文本
- 📊 RESTful API 接口
- 🔄 自动GPU加速（如果可用）
- 📝 完整的日志记录

## 安装依赖

```bash
pip install -r requirements.txt
```

## 启动服务

```bash
python main.py
```

服务将在 `http://127.0.0.1:8000` 启动。

## API 接口

### 1. 生成 Embeddings

**POST** `/api/embeddings`

请求体：
```json
{
  "texts": ["中国的首都是哪里？", "北京是中华人民共和国的首都。"],
  "max_length": 512
}
```

响应：
```json
{
  "embeddings": [[0.1, 0.2, 0.3, ...], [0.4, 0.5, 0.6, ...]],
  "dimension": 1024,
  "count": 2
}
```

### 2. 健康检查

**GET** `/api/health`

响应：
```json
{
  "status": "ok",
  "message": "Embedding service is running"
}
```

## 使用示例

### Python 测试

```bash
python test_api.py
```

### cURL 示例

```bash
curl -X POST "http://127.0.0.1:8000/api/embeddings" \
  -H "Content-Type: application/json" \
  -d '{
    "texts": ["你好，世界！", "Hello, world!"],
    "max_length": 512
  }'
```

### Python 代码示例

```python
import requests

url = "http://127.0.0.1:8000/api/embeddings"
data = {
    "texts": ["中国的首都是哪里？", "北京是中华人民共和国的首都。"],
    "max_length": 512
}

response = requests.post(url, json=data)
result = response.json()

print(f"生成 {result['count']} 个 embeddings")
print(f"维度: {result['dimension']}")
```

### JavaScript 示例

```javascript
const response = await fetch('http://127.0.0.1:8000/api/embeddings', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    texts: ['你好，世界！', 'Hello, world!'],
    max_length: 512
  })
});

const result = await response.json();
console.log(`生成 ${result.count} 个 embeddings`);
console.log(`维度: ${result.dimension}`);
```

## API 文档

启动服务后，访问以下地址查看完整API文档：

- Swagger UI: `http://127.0.0.1:8000/docs`
- ReDoc: `http://127.0.0.1:8000/redoc`

## 项目结构

```
py-embed/
├── main.py              # 主程序入口
├── embed_module.py      # Embedding模型封装
├── embed.py            # 原始embedding测试文件
├── test_api.py         # API测试脚本
├── requirements.txt    # 依赖列表
└── routers/
    ├── __init__.py
    ├── api.py         # API路由
    └── websocket.py   # WebSocket路由
```

## 技术栈

- **Web框架**: FastAPI + Uvicorn
- **模型**: BGE-M3 (Xorbits/bge-m3)
- **深度学习**: PyTorch + Transformers
- **日志**: Loguru

## 注意事项

1. 首次启动时需要加载模型，可能需要一些时间
2. 模型会自动检测并使用GPU（如果可用）
3. 默认最大文本长度为512，可通过参数调整
4. BGE-M3 支持8192长度的文本

## 性能优化建议

1. 批量处理文本时，尽量一次性发送多个文本
2. 根据实际需求调整 `max_length` 参数
3. 在生产环境中建议使用GPU加速

## License

MIT
