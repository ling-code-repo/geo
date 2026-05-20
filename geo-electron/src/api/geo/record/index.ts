import {http as request} from "@/utils/http";
import type { Dayjs } from 'dayjs';

/** 发布记录信息 */
export interface PublishRecord {
  }

// 发布记录 API
export const PublishRecordApi = {
  // 查询发布记录分页
  getPublishRecordPage: async (params: any) => {
    return await request.get({ url: `/geo/publish-record/page`, params })
  },

  // 查询发布记录详情
  getPublishRecord: async (id: number) => {
    return await request.get({ url: `/geo/publish-record/get?id=` + id })
  },

  // 新增发布记录
  createPublishRecord: async (data: PublishRecord) => {
    return await request.post({ url: `/geo/publish-record/create`, data })
  },

  // 修改发布记录
  updatePublishRecord: async (data: PublishRecord) => {
    return await request.put({ url: `/geo/publish-record/update`, data })
  },

  // 删除发布记录
  deletePublishRecord: async (id: number) => {
    return await request.delete({ url: `/geo/publish-record/delete?id=` + id })
  },

  /** 批量删除发布记录 */
  deletePublishRecordList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/publish-record/delete-list?ids=${ids.join(',')}` })
  },

  // 导出发布记录 Excel
  exportPublishRecord: async (params) => {
    return await request.download({ url: `/geo/publish-record/export-excel`, params })
  },
}
