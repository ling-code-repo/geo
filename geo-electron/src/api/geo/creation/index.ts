import {http as request} from "@/utils/http";

/** AI创作信息 */
export interface Creation {
          id: number; // id
          name?: string; // 创作名称
          wordId?: number; // 蒸馏词id
          count: number; // 创作文章数量
          knowledgeId: number; // 知识库id
          pictureIds?: string; // 图片id
          pictureIdList?: [number]; // 图片id
          pictureCount: number; // 配图数量
          contentId: number; // 内容指令id
          titleId: number; // 标题指令id
  }

// AI创作 API
export const CreationApi = {
  // 查询AI创作分页
  getCreationPage: async (params: any) => {
    return await request.get({ url: `/geo/creation/page`, params })
  },

  // 查询AI创作详情
  getCreation: async (id: number) => {
    return await request.get({ url: `/geo/creation/get?id=` + id })
  },

  // 新增AI创作
  createCreation: async (data: Creation) => {
    return await request.post({ url: `/geo/creation/create`, data })
  },

  // 修改AI创作
  updateCreation: async (data: Creation) => {
    return await request.put({ url: `/geo/creation/update`, data })
  },

  // 删除AI创作
  deleteCreation: async (id: number) => {
    return await request.delete({ url: `/geo/creation/delete?id=` + id })
  },

  /** 批量删除AI创作 */
  deleteCreationList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/creation/delete-list?ids=${ids.join(',')}` })
  },

  // 导出AI创作 Excel
  exportCreation: async (params) => {
    return await request.download({ url: `/geo/creation/export-excel`, params })
  },
}
