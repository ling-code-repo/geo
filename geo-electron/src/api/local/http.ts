import Axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type CustomParamsSerializer,
} from "axios";
import { stringify } from "qs";
import { ref,onMounted } from "vue";
import { getLocalServerApi } from "@/utils/electronAPI";

const base_url = ref('')

// 获取 Python 服务地址的函数

// const base_url =
//   import.meta.env.VITE_LOCAL_BASE_URL + import.meta.env.VITE_LOCAL_API_URL;
// 相关配置请参考：www.axios-js.com/zh-cn/docs/#axios-request-config-1
const defaultConfig: AxiosRequestConfig = {
  // 请求超时时间
  timeout: 10000,
  headers: {
    Accept: "application/json, text/plain, */*",
    "Content-Type": "application/json",
    "X-Requested-With": "XMLHttpRequest",
  },
  // 数组格式参数序列化（https://github.com/axios/axios/issues/5142）
  paramsSerializer: {
    serialize: stringify as unknown as CustomParamsSerializer,
  },
};

class PureHttp {
  constructor() {}

  /** 保存当前`Axios`实例对象 */
  private static axiosInstance: AxiosInstance = Axios.create(defaultConfig);

  /** 通用请求工具函数 */
  public async request<T>(option) {
    const { headersType, headers, ...otherOption } = option;
    if (base_url.value === ''){
      base_url.value = await getLocalServerApi()
    }
    const config = {
      ...otherOption,
      url: base_url.value + option.url,
      headers: {
        "Content-Type": headersType || "application/json",
        ...headers,
      },
    };

    // 防止 GET 请求缓存
    if (config.method.toUpperCase() === "GET") {
      config.headers["Cache-Control"] = "no-cache";
      config.headers["Pragma"] = "no-cache";
    }

    // 单独处理自定义请求/响应回调
    return await PureHttp.axiosInstance.request(config);
  }

  /** 单独抽离的`post`工具函数 */
  public async post<T, P>(option: any) {
    return await this.request<T>({ method: "post", ...option });
  }

  public async get<T>(option: any) {
    return await this.request<T>({ method: "get", ...option });
  }
}


export const http = new PureHttp();
