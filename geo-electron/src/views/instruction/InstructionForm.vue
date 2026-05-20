<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="指令名称" prop="instructionName">
        <el-input v-model="formData.instructionName" placeholder="请输入指令名称" />
      </el-form-item>
      <el-form-item label="指令类型" prop="instructionType">
        <el-radio-group v-model="formData.instructionType">
          <!-- works when >=2.6.0, recommended ✔️ not work when <2.6.0 ❌ -->
          <el-radio
            v-for="dict in instructionTypes"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-radio-group>
      </el-form-item>
<!--      <el-form-item label="文章" prop="test_article">
        <Editor v-model="formData.test_article" height="150px" />
      </el-form-item>-->
      <el-form-item label="指令内容" prop="content">
        <el-input
          v-model="formData.content"
          :autosize="{ minRows: 12, maxRows: 12 }"
          type="textarea"
          placeholder="请输入指令"
        />
      </el-form-item>
      <el-form-item label="">
        <el-button @click="resetInstruction(1)" type="primary" :disabled="formLoading"
          >默认文章指令</el-button
        >
        <el-button @click="resetInstruction(2)" type="success" :disabled="formLoading"
          >默认标题指令</el-button
        >
        <el-button @click="resetInstruction(3)" type="danger" :disabled="formLoading"
          >默认复刻指令</el-button
        >
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { InstructionApi, Instruction } from '@/api/geo/instruction'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import article_instruction from '@/assets/geo/article_instruction.txt?raw'
import replica_instruction from '@/assets/geo/replica_instruction.txt?raw'
import title_instruction from '@/assets/geo/title_instruction.txt?raw'
import {useMessage} from '@/hooks/web/useMessage'
import {ref,reactive,onMounted} from 'vue'
import { title } from "@/utils/utils";
/** 创作指令 表单 */
defineOptions({ name: 'InstructionForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  instructionName: undefined,
  test_article: undefined,
  instructionType: undefined,
  content: ''
})
const formRules = reactive({
  instructionName: [{ required: true, message: '指令名称不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '指令内容不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

const instructionTypes = ref()

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = title( type);
  formType.value = type
  resetForm()
  instructionTypes.value = getIntDictOptions(DICT_TYPE.GEO_INSTRUCTION_TYPE)
  formData.value.instructionType = instructionTypes.value[0]['value']
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await InstructionApi.getInstruction(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as Instruction

    if (formType.value === 'create') {
      await InstructionApi.createInstruction(data)
      message.success('创建成功！')
    } else {
      await InstructionApi.updateInstruction(data)
      message.success('更新成功！')
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetInstruction = (type) => {
  if (type == 1) {
    formData.value.content = article_instruction
  } else if (type == 2) {
    formData.value.content = title_instruction
  } else if (type == 3) {
    formData.value.content = replica_instruction
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    instructionName: undefined,
    instructionType: undefined,
    content: ''
  }
  formRef.value?.resetFields()
}
</script>
