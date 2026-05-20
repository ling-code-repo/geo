


import os
import subprocess
import sys
import time
from typing import Optional

import psutil


def kill_chrome_processes():
    """关闭所有 Chrome 进程"""
    for proc in psutil.process_iter(['pid', 'name']):
        try:
            process_name = proc.info['name'].lower()
            if 'chrome' in process_name:
                proc.terminate()
        except (psutil.NoSuchProcess, psutil.AccessDenied):
            pass
    time.sleep(1)

def _find_chrome() -> Optional[str]:
    """查找 Chrome 可执行文件"""
    possible_paths = []

    if sys.platform.startswith("win"):
        possible_paths = [
            r"C:\Program Files\Google\Chrome\Application\chrome.exe",
            r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
            os.path.expandvars(r"%LOCALAPPDATA%\Google\Chrome\Application\chrome.exe"),
            r"C:\Program Files\Google\Chrome Beta\Application\chrome.exe",
            r"C:\Program Files\Google\Chrome Dev\Application\chrome.exe"
        ]

    for path in possible_paths:
        if os.path.exists(path):
            return path

    return None

def start_chrome_with_debug(user_data_dir,profile,port:int,headless=False):
    kill_chrome_processes()
    """启动带调试模式的 Chrome"""
    chrome_path = _find_chrome()

    if not chrome_path:
        raise Exception("未找到 Chrome 浏览器")

    # 创建用户数据目录
    os.makedirs(user_data_dir, exist_ok=True)

    # 构建启动命令
    cmd = [
        chrome_path,
        f"--remote-debugging-port={port}",
        f"--user-data-dir={user_data_dir}",
        f"--profile-directory={profile}",  # 只保留一个
        "--no-first-run",  # 跳过首次运行向导
        "--start-maximized",
        "--no-default-browser-check",  # 不检查默认浏览器
    ]
    if headless:
        cmd.append("--headless=new")


    # 启动进程
    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )


    # 等待 Chrome 启动
    time.sleep(1)
    if process.poll() is not None:
        raise Exception(f"浏览器启动失败,退出码: {process.returncode}")

    return process