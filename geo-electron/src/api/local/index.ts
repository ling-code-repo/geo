import {http as request} from "./http";

export const accountAuthorization = async (data: any):Promise<any> => {
  const rst = await request.post({ url: '/account/authorization', data })
  return new Promise<any>((resolve, reject) => {
    resolve(rst.data)
  })
}

export const executePublish = async (data: any) => {
  const rst = await request.post({ url: '/publish/execute', data })
  return rst.data
}
