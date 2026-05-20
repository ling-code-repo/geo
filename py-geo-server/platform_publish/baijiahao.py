import time

from loguru import logger
from playwright.async_api import Page

from platform_publish.publish import Publish
from utils.common import copy_html_to_clipboard, markdown_to_html

steps = [
        {
            "type": "navigate",
            "selector": "https://baijiahao.baidu.com/builder/rc/edit?type=news&is_from_cms=1",
            "timeout": 30000,
            "descript": "正在打开主页",
            "is_wait": 1,
        },
        {
            "type": "click",
            "selector": '//button[@class="cheetah-tour-close"]',
            "descript": "关闭弹窗则关闭",
            "is_wait": 1,
            "is_exist": 1,
            "timeout": 10000,
        },
        {
            "type": "click",
            "selector": "div.c27d9e2f3e73a2ac-btn-close",
            "descript": "存在弹窗则关闭",
            "is_wait": 1,
            "is_exist": 1,
            "timeout": 5000,
        },
        {
            "type": "click",
            "selector": '//div[@class="_9ddb7e475b559749-editor _377c94a778c072b3-editor"]',
            "descript": "点击标题",
            "value": "title",
            "is_wait": 1,
        },
        {
            "type": "fill",
            "selector": '//div[@class="_9ddb7e475b559749-editor _377c94a778c072b3-editor"]',
            "descript": "输入标题",
            "value": "title",
            "is_wait": 1,
        },
        {
            "type": "press",
            "selector": '//iframe[@id="ueditor_0"]',
            "descript": "输入正文",
            "error_descript": "",
            "value": "content",
        },
        {"type": "click", "selector": '//span[text()="单图"]', "descript": "上传图片"},
        {
            "type": "click",
            "selector": "div.DraggableTags-tag-drag div.coverUploaderView",
            "is_exist": 1,
            "timeout": 3000,
            "descript2": "A用户点击上传图片焦点",
        },
        {
            "type": "click",
            "selector": '//div[text()="选择封面"]',
            "is_exist": 1,
            "timeout": 3000,
            "descript2": "B用户点击上传图片焦点",
        },
        {
            "type": "click",
            "selector": '//button//span[contains(text(), \"确定\")]',
            "descript": "确认图片",
            "is_exist": 1,
            "timeout": 3000,
            "descript2": "B用户点击上传图片焦点",
        },
        {
            "type": "click",
            "selector": "div.cheetah-ui-pro-base-image",
            "nth": 0,
            "descript": "选择第一张图片",
            "is_exist": 1,
            "timeout": 3000,
            "descript2": "A用户点击上传图片焦点",
        },
        {
            "type": "click",
            "selector": '//span[text()="去编辑"]',
            "descript": "去剪辑",
            "is_exist": 1,
            "timeout": 3000,
            "descript2": "A用户点击上传图片焦点",
        },
        {
            "type": "click",
            "selector": '//button[text()="完成"]',
            "is_exist": 1,
            "timeout": 3000,
            "descript2": "A用户点击上传图片焦点",
        },
        {
            "type": "click",
            "selector": "span.l-icon-BjhBasicGuanbi svg",
            "is_exist": 1,
            "descript": "关闭提醒弹窗",
            "timeout": 3000,
        },
{"type": "click", "selector": '//span[text()="AI创作声明"]', "descript": "勾选AI创作声明","value": "declareAi","declareValue":"1"},
        # {
        #     "type": "branch",
        #     "is_value": "is_ai",
        #     "is_list": [
        #         {"type": "click", "selector": '//span[text()="AI创作声明"]', "descript": "勾选AI创作声明"}
        #     ],
        #     "else_list": [],
        # },
        # {
        #     "type": "branch",
        #     "is_value": "guazai_phone",
        #     "is_list": [
        #         {"type": "click", "selector": '//span[text()="挂载营销通电话"]', "descript": "勾选挂载电话", "is_try": 1},
        #         {"type": "click", "selector": '//span[text()="我知道了，去挂载"]', "is_try": 1},
        #         {"type": "click", "selector": '//span[text()="请选择电话方案"]/parent::div//input', "is_try": 1},
        #         {"type": "click", "selector": "div.cheetah-select-item", "is_try": 1, "nth": 0},
        #         {"type": "click", "selector": '//span[text()="确定"]', "is_try": 1},
        #     ],
        #     "else_list": [],
        # },
        # {
        #     "type": "click",
        #     "selector": '//span[text()="AI创作声明"]',
        #     "is_value": "ai_status",
        # },
{"type": "click", "selector": '//span[text()="发布"]', "is_exist": 1, "descript": "点击发布"},
                # {"type": "click", "selector": '//button//span[text()="发布"]', "is_exist": 1, "is_try": 1, "descript": "点击定时发布"},
        # {
        #     "type": "branch",
        #     "is_value": "timing",
        #     "is_list": [
        #         {"type": "click", "selector": '//span[text()="定时发布"]', "is_try": 1},
        #         {"type": "click", "selector": '//div[text()="定时发布"]/parent::div', "descript": "点击定时发布", "is_try": 1},
        #         {"type": "click", "selector": '//div[@class="select-wrap"]', "nth": 0},
        #         {"type": "baidu-timmer", "selector": "", "descript": "选择日期"},
        #     ],
        #     "else_list": [
        #         {"type": "click", "selector": '//div[text()="发布"]/parent::div', "is_exist": 1, "descript": "点击定时发布"},
        #         {"type": "click", "selector": '//button//span[text()="发布"]', "is_exist": 1, "is_try": 1, "descript": "点击定时发布"},
        #     ],
        # },
        # {
        #     "type": "wait_for_selector",
        #     "selector": '//p[text()="提交成功，正在审核中..."]',
        #     "timeout": 5000,
        #     "is_wait": 0,
        #     "is_exist": 0,
        #     "descript": "发布成功",
        #     "error": "发布失败",
        # },
    ]

class Baijiahao(Publish):
    def __init__(self, fileList:list[str],
    declareAi:int,
    contentMarkdown:str,
    title:str):
        setattr(self, 'declareAi', declareAi)
        setattr(self, 'title', title)
        self.content = markdown_to_html(contentMarkdown)
    async def publish(self, page: Page):
        await self.execute_step(page,steps)
    async def execute_step(self,page:Page,steps:list):
        for step in steps:
            _type = step.get('type', '')
            selector = step.get('selector', '')
            timeout = step.get('timeout', 15000)
            is_try = step.get('is_try', 0)
            nth = step.get('nth', '')
            is_wait = step.get('is_wait', 3)
            is_exist = step.get('is_exist', 0)
            declareValue = step.get('declareValue', '')
            value = step.get('value', '')
            force = step.get('force', 0)
            _break = step.get('_break', 0)

            if _type == 'navigate':
                await page.goto(selector)

            if _type == 'wait_for_url':
                try:
                    await page.wait_for_url(selector, wait_until='load', timeout=timeout)
                except Exception as e:
                    if _break:
                        return
            if _type == 'wait_for_selector':
                try:
                    await page.wait_for_selector(selector, timeout=timeout)
                except Exception as e:
                    if not is_try:
                        raise
            if _type == 'press':
                if value == 'content':
                    locator = page.locator(selector)
                    if nth != '':
                        locator = locator.nth(nth)
                    await page.evaluate("""
                                () => {
                                    const editorIframe = document.getElementById('ueditor_0');
                                    if (editorIframe && editorIframe.contentWindow) {
                                        try {
                                            editorIframe.contentWindow.document.body.innerHTML = '';
                                            return '通过备用方法清空成功';
                                        } catch (e) {
                                            return '备用方法失败: ' + e.message;
                                        }
                                    }
                                    return '未找到编辑器iframe';
                                }
                            """)
                    if force:
                        await locator.click(timeout=timeout, force=True)
                    else:  # inserted
                        await locator.click(timeout=timeout)
                    copy_html_to_clipboard(self.content)
                    time.sleep(2)
                    await page.keyboard.press('Control+V')
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
                        val = getattr(self, value, '')
                        if force:
                            await locator.click(timeout=timeout, force=True)
                        elif val:
                            if val == declareValue:
                               await locator.click(timeout=timeout)
                        else:  # inserted
                            await locator.click(timeout=timeout)
                    if _type == 'hover':
                        await locator.hover()
                        await locator.click(timeout=timeout)
                    if _type == 'fill':
                        fill_value = getattr(self, value, '')
                        if force:
                            await locator.fill(fill_value, timeout=timeout, force=True)
                        else:  # inserted
                            await locator.fill(fill_value, timeout=timeout)

                except Exception as e:
                    if not is_try:
                        raise

            time.sleep(is_wait)