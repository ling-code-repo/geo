import asyncio
import os

from loguru import logger
from playwright.async_api import async_playwright, Browser, Page

from chrome.chrome import start_chrome_with_debug
from models.consts import geo_chrome_session_dir, get_free_port, STEALTH_SCRIPT
from models.model import AccountAuthorization, SUCCESS_CODE, ERROR_CODE, Status
from models.platform_consts import profileMap, platformUrlMap, dou_yin, xiao_hong_shu, zhi_hu, jian_shu, bili, CSDN, \
    gong_zhong_hao, qi_e_hao, bai_jia_hao, wang_yi
from routers.websocket import send_ws_message
from websocket_manager import manager

max_timeout = 600_000  # 10分钟，单位毫秒


# 手动读取 stealth.js 内容（关键！避免 Playwright 在 shutdown 时读文件）


key = "authorization_task"

authorization_tasks = {}


async def getDouyinNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("#header-avatar div", "style", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class^=name-]").innerText;
        })()
    """)
    avatar = avatar[(avatar.index('"') + 1):]
    avatar = avatar[0: avatar.index('"')]
    return name, avatar


async def getXiaohongshuNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("img[class=user_avatar]", "src", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("span[class=name-box]").innerText;
        })()
    """)
    return name, avatar


async def getZhihuNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("img[class^=Avatar]", "src", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("img[class^=Avatar]").getAttribute("alt");
        })()
    """)
    return name.replace("点击打开",'').replace('的主页',''), avatar


async def getJianshuNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("a[class=avatar] img", "src", timeout=max_timeout)
    await page.evaluate("""
        (function() {
            document.querySelector("a[class=avatar]").click();
        })()
    """)
    await asyncio.sleep(1)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("a[class=name]").innerText;
        })()
    """)
    return name, avatar


async def getBiliNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("div[class^=header-avatar-wrap--container] picture[class=v-img] img", "src", timeout=max_timeout)

    await asyncio.sleep(1)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class=nickname]").innerText;
        })()
    """)
    if not avatar.startswith("https"):
        avatar = "https:" + avatar
    return name, avatar


async def getCSDNNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("a[class=hasAvatar] img", "src", timeout=max_timeout)
    await page.evaluate("""
        (function() {
            document.querySelector("a[class=hasAvatar]").click();
        })()
    """)
    await asyncio.sleep(1)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class=user-profile-head-name] div").innerText;
        })()
    """)
    return name, avatar


async def getGongzhonghaoNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("img[class=weui-desktop-account__img]", "src", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class=weui-desktop_name]").innerText;
        })()
    """)
    return name, avatar


async def getQiehaoNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("div[class=omui-avatar] img", "src", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("span[class^=usernameText]").innerText;
        })()
    """)
    return name, avatar


async def getBaijiahaoNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("img[alt=头像]", "src", timeout=max_timeout)
    # avatar = await page.get_attribute("img[class*=-userImg]", "src", timeout=max_timeout)
    await page.evaluate("""
        (function() {
            document.querySelector("#asideMenuItem-个人中心").click();
        })()
    """)
    await asyncio.sleep(2)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class*=-userName]").innerText;
        })()
    """)
    if not avatar.startswith("https"):
        avatar = "https:" + avatar
    return name, avatar


async def getSouhuNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("img[class=avatar-img]", "src", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class*=nickname-input]").innerText;
        })()
    """)
    return name, avatar


async def getWangyiNameAndAvatar(page: Page) -> tuple[str, str]:
    avatar = await page.get_attribute("img[alt=头像]", "src", timeout=max_timeout)
    name = await page.evaluate("""
        (function() {
            return document.querySelector("div[class*=__userInfo__tname]").innerText;
        })()
    """)
    return name, avatar

async def authorization_task(item: AccountAuthorization):
        """真正的异步授权任务"""
        global authorization_tasks
        flag = False
        try:
            profile = profileMap.get(item.platform)
            if not profile:
                raise ValueError(f"不支持的平台: {item.platform}")

            url = platformUrlMap[item.platform]

            # 启动 Chrome（这步是同步阻塞的，用线程执行）
            dir = item.dataDir + os.sep + geo_chrome_session_dir
            if not os.path.exists(dir):
                os.makedirs(dir)
            default_debug_port = get_free_port()
            start_chrome_with_debug(dir, profile, default_debug_port)
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

                page: Page = browser.contexts[0].pages[0]

                # 关键：使用 script= 参数注入，避免读取文件引发 shutdown 错误
                if STEALTH_SCRIPT:
                    await page.add_init_script(script=STEALTH_SCRIPT)

                logger.info(f"导航到 {item.platform} 页面: {url}")
                await page.goto(url, wait_until='domcontentloaded',timeout=max_timeout)

                await asyncio.sleep(5)  # 等待页面稳定 + 用户可能手动登录

                # 尝试多种方式提取头像和昵称（不同平台 selector 不同）
                avatar = None
                name = None
                if item.platform == dou_yin:
                    (name, avatar) = await getDouyinNameAndAvatar(page)
                elif item.platform == xiao_hong_shu:
                     (name, avatar) = await getXiaohongshuNameAndAvatar(page)
                elif item.platform == zhi_hu:
                     (name, avatar) = await  getZhihuNameAndAvatar(page)
                elif item.platform == jian_shu:
                     (name, avatar) = await getJianshuNameAndAvatar(page)
                elif item.platform == bili:
                     (name, avatar) = await getBiliNameAndAvatar(page)
                elif item.platform == CSDN:
                     (name, avatar) = await getCSDNNameAndAvatar(page)
                elif item.platform == gong_zhong_hao:
                     (name, avatar) = await getGongzhonghaoNameAndAvatar(page)
                elif item.platform == qi_e_hao:
                     (name, avatar) = await getQiehaoNameAndAvatar(page)
                elif item.platform == bai_jia_hao:
                     (name, avatar) = await getBaijiahaoNameAndAvatar(page)
                elif item.platform == wang_yi:
                     (name, avatar) = await getWangyiNameAndAvatar(page)

                if avatar and name:
                    flag = True
                    await send_ws_message(item.clientId, {
                        "content":{
                            "name": name,
                            "avatar": avatar,
                            "platform": item.platform,
                            "code": SUCCESS_CODE,
                        },
                        "type": "ws:account_authorization",

                    })
                else:
                    logger.warning(f"未检测到完整用户信息: name={name}, avatar={avatar}")

        except Exception as e:
            logger.exception("授权任务执行失败", e)

        finally:
            if not flag:
                await send_ws_message(item.clientId, {
                    "type": "ws:account_authorization",
                    "content":{
                        "platform": item.platform,
                        "code": ERROR_CODE,
                        "message": "授权失败"
                    }
                })
            authorization_tasks[key] = Status.NONE

def authorization(item: AccountAuthorization):
    """
    外部调用接口（同步或异步均可）
    在主事件循环中创建后台任务
    """
    if authorization_tasks.get(key) == Status.RUN:
        logger.warning("账号授权任务")
        return False,'账号正在授权！'

    authorization_tasks[key] = Status.RUN
    loop = asyncio.get_running_loop()
    loop.create_task(authorization_task(item))
    logger.info(f"授权任务已启动: {item.platform} - {item.clientId}")
    return True,''