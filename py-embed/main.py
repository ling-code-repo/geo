import argparse
import asyncio
import logging
import os
import sys

need_out_put_log = True

# ========== 修复代码：必须放在最前面 ==========
# 检测打包环境
IS_FROZEN = getattr(sys, 'frozen', False) or hasattr(sys, '_MEIPASS')


def fix_standard_streams():
    """修复标准输出流"""
    if not IS_FROZEN:
        return

    # 方法1：确保 stdout 和 stderr 不是 None
    if sys.stdout is None:
        # 创建一个简单的流对象
        class DummyStream:
            def write(self, s):
                pass

            def flush(self):
                pass

            def isatty(self):
                return False

        sys.stdout = DummyStream()

    if sys.stderr is None:
        class DummyStream:
            def write(self, s):
                pass

            def flush(self):
                pass

            def isatty(self):
                return False

        sys.stderr = DummyStream()

    # 方法2：确保 isatty 方法存在
    for stream_name in ['stdout', 'stderr']:
        stream = getattr(sys, stream_name)
        if stream:
            # 如果流对象没有 isatty 方法，添加一个
            if not hasattr(stream, 'isatty'):
                stream.isatty = lambda: False
            else:
                # 保存原始方法
                original_isatty = stream.isatty

                # 包装它，防止返回 None
                def safe_isatty():
                    try:
                        result = original_isatty()
                        return bool(result) if result is not None else False
                    except:
                        return False

                stream.isatty = safe_isatty


# 立即调用修复函数
fix_standard_streams()

logging.basicConfig(level=logging.NOTSET)
from loguru import logger
class InterceptHandler(logging.Handler):
    """将标准 logging 日志转发到 loguru"""

    def emit(self, record):
        # 获取对应的 Loguru 级别
        try:
            level = logger.level(record.levelname).name
        except ValueError:
            level = record.levelno

        # 找到调用者
        frame, depth = logging.currentframe(), 2
        while frame.f_code.co_filename == logging.__file__:
            frame = frame.f_back
            depth += 1

        # 记录日志
        logger.opt(depth=depth, exception=record.exc_info).log(
            level, record.getMessage()
        )


def setup_logging_intercept():
    """设置日志拦截，将 logging 日志转发到 loguru"""

    # 移除所有现有的处理器
    logging.basicConfig(handlers=[], level=0, force=True)

    # 拦截 uvicorn 和 fastapi 日志
    for log_name in ["uvicorn", "uvicorn.error", "uvicorn.access", "fastapi"]:
        logging_logger = logging.getLogger(log_name)
        logging_logger.handlers = [InterceptHandler()]
        logging_logger.propagate = False

    # 也拦截根日志
    logging.getLogger().handlers = [InterceptHandler()]

setup_logging_intercept()


# ========== 5. 配置 loguru ==========
def setup_loguru_logging():
    """设置 loguru 日志"""
    # 移除默认处理器
    logger.remove()

    if not need_out_put_log:
        return

    # 控制台输出
    if IS_FROZEN:
        # 打包环境：简单格式，无颜色
        console_format = (
            "[{time:HH:mm:ss}] | "
            "{level: <8} | "
            "{name}:{line} | "  # 改为 name 而不是 file
            "{message}"
        )
        colorize = False
    else:
        # 开发环境：带颜色
        console_format = (
            "<light-blue>[</light-blue><yellow>{time:HH:mm:ss}</yellow><light-blue>]</light-blue> | "
            "<level>{level: <8}</level> | "
            "<cyan>{name}</cyan>:<cyan>{line}</cyan> | "
            "<level>{message}</level>"
        )
        colorize = True

    logger.add(
        sys.stdout,
        format=console_format,
        level="INFO",
        colorize=colorize,
        backtrace=True,
        diagnose=True
    )

    # 文件输出
    log_file = "logs/app.log"
    if IS_FROZEN:
        # 打包环境使用绝对路径
        if hasattr(sys, '_MEIPASS'):
            base_dir = os.path.dirname(sys.executable)
        else:
            base_dir = os.getcwd()
        log_file = os.path.join(base_dir, "logs", "app.log")
        os.makedirs(os.path.dirname(log_file), exist_ok=True)

    logger.add(
        log_file,
        rotation="10 MB",
        retention="1 month",
        compression="zip",
        format="{time:YYYY-MM-DD HH:mm:ss} | {level} | {name}:{line} - {message}",
        level="DEBUG",  # 改为 DEBUG 以捕获更多日志
        encoding="utf-8"
    )

    # logger.info("=" * 50)
    # logger.info("Loguru 日志系统初始化完成")
    # logger.info(f"打包环境: {IS_FROZEN}")
    # logger.info("=" * 50)


# 立即配置 loguru
setup_loguru_logging()

# ========== 7. 测试日志拦截是否工作 ==========
# logger.info("测试: 这条消息应该来自 loguru")

# 测试 logging 模块是否被拦截
# test_logger = logging.getLogger("test")
# test_logger.info("测试: 这条消息应该被 loguru 拦截并显示")

# ========== 8. 配置 uvicorn 日志 ==========
def get_uvicorn_log_config():
    """获取 uvicorn 日志配置"""
    return {
        "version": 1,
        "disable_existing_loggers": False,  # 改为 False，不要禁用现有日志器
        "formatters": {
            "default": {
                "format": "%(message)s",  # 简单格式，让拦截器处理
            },
            "access": {
                "format": "%(message)s",
            },
        },
        "handlers": {
            "default": {
                "formatter": "default",
                "class": "logging.StreamHandler",
                "stream": sys.stdout,
            },
            "access": {
                "formatter": "access",
                "class": "logging.StreamHandler",
                "stream": sys.stdout,
            },
        },
        "loggers": {
            # 关键：不要设置 uvicorn 日志器，让它们使用根日志器
            # 这样就会被我们的拦截处理器捕获
        },
    }

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from loguru import logger

from routers import api
from routers.websocket import websocket_endpoint


if sys.platform == 'win32':
    asyncio.set_event_loop_policy(asyncio.WindowsProactorEventLoopPolicy())

app = FastAPI(title="FastAPI",
    description="py-geo-server",
    version="1.0.0",
    # lifespan=lifespan,
    # docs_url="/docs",      # 启用 Swagger UI
    # redoc_url="/redoc",    # 启用 ReDoc
    # openapi_url="/openapi.json"
              )
# 添加 CORS 中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
main_loop = asyncio.new_event_loop()
# 注册路由
app.include_router(api.router)

# WebSocket 路由
app.websocket("/local_server")(websocket_endpoint)
#
#
# # 关键：必须用 @app.websocket 装饰器注册路由！
# @app.websocket("/ws")
# async def websocket_endpoint(websocket: WebSocket):
#     """WebSocket 端点"""
#     await handle_websocket(websocket)


# 添加一个测试页面


# pyarmor gen -O dist -r ./ --exclude ./.venv

# pyinstaller --paths=./dist --hidden-import fastapi --hidden-import uvicorn --hidden-import websockets --hidden-import playwright --hidden-import loguru --hidden-import psutil --hidden-import commonmark --hidden-import markdown-it-py ./main.py --noconsole --clean

def parse_arguments():
    """
    解析命令行参数
    支持从 Electron 传递端口参数
    """
    parser = argparse.ArgumentParser(
        description='FastAPI 后端服务',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 从 Electron 启动（端口由 Electron 动态分配）
  python main.py --port 8089 --host 127.0.0.1

  # 独立启动（使用默认或指定端口）
  python main.py
  python main.py --port 8080
        """
    )

    parser.add_argument(
        '--port', '-p',
        type=int,
        default=8089,
        help='服务端口号。如果不提供，默认使用 8089，如果被占用则自动寻找可用端口'
    )
    #
    # parser.add_argument(
    #     '--host',
    #     type=str,
    #     default='127.0.0.1',
    #     help='服务监听地址 (默认: 127.0.0.1)'
    # )
    #
    # parser.add_argument(
    #     '--reload',
    #     action='store_true',
    #     default=False,
    #     help='开启热重载（开发模式）'
    # )

    parser.add_argument(
        '--workers',
        type=int,
        default=1,
        help='工作进程数（默认: 1）'
    )
    #
    # parser.add_argument(
    #     '--log-level',
    #     type=str,
    #     default='info',
    #     choices=['debug', 'info', 'warning', 'error', 'critical'],
    #     help='日志级别'
    # )
    #
    # parser.add_argument(
    #     '--electron-mode',
    #     action='store_true',
    #     default=False,
    #     help='Electron 模式，输出特定格式的日志'
    # )

    return parser.parse_args()


def setup_loguru_logging():
    """设置 loguru 日志"""
    # 移除默认处理器
    logger.remove()

    # 控制台输出
    if IS_FROZEN:
        # 打包环境：简单格式，无颜色
        console_format = (
            "[{time:HH:mm:ss}] | "
            "{level: <8} | "
            "{file}:{line} | "
            "{message}"
        )
        colorize = False
    else:
        # 开发环境：带颜色
        console_format = (
            "<light-blue>[</light-blue><yellow>{time:HH:mm:ss}</yellow><light-blue>]</light-blue> | "
            "<level>{level: <8}</level> | "
            "<cyan>{file}:{line}</cyan> | "
            "<level>{message}</level>"
        )
        colorize = True

    logger.add(
        # sys.stdout,
        format=console_format,
        level="INFO",
        colorize=colorize,
        backtrace=True,
        diagnose=True
    )

    # 文件输出
    log_file = "logs/app.log"
    if IS_FROZEN:
        # 打包环境使用绝对路径
        if hasattr(sys, '_MEIPASS'):
            base_dir = os.path.dirname(sys.executable)
        else:
            base_dir = os.getcwd()
        log_file = os.path.join(base_dir, "logs", "app.log")
        os.makedirs(os.path.dirname(log_file), exist_ok=True)

    logger.add(
        log_file,
        rotation="10 MB",
        retention="1 month",
        compression="zip",
        format="{time:YYYY-MM-DD HH:mm:ss} | {level} | {name}:{line} - {message}",
        level="INFO",
        encoding="utf-8"
    )

    logger.info(f"日志文件: {log_file}")

if __name__ == "__main__":

    current_file = os.path.splitext(os.path.basename(__file__))[0]

    args = parse_arguments()

    uvicorn.run(
        app,  # 从本文件导入 app
        host="127.0.0.1",
        port=args.port,
        loop="asyncio",
        workers=args.workers,
        reload=False,  # 开发时开启热重载
        log_level="info",
        log_config=get_uvicorn_log_config(),  # 使用自定义配置
        # access_log=True  # 访问日志也会被拦截
        access_log=need_out_put_log  # 访问日志也会被拦截
    )