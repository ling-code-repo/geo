import { ref } from 'vue'
import { useGlobalSocket } from '@/composables/useGlobalSocket'
import {
  useLocalSocket,
} from '@/composables/useLocalSocket'
import { useMessage } from '@/hooks/web/useMessage'
import {  executePublish } from "@/api/local";
import { client_id_key } from "@/utils/constants";
import { PublishTaskApi} from '@/api/geo/publish'
import { getDataPath } from "@/utils/electronAPI";


/**
 * 文章发布服务
 * 作为服务端 WebSocket 和本地 WebSocket 的桥梁
 * - 从服务端接收文章内容
 * - 发送到本地 Python 程序
 * - 监听发布完成状态并通知页面刷新
 */
class ArticlePublishService {
  private globalSocket = useGlobalSocket()
  private localSocket = useLocalSocket()
  private message = useMessage()
  private isInitialized = ref(false)
  private processingTasks = new Map<number, any>() // 正在处理的任务
  private localUnsubscribers: Array<() => void> = [] // 本地事件订阅取消函数


  initialized() {
    return this.isInitialized.value
  }
  /**
   * 初始化服务
   * 在登录成功后调用
   */
  async init() {
    if (this.isInitialized.value) {
      // console.log('[ArticlePublishService] 服务已初始化，跳过')
      return
    }


    try {
      // 订阅服务端 WebSocket 消息
      this.subscribeServerEvents()

      // 订阅本地 WebSocket 消息
      this.subscribeLocalEvents()




      this.isInitialized.value = true
    } catch (error) {
      throw error
    }
  }

  /**
   * 订阅服务端 WebSocket 事件
   */
  private  subscribeServerEvents()  {
    this.globalSocket.subscribeGlobalEvent('geo-publish-message', async (data)  => {
      const {userDataPath} = await getDataPath()
      data.dataDir = userDataPath
      await this.handleArticlePublishTask(data)
    })
  }

  /**
   * 订阅本地 WebSocket 事件
   */
  private subscribeLocalEvents() {


    this.localSocket.subscribeLocalEvent( "ws:publish_task", async (data) => {
      const { timestamp, id, platform, code } = data;
      if (code != 1) {
        this.message.error(platform + "发布失败！");
      } else {
        this.message.success(platform + "发布成功！");
      }
      await PublishTaskApi.updateRecord(data)
    });
  }

  /**
   * 处理文章发布任务
   * 从服务端接收任务，发送到本地 Python 程序
   */
  private async handleArticlePublishTask(data: any) {
    // console.log("handleArticlePublishTask",data)
    data.clientId = client_id_key
    const result = await executePublish(data);
    if (result.code != 1) {
      this.message.error(result.message);
    } else {
      this.message.success(result.message);
    }
  }

  /**
   * 处理任务取消
   */
  private handleTaskCancel(data: any) {

  }

  /**
   * 处理发布完成
   * 从本地 Python 程序接收完成状态，通知页面刷新
   */
  private handlePublishComplete(data: any) {

  }

  /**
   * 处理发布错误
   */
  private handlePublishError(data: any) {

  }

  /**
   * 处理发布进度
   */
  private handlePublishProgress(data: any) {
    // console.log('[ArticlePublishService] 发布进度:', data)
    //
    // const { taskId, progress, message } = data
    //
    // // 更新任务进度
    // const task = this.processingTasks.get(taskId)
    // if (task) {
    //   task.progress = progress
    // }
    //
    // // 通知 publish 页面更新进度
    // this.notifyPageRefresh('publish-progress', {
    //   taskId,
    //   progress,
    //   message
    // })
  }

  /**
   * 发送消息到本地 Python 程序
   */
  private sendToLocalPython(message: any) {

  }

  /**
   * 通知页面刷新
   * 通过 global-socket 的事件分发机制
   */
  private notifyPageRefresh(eventType: string, data: any) {

  }

  /**
   * 手动触发文章发布
   * 用于测试或手动触发场景
   */
  async publishArticle(article: any, platform: string) {

  }



  /**
   * 销毁服务
   * 退出登录时调用
   */
  destroy() {

  }
}

// 导出单例
export const articlePublishService = new ArticlePublishService()
