import {http as request} from "@/utils/http";

/** 授权账号信息 */
export interface Account {
          publishStatus: number; // 发布状态 0不发布 1需要发布
          publishCount: number; // 每日发布次数
          proxy: string; // ip:port
          account: string; // username:password
          authorizeStatus: number; // 授权状态 0未授权 1已授权
  }

export interface AccountInfo {
  name: string;
  avatar: string;
  value: number;
}

// 授权账号 API
export const AccountApi = {
  // 查询授权账号分页
  getAccountPage: async (params: any) => {
    return await request.get({ url: `/geo/account/page`, params })
  },

  // 查询授权账号详情
  getAccount: async (id: number) => {
    return await request.get({ url: `/geo/account/get?id=` + id })
  },

  saveOrUpdate: async (data: AccountInfo) => {
    return await request.post({ url: `/geo/account/saveOrUpdate`, data })
  },

  // 修改授权账号
  updateAccount: async (data: Account) => {
    return await request.put({ url: `/geo/account/update`, data })
  },

  // 删除授权账号
  deleteAccount: async (id: number) => {
    return await request.delete({ url: `/geo/account/delete?id=` + id })
  },

  /** 批量删除授权账号 */
  deleteAccountList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/account/delete-list?ids=${ids.join(',')}` })
  },

  // 导出授权账号 Excel
  exportAccount: async (params) => {
    return await request.download({ url: `/geo/account/export-excel`, params })
  },
}
