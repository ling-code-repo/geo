import {http as request} from "@/utils/http";

/** 扩展问题信息 */
export interface Question {
          id: number; // 问题id
          wordId?: number; // 主词id
          question?: string; // 问题
          status: number; // 收录状态
  }

// 扩展问题 API
export const QuestionApi = {
  // 查询扩展问题分页
  getQuestionPage: async (params: any) => {
    return await request.get({ url: `/geo/question/page`, params })
  },

  // 查询扩展问题详情
  getQuestion: async (id: number) => {
    return await request.get({ url: `/geo/question/get?id=` + id })
  },

  // 新增扩展问题
  createQuestion: async (data: Question) => {
    return await request.post({ url: `/geo/question/create`, data })
  },

  // 修改扩展问题
  updateQuestion: async (data: Question) => {
    return await request.put({ url: `/geo/question/update`, data })
  },

  // 删除扩展问题
  deleteQuestion: async (id: number) => {
    return await request.delete({ url: `/geo/question/delete?id=` + id })
  },

  /** 批量删除扩展问题 */
  deleteQuestionList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/question/delete-list?ids=${ids.join(',')}` })
  },

  // 导出扩展问题 Excel
  exportQuestion: async (params) => {
    return await request.download({ url: `/geo/question/export-excel`, params })
  },
}
