import { useGlobalSocket } from "@/composables/useGlobalSocket";
import { useLocalSocket } from "@/composables/useLocalSocket";
import { articlePublishService } from "@/composables/article-publish.service";

const {initGlobalSocket,globalConnected} = useGlobalSocket ()
const {initLocalSocket,localConnected} = useLocalSocket ()

// useLocalSocket.ts
export const setupInitListener = async () => {
  const initEvent = async () => {

    if (!globalConnected){
        initGlobalSocket()
    }

    if (!localConnected) {
      // 重新连接
        initLocalSocket()
    }

    if(!articlePublishService.initialized()){
      await articlePublishService.init()
    }
  }

  window.addEventListener('init-event', initEvent)

  return () => {
    window.removeEventListener('init-event', initEvent)
  }
}
