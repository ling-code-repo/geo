import Axios, {
  type AxiosInstance,
  type AxiosRequestConfig, type AxiosResponse,
  type CustomParamsSerializer
} from "axios";
import type {
  PureHttpError,
  RequestMethods,
  PureHttpResponse,
  PureHttpRequestConfig
} from "./types.d";
import { stringify } from "qs";
import { getToken, formatToken,getAccessToken,getRefreshToken,setToken } from "@/utils/auth";
import { useUserStoreHook } from "@/store/modules/user";
import errorCode from "@/utils/http/errorCode";
const ignoreMsgs = [
  '无效的刷新令牌', // 刷新令牌被删除时，不用提示
  '刷新令牌已过期' // 使用刷新令牌，刷新获取新的访问令牌时，结果因为过期失败，此时需要忽略。否则，会导致继续 401，无法跳转到登出界面
]
const result_code = 200;

import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
let isRefreshToken = false
let requestList: any[] = []

const base_url = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL;
// 相关配置请参考：www.axios-js.com/zh-cn/docs/#axios-request-config-1
const defaultConfig: AxiosRequestConfig = {
  // 请求超时时间
  timeout: 10000,
  headers: {
    Accept: "application/json, text/plain, */*",
    "Content-Type": "application/json",
    "X-Requested-With": "XMLHttpRequest"
  },
  // 数组格式参数序列化（https://github.com/axios/axios/issues/5142）
  paramsSerializer: {
    serialize: stringify as unknown as CustomParamsSerializer
  }
};

class PureHttp {
  constructor() {
    this.httpInterceptorsRequest();
    this.httpInterceptorsResponse();
  }

  /** `token`过期后，暂存待执行的请求 */
  private static requests = [];

  /** 防止重复刷新`token` */
  private static isRefreshing = false;

  /** 初始化配置对象 */
  private static initConfig: PureHttpRequestConfig = {};

  /** 保存当前`Axios`实例对象 */
  private static axiosInstance: AxiosInstance = Axios.create(defaultConfig);

  /** 重连原始请求 */
  private static retryOriginalRequest(config: PureHttpRequestConfig) {
    return new Promise(resolve => {
      PureHttp.requests.push((token: string) => {
        config.headers["Authorization"] = formatToken(token);
        resolve(config);
      });
    });
  }

  /** 请求拦截 */
  private httpInterceptorsRequest(): void {
    PureHttp.axiosInstance.interceptors.request.use(
      async (config: PureHttpRequestConfig): Promise<any> => {
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if (typeof config.beforeRequestCallback === "function") {
          config.beforeRequestCallback(config);
          return config;
        }
        if (PureHttp.initConfig.beforeRequestCallback) {
          PureHttp.initConfig.beforeRequestCallback(config);
          return config;
        }
        /** 请求白名单，放置一些不需要`token`的接口（通过设置请求白名单，防止`token`过期后再请求造成的死循环问题） */
        const whiteList = ["/refresh-token", "/login"];
        return whiteList.some(url => config.url.endsWith(url))
          ? config
          : new Promise(resolve => {
              const data = getToken();
              if (data) {
                const now = new Date().getTime();
                const expired = parseInt(data.expires) - now <= 0;
                if (expired) {
                  if (!PureHttp.isRefreshing) {
                    PureHttp.isRefreshing = true;

                   let res = useUserStoreHook()
                      .handRefreshToken({ refreshToken: data.refreshToken });
                    PureHttp.isRefreshing = false;
                    const token = res['accessToken'];
                    config.headers["Authorization"] = formatToken(token);
                    PureHttp.requests.forEach(cb => cb(token));
                  }
                  resolve(PureHttp.retryOriginalRequest(config));
                } else {
                  config.headers["Authorization"] = formatToken(
                    data.accessToken
                  );
                  resolve(config);
                }
              } else {
                resolve(config);
              }
            });
      },
      error => {
        return Promise.reject(error);
      }
    );
  }



  /** 响应拦截 */
  private httpInterceptorsResponse(): void {
    const instance = PureHttp.axiosInstance;
    instance.interceptors.response.use(
      async (response: AxiosResponse<any>) => {
        let { data } = response
        const config = response.config
        if (!data) {
          // 返回“[HTTP]请求没有返回值”;
          throw new Error()
        }
        // 未设置状态码则默认成功状态
        // 二进制数据则直接返回，例如说 Excel 导出
        if (
          response.request.responseType === 'blob' ||
          response.request.responseType === 'arraybuffer'
        ) {
          // 注意：如果导出的响应为 json，说明可能失败了，不直接返回进行下载
          if (response.data.type !== 'application/json') {
            return response.data
          }
          data = await new Response(response.data).json()
        }
        const code = data.code || result_code
        // 获取错误信息
        const msg = data.msg || errorCode[code] || errorCode['default']
        if (ignoreMsgs.indexOf(msg) !== -1) {
          // 如果是忽略的错误码，直接返回 msg 异常
          return Promise.reject(msg)
        } else if (code === 401) {
          // 如果未认证，并且未进行刷新令牌，说明可能是访问令牌过期了
          if (!isRefreshToken) {
            isRefreshToken = true
            // 1. 如果获取不到刷新令牌，则只能执行登出操作
            if (!getRefreshToken()) {
              return useUserStoreHook().logOut()
            }
            // 2. 进行刷新访问令牌
            try {
              const refreshTokenRes = await refreshToken()
              // 2.1 刷新成功，则回放队列的请求 + 当前请求
              setToken((await refreshTokenRes).data.data)
              config.headers!.Authorization = 'Bearer ' + getAccessToken()
              requestList.forEach((cb: any) => {
                cb()
              })
              requestList = []
              return instance(config)
            } catch (e) {
              // 为什么需要 catch 异常呢？刷新失败时，请求因为 Promise.reject 触发异常。
              // 2.2 刷新失败，只回放队列的请求
              requestList.forEach((cb: any) => {
                cb()
              })
              // 提示是否要登出。即不回放当前请求！不然会形成递归
              return useUserStoreHook().logOut()
            } finally {
              requestList = []
              isRefreshToken = false
            }
          } else {
            // 添加到队列，等待刷新获取到新的令牌
            return new Promise((resolve) => {
              requestList.push(() => {
                config.headers!.Authorization = 'Bearer ' + getAccessToken() // 让每个请求携带自定义token 请根据实际情况自行修改
                resolve(instance(config))
              })
            })
          }
        } else if (code === 500) {
          ElMessage.error('请联系关系员！')
          return new Error(msg)
        } else if (code === 901) {
          ElMessage.error({
            offset: 300,
            dangerouslyUseHTMLString: true,
            message:
              '请联系通管理员'
          })
          return new Error(msg)
        } else if (code !== 200) {
          if (msg === '无效的刷新令牌') {
            // hard coding：忽略这个提示，直接登出
            console.log(msg)
            return useUserStoreHook().logOut()
          } else {
            ElNotification.error({ title: msg })
          }
          return Promise.reject('error')
        } else {
          return data

        }
      },

      (error: PureHttpError) => {
        const $error = error;
        $error.isCancelRequest = Axios.isCancel($error);
        // 所有的响应异常 区分来源为取消请求/非取消请求
        return Promise.reject(error);
      }
      )
}

  /** 通用请求工具函数 */
  public async request<T>(
   option
  ) {
    const { headersType, headers, ...otherOption } = option

    const config = {
      ...otherOption,
      url:base_url+option.url,
      'headers': {
        'Content-Type': headersType || 'application/json',
        ...headers
      }
    };

    let isToken = (config!.headers || {}).isToken === false
    if (getAccessToken() && !isToken) {
      config.headers['Authorization'] = 'Bearer ' +  getAccessToken() // 让每个请求携带自定义token
    }

    // 防止 GET 请求缓存
    if (config.method.toUpperCase() === 'GET') {
      config.headers['Cache-Control'] = 'no-cache'
      config.headers['Pragma'] = 'no-cache'
    }

    // 单独处理自定义请求/响应回调
    return await PureHttp.axiosInstance
      .request(config);
  }


  /** 单独抽离的`post`工具函数 */
  public async post<T, P>(
    option:any
  ) {
    let result = await this.request<T>({method:"post", ...option})
    return result['data'];
  }

  public async get<T>(
    option:any
  ) {
    let result = await this.request<T>({method:"get", ...option})
    return result['data'];
  }

  public async postOriginal<T>(
    option:any
  ) {
    let result = await this.request({ method: 'POST', ...option })
    return result;
  }

  public async delete<T>(
    option:any
  ) {
    let res = await this.request({ method: 'DELETE', ...option })
    return res.data as unknown as T
  }
  public async put<T>(
    option:any
  ) {
    let res = await this.request({ method: 'PUT', ...option })
    return res.data as unknown as T
  }

  public async download<T>(
    option:any
  ) {
    let res = await this.request({ method: 'GET',responseType: 'blob', ...option })
    return res as unknown as Promise<T>
  }

  public async upload<T>(
    option:any
  ) {
    option.headersType = 'multipart/form-data'
    let res = await this.request({ method: 'POST',responseType: 'blob', ...option })
    return res as unknown as Promise<T>
  }


  /** 单独抽离的`get`工具函数 */
 /* public get<T, P>(
    url: string,
    params?: AxiosRequestConfig<P>,
    config?: PureHttpRequestConfig
  ): Promise<T> {
    return this.request<T>("get", url, params, config);
  }*/
}

const refreshToken = async () => {
  return await Axios.post(base_url + '/system/auth/refresh-token?refreshToken=' + getRefreshToken())
}
export const http = new PureHttp();
