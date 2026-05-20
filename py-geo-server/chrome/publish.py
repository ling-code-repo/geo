import asyncio
import os
from datetime import datetime

from loguru import logger
from playwright.async_api import async_playwright, Browser, Page

from chrome.chrome import start_chrome_with_debug, kill_chrome_processes
from models.consts import geo_chrome_session_dir, get_free_port, STEALTH_SCRIPT
from models.model import PublishTask, Status, SUCCESS_CODE, ERROR_CODE
from models.platform_consts import profileMap, dou_yin, xiao_hong_shu, zhi_hu, jian_shu, bili, CSDN, gong_zhong_hao, \
    qi_e_hao, bai_jia_hao, wang_yi
from platform_publish.baijiahao import Baijiahao
from platform_publish.bili import Bili
from platform_publish.csdn import CSDN_Publish
from platform_publish.douyin import Douyin
from platform_publish.gongzhonghao import Gongzhonghao
from platform_publish.jianshu import Jianshu
from platform_publish.publish import Publish
from platform_publish.wangyi import Wangyi
from platform_publish.xiaohongshu import Xiaohongshu
from routers.websocket import send_ws_message

publish_tasks = {}

key = "publish_task"


async def do_publish_task(item:PublishTask):
    """真正的异步授权任务"""
    global publish_tasks
    flag = False
    platforms = item.platformNames

    for platform in platforms:
        page: Page = None
        try:
            profile = profileMap.get(platform)
            if not profile:
                raise ValueError(f"不支持的平台: {item.platform}")

            # 启动 Chrome（这步是同步阻塞的，用线程执行）
            dir = item.dataDir + os.sep + geo_chrome_session_dir
            if not os.path.exists(dir):
                os.makedirs(dir)
            default_debug_port = get_free_port()
            start_chrome_with_debug(dir, profile, default_debug_port,item.headless == 0)
            # loop = asyncio.get_running_loop()
            # await loop.run_in_executor(None, start_chrome_with_debug,
            #                            "d:/chrome_test", profile, default_debug_port)

            # 等待 Chrome 启动
            await asyncio.sleep(0.5)

            async with async_playwright() as playwright:
                logger.info(f"连接到 Chrome 调试端口 {default_debug_port}")
                browser: Browser = await playwright.chromium.connect_over_cdp(
                    f"http://127.0.0.1:{default_debug_port}"
                )

                page = browser.contexts[0].pages[0]

                # 关键：使用 script= 参数注入，避免读取文件引发 shutdown 错误
                if STEALTH_SCRIPT:
                    await page.add_init_script(script=STEALTH_SCRIPT)
                publish_obj:Publish = None
                if platform == dou_yin:
                    publish_obj = Douyin(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == xiao_hong_shu:
                    publish_obj = Xiaohongshu(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == zhi_hu:
                    publish_obj = Douyin(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == jian_shu:
                    publish_obj = Jianshu(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == bili:
                    publish_obj = Bili(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == CSDN:
                    publish_obj = CSDN_Publish(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == gong_zhong_hao:
                    publish_obj = Gongzhonghao(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == bai_jia_hao:
                    publish_obj = Baijiahao(item.fileList,item.declareAi,item.contentMarkdown,item.title)
                elif platform == wang_yi:
                    publish_obj = Wangyi(item.fileList,item.declareAi,item.contentMarkdown,item.title)

                if publish_obj:
                    try:
                       await publish_obj.publish(page)
                       await send_ws_message(item.clientId, {
                           "content": {
                               "timestamp": datetime.now().timestamp(),
                               "platform": platform,
                               "id": item.id,
                               "code": SUCCESS_CODE,
                           },
                           "type": "ws:publish_task",
                       })
                    except Exception as e:
                        await send_ws_message(item.clientId, {
                            "content": {
                                "timestamp": datetime.now().timestamp(),
                                "platform": platform,
                                "id": item.id,
                                "code": ERROR_CODE,
                            },
                            "type": "ws:publish_task",
                        })
                        logger.exception("发布任务执行失败", e)
        except Exception as e:
            await send_ws_message(item.clientId, {
                "content": {
                    "timestamp": datetime.now().timestamp(),
                    "platform": platform,
                    "id": item.id,
                    "code": ERROR_CODE,
                },
                "type": "ws:publish_task",
            })
            logger.exception("授权任务执行失败", e)
        finally:
            if  page:
                kill_chrome_processes()
    publish_tasks[key] = Status.NONE


def publish_task(item:PublishTask):
    logger.info(f"发布任务: {item}")
    """
    外部调用接口（同步或异步均可）
    在主事件循环中创建后台任务
    """
    if publish_tasks.get(key) == (Status.RUN):
        logger.warning("账号授权任务")
        return False,'任务发布中！'

    publish_tasks[key] = Status.RUN
    loop = asyncio.get_running_loop()
    loop.create_task(do_publish_task(item))
    logger.info(f"授权任务已启动: {item.platform} - {item.clientId}")
    return True,'执行发布！'