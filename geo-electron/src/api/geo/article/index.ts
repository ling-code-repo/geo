import {http as request} from "@/utils/http";

/** 创作文章信息 */
export interface Article {
          id: number; // id
          title: string; // 文章标题
          pictureIds: string; // 文章标题
          contentMarkdown: string; // 文章内容
  }

// 创作文章 API
export const ArticleApi = {
  // 查询创作文章分页
  getArticlePage: async (params: any) => {
    return await request.get({ url: `/geo/article/page`, params })
  },

  // 查询创作文章详情
  getArticle: async (id: number) => {
    return await request.get({ url: `/geo/article/get?id=` + id })
  },

  // 新增创作文章
  createArticle: async (data: Article) => {
    return await request.post({ url: `/geo/article/create`, data })
  },

  // 修改创作文章
  updateArticle: async (data: Article) => {
    return await request.put({ url: `/geo/article/update`, data })
  },

  // 删除创作文章
  deleteArticle: async (id: number) => {
    return await request.delete({ url: `/geo/article/delete?id=` + id })
  },

  /** 批量删除创作文章 */
  deleteArticleList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/article/delete-list?ids=${ids.join(',')}` })
  },

  // 导出创作文章 Excel
  exportArticle: async (params) => {
    return await request.download({ url: `/geo/article/export-excel`, params })
  },
  getSimpleArticleList : async () => {
    return request.get({ url: '/geo/article/simple-list' })
  }
}
