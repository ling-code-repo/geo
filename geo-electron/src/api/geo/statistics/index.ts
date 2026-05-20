import {http as request} from "@/utils/http";


// 发布记录 API
export const StatisticsApi = {
  // 查询发布记录分页
  getMetric: async () => {
    return await request.get({ url: `/geo/statistics/metric` })
  },

  getArticle: async () => {
    return await request.get({ url: `/geo/statistics/article` })
  },

  getRecord: async () => {
    return await request.get({ url: `/geo/statistics/record` })
  },

}
