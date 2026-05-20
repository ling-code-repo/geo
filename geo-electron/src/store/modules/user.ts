import { defineStore } from "pinia";
import {
  type userType,
  store,
  router,
  resetRouter,
  routerArrays,
  storageLocal
} from "../utils";
import {
  getLogin,
  refreshTokenApi
} from "@/api/user";
import { useMultiTagsStoreHook } from "./multiTags";
import { type DataInfo, setToken, removeToken, userKey } from "@/utils/auth";
import { useGlobalSocket } from "@/composables/useGlobalSocket";
import { useLocalSocket } from "@/composables/useLocalSocket";
import { articlePublishService } from "@/composables/article-publish.service";
const {initGlobalSocket,closeGlobalSocket} = useGlobalSocket ()
const {initLocalSocket,closeLocalSocket} = useLocalSocket ()
export const useUserStore = defineStore("pure-user", {
  state: (): userType => ({
    // 头像
    avatar: storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? "",
    // 用户名
    username: storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? "",
    // 昵称
    nickname: storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? "",
    // 页面级别权限
    roles: storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [],
    // 按钮级别权限
    permissions:
      storageLocal().getItem<DataInfo<number>>(userKey)?.permissions ?? [],
    // 是否勾选了登录页的免登录
    isRemembered: false,
    // 登录页的免登录存储几天，默认7天
    loginDay: 7
  }),
  actions: {
    /** 存储头像 */
    SET_AVATAR(avatar: string) {
      this.avatar = avatar;
    },
    /** 存储用户名 */
    SET_USERNAME(username: string) {
      this.username = username;
    },
    /** 存储昵称 */
    SET_NICKNAME(nickname: string) {
      this.nickname = nickname;
    },
    /** 存储角色 */
    SET_ROLES(roles: Array<string>) {
      this.roles = roles;
    },
    /** 存储按钮级别权限 */
    /*SET_PERMS(permissions: Array<string>) {
      this.permissions = permissions;
    },*/
    /** 存储是否勾选了登录页的免登录 */
    /*SET_ISREMEMBERED(bool: boolean) {
      this.isRemembered = bool;
    },*/
    /** 设置登录页的免登录存储几天 */
    SET_LOGINDAY(value: number) {
      this.loginDay = Number(value);
    },
    /** 登入 */
    async loginByUsername(data) {
      let result = await getLogin(data);
      if (result) {
        setToken(result);
        // 登录成功后初始化全局WebSocket连接
        try {
          // initGlobalSocket();
          // 初始化本地 WebSocket 连接
         // await initLocalSocket();
          // 初始化文章发布服务
          // await articlePublishService.init();
        } catch (error) {
          console.error('[UserStore] 初始化WebSocket服务失败:', error);
        }
        return true
      }
      return false
    },
    /** 前端登出（不调用接口） */
    logOut() {
      this.username = "";
      this.roles = [];
      this.permissions = [];
      // 销毁文章发布服务
      articlePublishService.destroy();
      // 关闭本地 WebSocket 连接
      closeLocalSocket();
      // 关闭全局 WebSocket 连接
      closeGlobalSocket();
      removeToken();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      router.push("/login");
    },
    /** 刷新`token` */
    async handRefreshToken(data) {
      let result =  await refreshTokenApi(data);
      setToken(result);
      return result;
    }
  }
});

export function useUserStoreHook() {
  return useUserStore(store);
}
