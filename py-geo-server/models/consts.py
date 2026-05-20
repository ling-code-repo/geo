import os
import socket

from loguru import logger

from path import PATH

geo_chrome_session_dir = "geo_chrome_session"

def get_free_port() -> int:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind(("", 0))        # 0 = 让 OS 自动分配端口
        return s.getsockname()[1]


STEALTH_SCRIPT = ""
script_path = PATH.root_dir_path + os.sep + 'stealth.min.js'
if os.path.exists(script_path):
    try:
        with open(script_path, 'r', encoding='utf-8') as f:
            STEALTH_SCRIPT = f.read()
        logger.info("stealth.min.js 已加载")
    except Exception as e:
        logger.warning(f"加载 stealth.min.js 失败: {e}")
else:
    logger.warning("stealth.min.js 文件不存在")