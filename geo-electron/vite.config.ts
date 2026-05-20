import { rmSync } from "node:fs";
import { getPluginsList } from "./build/plugins";
import { include, exclude } from "./build/optimize";
import { type UserConfigExport, type ConfigEnv, loadEnv } from "vite";
import {
  root,
  alias,
  wrapperEnv,
  pathResolve,
  __APP_INFO__
} from "./build/utils";
import  portfinder  from 'portfinder';
export default async ({ command, mode }: ConfigEnv): Promise<UserConfigExport> => {
  const lifecycle = process.env.npm_lifecycle_event;
  if (!lifecycle.includes("browser")) {
    rmSync("dist-electron", { recursive: true, force: true });
  }
  const { VITE_CDN, VITE_PORT, VITE_COMPRESSION, VITE_PUBLIC_PATH } =
    wrapperEnv(loadEnv(mode, root));
  const availablePort = await portfinder.getPortPromise({
    port: 49152, // 起始端口
    stopPort: 65535 // 最大端口范围
  })
  return {
    base: VITE_PUBLIC_PATH,
    root,
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `
          $elNamespace: 'el' !default;
          $namespace: 'el' !default;
        `
        }
      }
    },
    resolve: {
      alias
    },
    // 服务端渲染
    server: {
      // 端口号
      // port: VITE_PORT,
      port: availablePort,
      host: "0.0.0.0",
      // 本地跨域代理 https://cn.vitejs.dev/config/server-options.html#server-proxy
      proxy: {},
      // 预热文件以提前转换和缓存结果，降低启动期间的初始页面加载时长并防止转换瀑布
      warmup: {
        clientFiles: ["./index.html", "./src/{views,components}/*"]
      }
    },
    plugins: getPluginsList(command, VITE_CDN, VITE_COMPRESSION),
    // https://cn.vitejs.dev/config/dep-optimization-options.html#dep-optimization-options
    optimizeDeps: {
      include,
      exclude
    },
    build: {
      // https://cn.vitejs.dev/guide/build.html#browser-compatibility
      target: "es2015",
      sourcemap: false,
      // 消除打包大小超过500kb警告
      chunkSizeWarningLimit: 4000,
      rollupOptions: {
        input: {
          index: pathResolve("./index.html", import.meta.url)
        },
        // 静态资源分类打包
        output: {
          chunkFileNames: "static/js/[name]-[hash].js",
          entryFileNames: "static/js/[name]-[hash].js",
          assetFileNames: "static/[ext]/[name]-[hash].[ext]"
        }
      }
    },
    define: {
      __INTLIFY_PROD_DEVTOOLS__: false,
      __APP_INFO__: JSON.stringify(__APP_INFO__)
    }
  };
};
