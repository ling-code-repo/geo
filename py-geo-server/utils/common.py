import time
from pathlib import Path

import requests
import win32clipboard
from commonmark.blocks import Parser
from commonmark.node import Node
from loguru import logger


def copy_html_to_clipboard(html_text, plain_text=None):
    """
    将带样式的HTML文本复制到剪贴板
    参数：
    html_text - 包含HTML标签的富文本
    plain_text - 备用纯文本(可选)
    """
    header = f'Version:0.9\nStartHTML:00000000\nEndHTML:00000000\nStartFragment:00000000\nEndFragment:00000000\n<html>\n<body>\n<!--StartFragment-->\n{html_text}\n<!--EndFragment-->\n</body>\n</html>\n'
    start_html = header.find('<html>')
    end_html = len(header.encode('utf-8'))
    start_fragment = header.find('<!--StartFragment-->') + 20
    end_fragment = header.find('<!--EndFragment-->')
    header = header.replace('00000000', str(start_html).zfill(8))
    header = header.replace('StartHTML:00000000', f'StartHTML:{str(start_html).zfill(8)}')
    header = header.replace('EndHTML:00000000', f'EndHTML:{str(end_html).zfill(8)}')
    header = header.replace('StartFragment:00000000', f'StartFragment:{str(start_fragment).zfill(8)}')
    header = header.replace('EndFragment:00000000', f'EndFragment:{str(end_fragment).zfill(8)}')
    try:
        win32clipboard.OpenClipboard()
        win32clipboard.EmptyClipboard()
        win32clipboard.SetClipboardData(win32clipboard.RegisterClipboardFormat('HTML Format'), header.encode('utf-8'))
        if plain_text:
            win32clipboard.SetClipboardData(win32clipboard.CF_UNICODETEXT, plain_text)
            return
    finally:
        win32clipboard.CloseClipboard()

def _get_user_temp_dir():
    """
    Get a user-writable temp directory for app data across dev and frozen builds.
    Prefer %APPDATA%/geo/tempfile on Windows, and ~/.config/geo/tempfile on others.
    """
    import sys
    import os
    from pathlib import Path
    app_dir_name = 'geo'
    appdata = os.environ.get('APPDATA')
    if appdata:
        target = Path(appdata) / app_dir_name / 'tempfile'
    else:
        target = Path.home() / '.config' / app_dir_name / 'tempfile'
    try:
        target.mkdir(parents=True, exist_ok=True)
        return str(target)
    except Exception:
        target = Path(os.getenv('TEMP') or os.getenv('TMP') or Path.home() / 'AppData' / 'Local' / 'Temp') / app_dir_name / 'tempfile'
        target.mkdir(parents=True, exist_ok=True)
        return str(target)

def load_image(image_url_list:list):

    import sys
    import os
    if getattr(sys, 'frozen', False):
        mytempfile_dir = _get_user_temp_dir()
    else:
        grandparent_dir = Path(__file__).parent.parent
        mytempfile_dir = grandparent_dir / 'tempfile'
    if not os.path.exists(mytempfile_dir):
        os.makedirs(mytempfile_dir)
    images = []
    for image_url in image_url_list:
        name = int(time.time() * 10000)
        image_path = os.path.join(mytempfile_dir, f'{name}.png')
        response = requests.get(image_url)
        if response.status_code == 200:
            with open(image_path, 'wb') as f:
                f.write(response.content)
            logger.info(f'图片已保存为 {image_path}')
            images.append(image_path)
        else:
            logger.info(f'下载失败，状态码：{response.status_code}')
        return ",".join(images)

def delete_load_image():
    """
    清理tempfile目录中的所有图片文件
    在客户端登录时调用，用于清理临时下载的图片
    """
    try:
        import sys
        import os
        import glob
        if getattr(sys, 'frozen', False):
            mytempfile_dir = _get_user_temp_dir()
        else:
            grandparent_dir = Path(__file__).parent.parent
            mytempfile_dir = grandparent_dir / 'tempfile'
        if not os.path.exists(mytempfile_dir):
            logger.info(f'tempfile目录不存在: {mytempfile_dir}')
            return
        image_extensions = ['*.png', '*.jpg', '*.jpeg', '*.gif', '*.bmp', '*.webp']
        deleted_count = 0
        for ext in image_extensions:
            pattern = os.path.join(mytempfile_dir, ext)
            files = glob.glob(pattern)
            for file_path in files:
                try:
                    os.remove(file_path)
                    deleted_count += 1
                    logger.info(f'已删除图片文件: {file_path}')
                except Exception as e:
                    logger.info(f'删除文件失败 {file_path}: {e}')
        logger.info(f'清理完成，共删除 {deleted_count} 个图片文件')
        return deleted_count
    except Exception as e:
        logger.info(f'清理图片文件时发生错误: {e}')
        return 0


def walk(result:list,node: Node,ingore_image:bool):
    # 忽略 Image 节点（不输出 alt / url）
    if node.t == "image":
        if ingore_image:
            return
        alt = []
        child = node.first_child
        while child:
            if child.t == "text":
                alt.append(child.literal)
            child = child.nxt

        alt_text = "".join(alt)
        url = node.destination or ""
        result.append(f"![{alt_text}]({url})")
        return

    # 文本节点
    if node.t == "text":
        result.append(node.literal)

    # 换行（段落、标题）
    if node.t in ("paragraph", "heading"):
        result.append("\n")

    child = node.first_child
    while child:
        walk(result,child,ingore_image)
        child = child.nxt

def convert_plaintext(markdown: str,ingore_image:bool = True) -> str:
    parser = Parser()
    ast = parser.parse(markdown)
    result = []
    walk(result,ast,ingore_image)
    return "".join(result).strip()



from markdown_it import MarkdownIt

from markdown_it.renderer import RendererHTML

class NoImageRenderer(RendererHTML):
    def image(self, tokens, idx, options, env):
        return ""   # 直接丢弃图片

    def paragraph_open(self, tokens, idx, options, env):
        inline = tokens[idx + 1]
        if (
            inline.type == "inline"
            and inline.children
            and all(t.type == "image" for t in inline.children)
        ):
            return ""
        return "<p>"

    def paragraph_close(self, tokens, idx, options, env):
        inline = tokens[idx - 1]
        if (
            inline.type == "inline"
            and inline.children
            and all(t.type == "image" for t in inline.children)
        ):
            return ""
        return "</p>\n"

def markdown_to_html(md_text: str, ignore_image: bool = False) -> str:
    md = MarkdownIt(
        "commonmark",
        options_update={
            "html": False,   # 禁止 md 中的原生 HTML（防 XSS）
            "linkify": True
        }
    )
    if ignore_image:
        md.renderer = NoImageRenderer()
    return md.render(md_text)