import {http as request} from "@/utils/http";
export interface UserData {
  username: string
  password: string
}
export const GeoUserApi = {

  // 新增发布任务
  register: async (data: UserData) => {
    return await request.post({ url: '/geo/user/register', data })
  },

}
