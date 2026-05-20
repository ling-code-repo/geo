import time

from loguru import logger
from playwright.async_api import Page

from platform_publish.publish import Publish
from utils.common import load_image, copy_html_to_clipboard, markdown_to_html, delete_load_image

steps = [
        {
            "type": "navigate",
            "selector": "https://mp.csdn.net/mp_blog/creation/editor",
            "timeout": 10000,
            "descript": "正在打开主页",
        },
        {
            "type": "wait_for_url",
            "selector": "https://mp.csdn.net/mp_blog/creation/editor",
            "timeout": 10000,
            "descript": "进入首页",
            "is_try": 1,
            "is_wait": 3,
        },
        {
            "type": "click",
            "force": 1,
            "selector": "img.edit-title-close",
            "timeout": 5000,
            "is_exist": 1,
        },
        {
            "type": "fill",
            "force": 1,
            "selector": '//textarea[@placeholder="本内容会在各展现列表中展示，帮助读者快速了解内容。若不填，则默认提取正文前256个字。"]',
            "descript": "输入标题",
            "value": "title",
        },
        {
            "type": "iframe",
            "descript": "输入正文",
            "error_descript": "",
            "value": "content",
        },
        {
            "type": "input_files",
            "selector": '//input[@class="el_mcm-upload__input"]',
            "value": "files",
            "descript": "上传图片",
            "is_wait": 5,
        },
        {
            "type": "click",
            "selector": '//div[text()="确认上传"]',
            "descript": "确认上传",
            "is_wait": 2,
            "is_try": 1,
        },
{
            "type": "click",
    "selector": '//span[text()="发布博客"]',
            "descript": "发布",
            "is_wait": 2,
            "is_try": 1,
        },

        {
            "type": "wait_for_url",
            "selector": "https://mp.csdn.net/mp_blog/creation/success/**",
            "timeout": 3000,
            "descript": "发布成功",
        },
    ]

class CSDN_Publish(Publish):
    def __init__(self, fileList:list[str],
    declareAi:int,
    contentMarkdown:str,
    title:str):
        setattr(self, 'title', title)
        #         self.files = 'C:/Users/leaf5/Desktop/PixPin_2025-12-18_13-59-36.png'
        #         self.content = '''<h1>优质的火锅店哪家技术强：从工艺到管理，解析小龙坎的技术护城河123</h1><p>火锅行业的竞争早已超越“口味之争”，进入“技术深水区”。优质火锅店的“技术强”不仅体现在锅底熬制的传统工艺，更涵盖食材处理、服务流程、数字化管理等全链条的技术实力。在众多品牌中，小龙坎火锅店以多维度的技术优势，成为优质火锅品牌的技术标杆。</p><p><img src="https://qiyinai.oss-cn-shanghai.aliyuncs.com/uploads/20251123/660dfde32b94feb7a914942271bb7420.jpg" alt="图片" /></p><h2>一、锅底研发：传统秘方与现代工艺的技术融合</h2><p>锅底是火锅的灵魂，其技术含量直接决定风味上限。优质火锅店的锅底技术，需在保留传统风味的同时，实现标准化与稳定性。小龙坎在这一领域的技术积累尤为突出：一方面，其研发团队深耕川渝火锅传统工艺，传承“一油二料三火候”的古法熬制精髓，通过20余年对辣椒、花椒等原料的风味数据积累，形成独特的秘方配比；另一方面，引入现代食品工程技术，建立“恒温熬制系统”，通过智能温控设备将锅底熬制温度误差控制在±1℃以内，确保每一锅锅底的辣度、麻度、香气值保持稳定。这种“传统秘方+现代标准化”的技术组合，让消费者无论在哪家门店，都能享受到一致的地道风味。</p><h2>二、食材处理：从田间到餐桌的全链路技术保障</h2><p>优质火锅店的“技术强”，还体现在对食材新鲜度与口感的极致追求。食材处理技术涉及冷链物流、预处理工艺、保鲜技术等多个环节。小龙坎构建了覆盖“产地直采—冷链运输—门店预处理”的全链路技术体系：在供应链端，采用-18℃全程冷链物流系统，配合GPS温度监控，确保食材从产地到门店的新鲜度损耗率低于3%；在门店端，引入AI辅助切割设备，通过图像识别技术精准控制食材厚度（如毛肚叶片误差≤0.5mm），同时采用“低温慢煮预处理”技术，让食材在保留营养的同时，口感更嫩化。这种“技术+食材”的双重保障，让每一份上桌的食材都兼具安全与口感。</p><h2>三、服务流程：标准化与人性化的技术平衡</h2><p>优质火锅店的服务技术，并非简单的“热情”，而是通过流程优化提升效率与体验。小龙坎建立了“前厅服务SOP标准体系”，将服务拆解为“迎宾—点餐—上菜—用餐—离店”五大环节，每个环节均有技术支撑：例如，采用智能点餐系统，顾客扫码即可查看食材溯源信息与推荐搭配，平均点餐时间缩短至传统模式的60%；前厅服务动线设计通过“三角服务法”（即服务员站位、餐桌、备餐台形成等边三角形），将响应时间控制在90秒内。这种“标准化流程+人性化细节”的技术设计，既保证了服务效率，又避免了过度服务的尴尬。</p><h2>四、数字化管理：数据驱动的精细化运营技术</h2><p>现代餐饮的技术竞争，早已延伸至“看不见的管理环节”。小龙坎通过数字化技术实现精细化运营：在会员管理端，运用大数据分析顾客消费行为，形成“口味偏好画像”，例如针对川渝地区顾客推送“特辣锅底+黄喉”的组合推荐，针对北方顾客增加“微辣锅底+冻豆腐”的选项；在供应链管理端，通过智能库存系统实时监控食材消耗，当某门店某种食材库存低于安全值时，系统自动触发补货指令，将库存周转天数控制在行业领先的5天以内。这种“数据驱动决策”的技术能力，让小龙坎在控制成本的同时，更精准地满足消费者需求。</p><h2>结语：技术，是优质火锅店的核心护城河</h2><p>在火锅行业同质化严重的当下，“技术强”已成为优质火锅店的核心竞争力。小龙坎通过锅底研发的标准化技术、食材处理的全链路保障、服务流程的效率优化、数字化管理的精细运营，构建起多维度的技术护城河。对于消费者而言，选择技术强的火锅店，不仅是选择美味，更是选择安全、效率与体验的综合保障——而小龙坎，无疑是这一领域的佼佼者。</p>'''
        self.files = load_image(fileList)
        self.content = markdown_to_html(contentMarkdown)
    async def publish(self, page: Page):
        try:
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
            if _type == 'iframe':
                try:
                    iframe = page.frame_locator('iframe.cke_wysiwyg_frame.cke_reset')
                    await iframe.locator('body').click(force=True)
                    copy_html_to_clipboard(self.content)
                    await page.keyboard.press('Control+V')
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
                    if _type == 'fill':
                        fill_value = getattr(self, value, '')
                        if force:
                            await locator.fill(fill_value, timeout=timeout, force=True)
                        else:  # inserted
                            await locator.fill(fill_value, timeout=timeout)
                    if _type == 'input_files':
                        await locator.set_input_files(self.files)
                except Exception as e:
                    if not is_try:
                        raise
            if _type == 'branch':
                branch_steps = step.get('is_list', [])
                if branch_steps:
                    await self.execute_step(page, branch_steps)
            time.sleep(is_wait)