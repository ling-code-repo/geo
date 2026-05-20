import dyIcon from '@/assets/geo/platform/dy.png';
import wyIcon from '@/assets/geo/platform/wy.jpg';
import zhIcon from '@/assets/geo/platform/zh.png';
import jsIcon from '@/assets/geo/platform/js.png';
import biliIcon from '@/assets/geo/platform/bili1.png';
import csdnIcon from '@/assets/geo/platform/csdn.png';
import wxgzhIcon from '@/assets/geo/platform/wxgzh.png';
import bjhIcon from '@/assets/geo/platform/bjh.png';
import xhsIcon from '@/assets/geo/platform/xhs.png';

export interface AccountInfo {
  /** 消息类型，可选 `info` 、`success` 、`warning` 、`error` ，默认 `info` */
  name: string;
  /** 是否纯色，默认 `false` */
  avatarUrl: string;
}

export enum Platform {
  DOU_YIN = "抖音",
  XIAO_HONG_SHU = "小红书",
  ZHI_HU = "知乎",
  JIAN_SHU = "简书",
  BI_LI = "哔哩",
  CSDN = "CSDN",
  GONG_ZHONG_HAO = "公众号",
  QI_E_HAO = "企鹅号",
  // TOU_TIAO_HAO = "头条号",
  BAI_JIA_HAO = "百家号",
  // SOU_HU = "搜狐",
  WANG_YI = "网易",
}

export const platforms = [
  {
    id: 0,
    type: Platform.DOU_YIN,
    icon: dyIcon,
    url: "https://creator.douyin.com/",
    persist:"session-20b57e8a41c",
  },
  {
    id: 1,
    type: Platform.WANG_YI,
    icon: wyIcon,
    url: "https://mp.163.com/login.html",
    persist:"session-20b57e8a41c",
  },

  {
    id: 2,
    type: Platform.ZHI_HU,
    icon: zhIcon,
    url: "https://www.zhihu.com/creator",
    persist:"session-20b57e8a41c",
  },
  {
    id: 3,
    type: Platform.JIAN_SHU,
    icon: jsIcon,
    url: "https://www.jianshu.com/sign_in",
    persist:"session-20b57e8a41c",
  },
  {
    id: 4,
    type: Platform.BI_LI,
    icon: biliIcon,
    url: "https://account.bilibili.com/account/home",
    // https://member.bilibili.com/platform/home
    persist:"session-20b57e8a41c",
  },
  {
    id: 5,
    type: Platform.CSDN,
    icon: csdnIcon,
    url: "https://passport.csdn.net/login?code=applets",
    persist:"session-20b57e8a41c",
  },
  {
    id: 6,
    type: Platform.GONG_ZHONG_HAO,
    icon: wxgzhIcon,
    url: "https://mp.weixin.qq.com/",
    persist:"session-20b57e8a41c",
  },

  {
    id: 7,
    type: Platform.BAI_JIA_HAO,
    icon: bjhIcon,
    url: "https://baijiahao.baidu.com/builder/rc/login",
    persist:"session-20b57e8a41c",
  },
  {
    id: 8,
    type: Platform.XIAO_HONG_SHU,
    icon: xhsIcon,
    url: "https://creator.xiaohongshu.com/new/note-manager",
    persist:"session-20b57e8a41c",
  },


];

const getDouyinNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("#header-avatar div").getAttribute("style");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class^=name-]").innerText;
      })()
  `)
    info.name = name
    avatar = avatar.substring(avatar.indexOf('"')+1)
    avatar = avatar.substring(0,avatar.indexOf('"'))
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取抖音数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}



const getXiaohongshuNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[class=user_avatar]").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("span[class=name-box]").innerText;
      })()
  `)
    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取小紅书数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getZhihuNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[class^=Avatar]").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[class^=Avatar]").getAttribute("alt");
      })()
  `)

    info.name = name.replace("点击打开",'').replace('的主页','')
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取知乎数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}
const sleep = (ms: number) => {
  return new Promise(resolve => setTimeout(resolve, ms));
}

const getJianshuNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    await webviewElement.executeJavaScript(`
  (function() {
        document.querySelector("a[class=avatar]").click();
      })()
  `)
    await sleep(300);
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("a[class=avatar] img").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("a[class=name]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取简书数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getBiliNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{

    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class=home-head] img").getAttribute("src");
      })()
  `)
    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("span[class=home-top-msg-name]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取哔哩哔哩数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getCSDNNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    await webviewElement.executeJavaScript(`
  (function() {
       document.querySelector("a[class=hasAvatar]").click();
      })()
  `)
    await sleep(300);
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("a[class=hasAvatar] img").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class=user-profile-head-name] div").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取CSDN数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getGongzhonghaoNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[class=weui-desktop-account__img]").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class=weui-desktop_name]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取CSDN数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getQiehaoNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class=omui-avatar] img").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("span[class^=usernameText]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取CSDN数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getBaijiahaoNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{
    await webviewElement.executeJavaScript(`
  (function() {
       document.querySelector("#asideMenuItem-个人中心").click();
      })()
  `)
    await sleep(300);
    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[class*=-userImg]").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class*=-userName]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取CSDN数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getSouhuNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{

    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[class=avatar-img]").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class*=nickname-input]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取CSDN数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

const getWangyiNameAndAvatar = async ( webviewElement: any,
): Promise<AccountInfo> => {
  let info = {} as AccountInfo
  try{

    let avatar = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("img[alt=头像]").getAttribute("src");
      })()
  `)

    let name = await webviewElement.executeJavaScript(`
  (function() {
        return document.querySelector("div[class*=__userInfo__tname]").innerText;
      })()
  `)

    info.name = name
    info.avatarUrl = avatar
  }catch(error){
    console.log("获取CSDN数据错误！",error);
  }
  return new Promise((resolve, reject) => {
    resolve(info)
  });
}

export const getNameAndAvatar = async (
  type: string,
  webviewElement: any,
): Promise<AccountInfo> => {
  let info = null as AccountInfo

  switch (type) {
    case Platform.DOU_YIN:
      info = await  getDouyinNameAndAvatar(webviewElement)
      break
    case Platform.XIAO_HONG_SHU:
      info = await  getXiaohongshuNameAndAvatar(webviewElement)
      break;
    case Platform.ZHI_HU:
      info = await  getZhihuNameAndAvatar(webviewElement)
      break;
    case Platform.JIAN_SHU:
      info = await  getJianshuNameAndAvatar(webviewElement)
      break;
    case Platform.BI_LI:
      info = await  getBiliNameAndAvatar(webviewElement)
      break;
    case Platform.CSDN:
      info = await  getCSDNNameAndAvatar(webviewElement)
      break;
    case Platform.GONG_ZHONG_HAO:
      info = await  getGongzhonghaoNameAndAvatar(webviewElement)
      break;
    case Platform.QI_E_HAO:
      info = await  getQiehaoNameAndAvatar(webviewElement)
      break;
    case Platform.TOU_TIAO_HAO:
      // info = await  getXiaohongshuNameAndAvatar(webviewElement)
      break;
    case Platform.BAI_JIA_HAO:
      info = await  getBaijiahaoNameAndAvatar(webviewElement)
      break;
    case Platform.SOU_HU:
      info = await  getSouhuNameAndAvatar(webviewElement)
      break;
    case Platform.WANG_YI:
      info = await  getWangyiNameAndAvatar(webviewElement)
      break;
  }

  return new Promise((resolve, reject) => {
    resolve(info)
  });
};
