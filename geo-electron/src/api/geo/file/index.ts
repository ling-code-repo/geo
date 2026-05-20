import {http as request} from "@/utils/http";

// 文件预签名地址 Response VO
export interface FilePresignedUrlRespVO {
  // 文件配置编号
  configId: number
  // 文件上传 URL
  uploadUrl: string
  // 文件 URL
  url: string
  // 文件路径
  path: string
}


export interface SimpleFile {
  id: number
  name: string
  label: string
  url: string
  category: string
}

// 查询文件列表
export const getFilePage = (params: any) => {
  return request.get({ url: '/geo/file/page', params })
}

// 批量删除文件
export const deleteFileList = (ids: number[]) => {
  return request.delete({ url: '/geo/file/delete-list', params: { ids: ids.join(',') } })
}

// 创建文件
export const createFile = (data: any) => {
  return request.post({ url: '/geo/file/create', data })
}

// 上传文件
export const updateFile = (data: any, onUploadProgress?: Function) => {
  return request.upload({ url: '/geo/file/upload', data, onUploadProgress })
}

export const getSimpleFileList = () => {
  return request.get({ url: '/geo/file/simple-list' })
}
