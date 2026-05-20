import asyncio
import time

from loguru import logger
from playwright.async_api import Page

from platform_publish.publish import Publish
from utils.common import load_image, convert_plaintext, delete_load_image

steps = [
    {
        "type": "navigate",
        "selector": "https://creator.xiaohongshu.com/publish/publish?from=menu&target=image",
        "timeout": 3000,
        "is_try": 0,
        "is_wait": 0,
        "is_exist": 0,
        "descript": "正在打开主页",
        "error": "登录失效"
    },
    {
        "type": "wait_for_url",
        "selector": "https://creator.xiaohongshu.com/publish/publish?from=menu&target=image",
        "timeout": 5000,
        "is_wait": 0,
        "is_exist": 0,
        "descript": "进入首页",
        "error_descript": "登录失效"
    },
    {
        "type": "input_files",
        "selector": '//input[@class="upload-input"]',
        "timeout": 5000,
        "descript": "上传图片",
        "error": ""
    },
    {
        "type": "fill",
        "selector": '//input[@placeholder="填写标题会有更多赞哦～"]',
        "value": "title",
        "is_exist": 1,
        "descript": "输入标题",
        "error": ""
    },
    {
        "type": "tags",
        "selector": "",
        "descript": "添加话题"
    },
    {
        "type": "click",
        "selector": "button.publishBtn",
        "descript": "点击【发布】按钮提交作品"
    },

    {
        "type": "wait_for_url",
        "selector": "https://creator.xiaohongshu.com/publish/success**",
        "timeout": 20000,
        "is_wait": 0,
        "is_exist": 0,
        "descript": "发布成功",
        "error": "发布失败"
    }
]

class Xiaohongshu(Publish):
    def __init__(self, fileList:list[str],
    declareAi:int,
    contentMarkdown:str,
    title:str):
        # for key, value in data.items():
            # setattr(self, key, value)
        # self.tags = tags
        # self.files = load_image(image)
        # self.content = content
        setattr(self, 'title', title)
        self.tags = ''
        self.files = load_image(fileList)
        self.content = convert_plaintext(contentMarkdown)
    async def publish(self, page: Page):
        try :
            await self.execute_step(page,steps)
        finally:
             delete_load_image()
    async def execute_step(self,page:Page,steps:list):
        for step in steps:
            _type = step.get('type', '')
            selector = step.get('selector', '')
            timeout = step.get('timeout', 15000)
            is_try = step.get('is_try', 0)
            nth = step.get('nth', '')
            is_wait = step.get('is_wait', 3)
            is_exist = step.get('is_exist', 0)
            value = step.get('value', '')
            force = step.get('force', 0)
            _break = step.get('_break', 0)

            if _type == 'navigate':
                await page.goto(selector)

            if _type == 'wait_for_url':
                try:
                    await page.wait_for_url(selector, timeout=timeout)
                except Exception as e:
                    if _break:
                        return
            if _type == 'wait_for_selector':
                try:
                    await page.wait_for_selector(selector, timeout=timeout)
                except Exception as e:
                    if not is_try:
                        raise
            if _type in ['click', 'fill', 'input_files', 'hover']:
                try:
                    _start = time.time()
                    if is_exist:
                        try:
                            locator = page.locator(selector)
                            await locator.first.wait_for(timeout=timeout)
                        except:
                            logger.info(f'元素{selector}不存在,设置可跳过')
                            continue
                    else:  # inserted
                        locator = page.locator(selector)
                    if nth != '':
                        locator = locator.nth(nth)
                    if _type == 'click':
                        if force:
                            await locator.click(timeout=timeout, force=True)
                        else:  # inserted
                            await locator.click(timeout=timeout)
                    if _type == 'hover':
                        await locator.hover()
                        await locator.click(timeout=timeout)
                    else:  # inserted
                        if _type == 'fill':
                            fill_value = getattr(self, value, '')
                            if force:
                                await locator.fill(fill_value, timeout=timeout, force=True)
                            else:  # inserted
                                await locator.fill(fill_value, timeout=timeout)
                        else:  # inserted
                            if _type == 'input_files':
                                await locator.set_input_files(self.files.split(','))
                except Exception as e:
                    if not is_try:
                        raise
            if _type == 'branch':
                branch_steps = step.get('is_list', [])
                if branch_steps:
                    await self.execute_step(page, branch_steps)
            if _type == 'tags':

                editor_selector = 'div[contenteditable="true"]'

                # 等待编辑器出现并可操作
                await page.wait_for_selector(editor_selector, state="visible", timeout=10000)

                editor = page.locator(editor_selector)

                # 强制点击聚焦
                await editor.click(force=True)
                await page.wait_for_timeout(800)

                # 清空（全选删除）
                await page.keyboard.down("Control")
                await page.keyboard.press("A")
                await page.keyboard.up("Control")
                await page.keyboard.press("Delete")

                # 输入正文
                # await editor.type(self.content, delay=50)  # delay 模拟真实打字，更稳定

                await editor.type(self.content)  # delay 模拟真实打字，更稳定

                # 添加话题
                if self.tags:
                    tag_list = [t.strip() for t in self.tags.split(",") if t.strip()]
                    for tag in tag_list:
                        await editor.press("Space")  # 小红书话题推荐是空格触发
                        # await asyncio.sleep(1.5)
                        await editor.type(f"#{tag}")
                        await editor.press("Space")  # 小红书话题推荐是空格触发
                        await asyncio.sleep(0.8)  # 等待话题候选框消失
            time.sleep(is_wait)