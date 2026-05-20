import {http as request} from "@/utils/http";

/** 蒸馏词信息 */
export interface Word {
  id?:string,
          word?: string; // 主词
          target?: string; // 转化目标
          questions?: [string]; // 转化目标
          optimized: number; // 是否优化
          expand: number; // 是否查询拓展词
  }

/** 蒸馏词信息 */
export interface DistillWord {
  word: string; // 主词
  target: string; // 转化目标
}

export interface SimpleWord {
  id?:string,
  word?: string; // 主词
  label?: string; // 转化目标
  questionCount: number; // 是否优化
}

// 蒸馏词 API
export const WordApi = {
  // 查询蒸馏词分页
  getWordPage: async (params: any) => {
    return await request.get({ url: `/geo/word/page`, params })
  },

  // 查询蒸馏词详情
  getWord: async (id: number) => {
    return await request.get({ url: `/geo/word/get?id=` + id })
  },

  // 新增蒸馏词
  createWord: async (data: Word) => {
    return await request.post({ url: `/geo/word/create`, data })
  },

  // 修改蒸馏词
  updateWord: async (data: Word) => {
    return await request.put({ url: `/geo/word/update`, data })
  },

  // 删除蒸馏词
  deleteWord: async (id: number) => {
    return await request.delete({ url: `/geo/word/delete?id=` + id })
  },

  /** 批量删除蒸馏词 */
  deleteWordList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/word/delete-list?ids=${ids.join(',')}` })
  },

  // 导出蒸馏词 Excel
  exportWord: async (params) => {
    return await request.download({ url: `/geo/word/export-excel`, params })
  },
  getSimpleWordList: async () => {
    return request.get({ url: '/geo/word/simple-list' })
  },
  distill: async (data: DistillWord) => {
    return await request.post({ url: `/geo/word/distill`, data,timeout: 1000 * 60 * 2 })
  }
}
