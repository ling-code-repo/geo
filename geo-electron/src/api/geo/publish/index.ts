import {http as request} from "@/utils/http";

/** 发布任务信息 */
export interface PublishTask {
          id: number; // id
          name?: string; // 任务名
          declareAi: number; // 声明AI创作
          publishCount: number; // 最大发布次数
          articleId?: number; // 文章id
          platforms?: string; // 平台
  }

// 发布任务 API
export const PublishTaskApi = {
  // 查询发布任务分页
  getPublishTaskPage: async (params: any) => {
    return await request.get({ url: `/geo/publish-task/page`, params })
  },

  // 查询发布任务详情
  getPublishTask: async (id: number) => {
    return await request.get({ url: `/geo/publish-task/get?id=` + id })
  },

  // 新增发布任务
  createPublishTask: async (data: PublishTask) => {
    return await request.post({ url: `/geo/publish-task/create`, data })
  },

  // 修改发布任务
  updatePublishTask: async (data: PublishTask) => {
    return await request.put({ url: `/geo/publish-task/update`, data })
  },

  // 删除发布任务
  deletePublishTask: async (id: number) => {
    return await request.delete({ url: `/geo/publish-task/delete?id=` + id })
  },

  /** 批量删除发布任务 */
  deletePublishTaskList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/publish-task/delete-list?ids=${ids.join(',')}` })
  },

  // 导出发布任务 Excel
  exportPublishTask: async (params) => {
    return await request.download({ url: `/geo/publish-task/export-excel`, params })
  },
  executePublishTask: async (data) => {
    return await request.post({ url: `/geo/publish-task/execute`, data })
  },

  updateRecord: async (data) => {
    return await request.post({ url: `/geo/publish-task/update_record`, data })
  },

}
