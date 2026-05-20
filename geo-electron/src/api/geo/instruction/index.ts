import {http as request} from "@/utils/http";

/** 创作指令信息 */
export interface Instruction {
          id: number; // id
          instructionName?: string; // 指令名称
          instructionType: number; // 指令类型
          content?: string; // 指令内容
  }

// 创作指令 API
export const InstructionApi = {
  // 查询创作指令分页
  getInstructionPage: async (params: any) => {
    return await request.get({ url: `/geo/instruction/page`, params })
  },

  // 查询创作指令详情
  getInstruction: async (id: number) => {
    return await request.get({ url: `/geo/instruction/get?id=` + id })
  },

  // 新增创作指令
  createInstruction: async (data: Instruction) => {
    return await request.post({ url: `/geo/instruction/create`, data })
  },

  // 修改创作指令
  updateInstruction: async (data: Instruction) => {
    return await request.put({ url: `/geo/instruction/update`, data })
  },

  // 删除创作指令
  deleteInstruction: async (id: number) => {
    return await request.delete({ url: `/geo/instruction/delete?id=` + id })
  },

  /** 批量删除创作指令 */
  deleteInstructionList: async (ids: number[]) => {
    return await request.delete({ url: `/geo/instruction/delete-list?ids=${ids.join(',')}` })
  },

  // 导出创作指令 Excel
  exportInstruction: async (params) => {
    return await request.download({ url: `/geo/instruction/export-excel`, params })
  },
  getSimpleInstructionList: async () => {
  return request.get({ url: '/geo/instruction/simple-list' })
}
}
