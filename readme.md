# GEO 系统用户指南

## 📖 系统简介

**GEO 系统**是一个专业的生成式引擎优化（Generative Engine Optimization，GEO）平台，旨在帮助企业和内容创作者通过 AI 创作优质内容，并在各大 AI 引擎（如 ChatGPT、Claude、Perplexity 等）中获得更好的内容引用和排名效果。

### 什么是 GEO？

**GEO（生成式引擎优化）**类似于传统 SEO，但针对的是 AI 引擎而非传统搜索引擎。当用户向 AI 提出问题时，AI 会综合各种信息源生成答案。GEO 的目标是让 AI 模型在回答相关问题时更倾向于引用你的内容。

### 系统架构

GEO 系统采用现代化的分布式架构，由四个核心组件构成：

```
┌─────────────────────────────────────────────────────────────────┐
│                         GEO 系统架构                         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────┐    HTTP/WebSocket    ┌─────────────────┐
│   桌面客户端    │◄────────────────────►│   Java后端服务  │
│   (Electron)    │                      │   (Spring Boot) │
│                 │                      │   端口: 48080     │
│ - 用户界面      │                      │                 │
│ - 数据展示      │                      │ - 业务逻辑      │
│ - 操作控制      │                      │ - 用户管理      │
│ - 实时监控      │                      │ - 权限控制      │
└─────────────────┘                      │ - 数据库操作    │
                                         │ - 任务调度      │
                                         └────────┬────────┘
                                                  │
           ┌──────────────────────────────────────┼──────────────────────────────┐
           │                                      │                              │
           │                              HTTP REST API                    HTTP REST API
           │                                      │                              │
           ▼                                      ▼                              ▼
┌─────────────────────┐              ┌─────────────────────┐      ┌─────────────────────┐
│   Python GEO服务    │              │   Python 嵌入服务   │      │   数据库+缓存       │
│   (FastAPI)         │              │   (FastAPI)         │      │                     │
│   端口: 8000        │              │   端口: 8089        │      │ MySQL: 3306         │
│                     │              │                     │      │ Redis: 6379         │
│ - 平台内容发布      │              │ - 文本向量化       │      │                     │
│ - 浏览器自动化      │              │ - AI嵌入处理       │      │ - 数据存储          │
│ - 账号授权管理      │              │ - 相似度计算       │      │ - 缓存管理          │
│ - 定时任务执行      │              │ - 智能推荐         │      │ - 会话管理          │
└─────────────────────┘              └─────────────────────┘      └─────────────────────┘

═══════════════════════════════════════════════════════════════════════════════
                              系统间交互关系
═══════════════════════════════════════════════════════════════════════════════

1. 用户操作流程:
   桌面客户端 → Java后端 → 数据库 (存储/查询数据)
                  ↓
   桌面客户端 ← Java后端 ← 返回结果

2. 内容发布流程:
   桌面客户端 → Java后端 → Python GEO服务 → 外部平台
                  ↓                    ↓
   更新发布状态 ← 数据库     ← 返回发布结果

3. AI内容处理流程:
   桌面客户端 → Java后端 → Python嵌入服务 → AI模型处理
                  ↓                    ↓
   返回处理结果 ← 数据库     ← 返回嵌入向量
```

#### 各组件详细说明

**1. 桌面客户端 (geo-electron/)**
- **技术栈**: Electron + Vue 3 + TypeScript + Element Plus
- **作用**: 用户界面层，提供友好的操作界面
- **依赖**: 依赖Java后端服务提供数据和业务逻辑
- **配置**: 需配置Java后端服务的地址和端口

**2. Java后端服务 (geo-server/)**
- **技术栈**: Spring Boot 2.7.18 + Java 17 + MyBatis Plus
- **作用**: 核心业务逻辑层，处理用户请求和数据管理
- **依赖**: 依赖MySQL、Redis，调用Python服务处理特定任务
- **端口**: 默认48080

**3. Python GEO服务 (py-geo-server/)**
- **技术栈**: FastAPI + Python 3.11 + Playwright
- **作用**: 处理平台发布、浏览器自动化、定时任务等
- **依赖**: 依赖Java后端进行任务调度和数据存储
- **端口**: 默认8000

**4. Python嵌入服务 (py-embed/)**
- **技术栈**: FastAPI + PyTorch + Transformers
- **作用**: 提供文本向量化、相似度计算等AI能力
- **依赖**: 依赖BGE-M3模型，需要GPU支持（可选）
- **端口**: 默认8089

**5. 基础设施**
- **MySQL**: 持久化数据存储，端口3306
- **Redis**: 缓存和会话管理，端口6379

## 🚀 快速开始

### 系统启动顺序

**重要**: 必须按照以下顺序启动各组件，否则会出现连接失败：

```
启动顺序:
1. MySQL数据库     → 基础数据存储
2. Redis缓存       → 会话和缓存管理
3. Java后端服务    → 核心业务逻辑
4. Python GEO服务  → 平台发布功能
5. Python嵌入服务  → AI文本处理
6. 桌面客户端      → 用户界面 (最后启动)
```

### 详细安装和启动步骤

#### 第一步 初始化

```bash
执行geo-server/sql/geo.sql
启动redis


```

#### 第二步：启动Java后端服务

```bash
# 1. 进入Java后端目录
cd geo-server

# 2. 配置数据库连接
# 编辑 qianyu-server/src/main/resources/application.yaml
# 修改数据库连接信息：
# spring.datasource.dynamic.datasource.master.url
# spring.datasource.dynamic.datasource.master.username
# spring.datasource.dynamic.datasource.master.password
修改数据库/redis/模型配置
ai:
  llm:
    # 豆包API配置（主要使用）
    doubao-api-key: 豆包key
  embedding:
    url:  http://localhost:8089/api/embeddings

# 3. 启动服务
```

#### 第三步：启动Python GEO服务

```bash
# 1. 进入Python GEO服务目录
cd py-geo-server

# 2. 创建Python虚拟环境
python -m venv .venv

# 3. 激活虚拟环境
# Windows:
.venv\Scripts\activate
# Linux/macOS:
source .venv/bin/activate

# 4. 安装依赖
pip install -r requirements.txt

# 5. 安装Playwright浏览器 (首次运行)
playwright install chromium

# 6. 启动服务
python main.py
```

#### 第四步：启动Python嵌入服务

```bash
# 1. 进入Python嵌入服务目录
cd py-embed

# 2. 创建Python虚拟环境
python -m venv .venv

# 3. 激活虚拟环境
# Windows:
.venv\Scripts\activate
# Linux/macOS:
source .venv/bin/activate

# 4. 安装依赖
pip install -r requirements.txt

# 5. 下载BGE-M3模型 (首次运行)
# ✅ 使用 modelscope 下载模型
# from modelscope import snapshot_download
# model_dir = snapshot_download("Xorbits/bge-m3")
#安装cuda包,下载到package目录中。
#https://download.pytorch.org/whl/cu121/torchaudio-2.5.1%2Bcu121-cp311-cp311-win_amd64.whl
#https://download.pytorch.org/whl/cu121/torchvision-0.20.1%2Bcu121-cp311-cp311-win_amd64.whl
#https://download.pytorch.org/whl/cu121/torch-2.5.1%2Bcu121-cp311-cp311-win_amd64.whl
#pip install package/torch-2.5.1+cu121-cp311-cp311-win_amd64.whl
#pip install package/torchaudio-2.5.1+cu121-cp311-cp311-win_amd64.whl
#pip install package/torchvision-0.20.1+cu121-cp311-cp311-win_amd64.whl
# 6. 启动服务
python main.py
```

#### 第五步：启动桌面客户端

**方式一：开发模式启动**

```bash
# 1. 进入桌面客户端目录
cd geo-electron

# 2. 安装依赖
yarn install


# 3. 启动开发服务器
yarn dev


**Linux/macOS 启动脚本 (start.sh)**
```

## 📚 核心功能

### 1. 蒸馏词管理

**蒸馏词**是 GEO 策略的核心概念，指您希望 AI 引擎在回答相关问题时使用的关键词或短语。

#### 功能特性
- ✅ **关键词管理**: 创建、编辑、删除蒸馏词
- ✅ **智能分类**: 支持品牌词、产品词、问题词、长尾词、竞品词等分类
- ✅ **问题关联**: 将关键词与用户常见问题关联，指导内容创作

#### 使用指南

1. **创建蒸馏词**
   - 导航至"蒸馏词管理"页面
   - 点击"新建蒸馏词"按钮
   - 填写关键词信息（名称、分类、目标等）
   - 保存后系统将自动生成优化建议

2. **监控效果**
   - 在蒸馏词列表中查看实时数据
   - 点击"详细分析"查看趋势图表
   - 根据数据调整优化策略

### 2. AI 内容创作

基于顶级 AI 模型自动创作优化的内容，确保内容质量和 GEO 效果。

#### 创作模式
1. **GEO 优化模式**: 针对特定蒸馏词优化的内容创作
2. **批量创作模式**: 一次性生成多篇相关内容

#### 使用步骤
1. 选择创作模式和目标 AI 模型
2. 输入创作指令或选择预设模板
3. 关联相关的蒸馏词
4. 启动创作任务
5. 等待生成完成后进行人工审核和编辑

### 3. 文章管理

对 AI 创作的内容进行全生命周期管理。

#### 功能特性
- 📝 **内容编辑**: 内置富文本编辑器，支持格式化、图片插入等
- 🔄 **版本管理**: 自动保存历史版本，支持版本对比和回滚
- 🔗 **关联管理**: 管理文章与蒸馏词、发布平台的关联关系

### 4. 多平台发布

将内容一键发布到各大平台，扩大内容影响力。

#### 支持平台
- 📱 **社交媒体**: 微信公众号、微博、知乎、头条号
- 🏢 **企业平台**: 百度百家号、大鱼号、企鹅号
- 🌐 **其他平台**: 持续扩展中...

#### 发布功能
- ✅ **账号管理**: 统一管理各平台授权账号
- ⏰ **定时发布**: 设置发布时间，自动执行发布任务
- 📈 **结果追踪**: 实时监控发布状态和效果数据
- 🔄 **批量操作**: 一次性向多个平台发布内容

## 🛠️ 企业素材管理

管理企业相关的素材资源，为 AI 创作提供更丰富的背景信息。

- 📁 **素材分类**: 按产品、服务、行业等维度分类管理
- 📊 **素材效果**: 分析各类素材的使用效果

### 创作指令管理

管理 AI 创作使用的提示词模板，提高创作效率和质量。

- 💡 **模板库**: 预设多种场景的创作模板
- 🔄 **版本对比**: 对比不同版本指令的优劣

## 📊 使用场景

### 场景 1: 品牌曝光优化

**目标**: 提高品牌在 AI 回答中的曝光率

**步骤**:
1. 创建品牌相关的蒸馏词（如"GEO 系统"）
2. 使用 AI 创作包含品牌关键词的优质内容
3. 将内容发布到高权威度平台

### 场景 2: 产品推广

**目标**: 在用户咨询相关产品问题时获得 AI 推荐

**步骤**:
1. 分析目标用户可能提出的问题
2. 创建产品相关的蒸馏词和问题词
3. 针对问题创作专业解答内容
4. 多平台发布建立权威性