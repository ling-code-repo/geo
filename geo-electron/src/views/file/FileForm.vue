图片<template>
  <div>
    <Dialog v-model="dialogVisible" title="上传文件">
      <el-upload
        ref="uploadRef"
        v-model:file-list="fileList"
        :action="uploadUrl"
        :auto-upload="false"
        :data="data"
        :disabled="formLoading"
        :limit="maxUploadCount"
        :multiple="true"
        :on-change="handleFileChange"
        :on-error="submitFormError"
        :on-exceed="handleExceed"
        :on-progress="handleProgress"
        :on-success="submitFormSuccess"
        :on-remove="handleRemove"
        :before-upload="beforeUpload"
        :http-request="httpRequest"
        :accept="allowedExtensions"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">
            <span style="color: red">提示：仅允许导入 {{formatTypesDisplay}}格式文件！</span>
            <span style="color: #909399; margin-left: 10px">最多可上传 {{maxUploadCount}} 个文件</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button :disabled="formLoading" type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </template>
    </Dialog>
  </div>
</template>
<script lang="ts" setup>
import { useUpload } from '@/components/UploadFile/src/useGeoUpload'
import { UploadFile, UploadProgressEvent } from 'element-plus/es/components/upload/src/upload'
import { computed,ref,unref, watch } from 'vue'
import {useMessage} from '@/hooks/web/useMessage'
defineOptions({ name: 'GeoFileForm' })

const message = useMessage() // 消息弹窗

const category = ref('')

// 用于显示的文件格式文本
const formatTypesDisplay = computed(() => {
  if (category.value === 'GEO_KNOWLEDGE') {
    return 'PDF、DOC、DOCX、TXT、MD、PPT、PPTX、HTML'
  } else {
    return 'JPG、JPEG、PNG、GIF'
  }
})

// 允许的文件扩展名
const allowedExtensions = computed(() => {
  if (category.value === 'GEO_KNOWLEDGE') {
    return ['.pdf', '.doc', '.docx', '.txt', '.md', '.ppt', '.pptx', '.html']
  } else {
    return ['.jpg', '.jpeg', '.png', '.gif']
  }
})

// 验证文件类型
const validateFileType = (file: File): boolean => {
  const fileName = file.name.toLowerCase()
  const extension = '.' + fileName.split('.').pop()

  if (!allowedExtensions.value.includes(extension)) {
    message.error(`文件格式不支持！仅允许上传：${formatTypesDisplay.value}`)
    return false
  }
  return true
}

// 最大上传数量，根据类别区分
const maxUploadCount = 10

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const fileList = ref([]) // 文件列表
const data = ref({ path: '' })
const uploadRef = ref()
// const categoryValue = computed(() => category.value)
// const { uploadUrl, httpRequest } = useUpload(categoryValue.value)

// 上传配置的响应式引用
const uploadUrl = ref('')
const httpRequest = ref<any>(null)

// 监听category变化，更新上传配置
watch(category, (newCategory) => {
  if (newCategory) {
    const config = useUpload(newCategory)
    uploadUrl.value = config.uploadUrl
    httpRequest.value = config.httpRequest
  }
}, { immediate: false })

/** 打开弹窗 */
const open = async (cat: string) => {
  dialogVisible.value = true
  category.value = cat
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗



/** 处理上传的文件发生变化 */
const handleFileChange = (file: UploadFile, fileList: UploadFile[]) => {
  // 验证文件类型
  if (file.raw && !validateFileType(file.raw)) {
    // 如果文件类型不合法，从列表中移除
    const index = fileList.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      fileList.splice(index, 1)
    }
    return
  }

  // 更新路径，显示第一个文件名
  if (fileList.length > 0) {
    data.value.path = fileList.length > 1
      ? `${fileList[0].name} 等${fileList.length}个文件`
      : fileList[0].name
  }
}

/** 移除文件 */
const handleRemove = () => {
  // 更新路径显示
  if (fileList.value.length > 0) {
    data.value.path = fileList.value.length > 1
      ? `${fileList.value[0].name} 等${fileList.value.length}个文件`
      : fileList.value[0].name
  } else {
    data.value.path = ''
  }
}

/** 上传前验证 */
const beforeUpload = (file: File) => {
  return validateFileType(file)
}

/** 处理文件上传进度显示 */
const handleProgress = (upEvt: UploadProgressEvent, file: UploadFile) => {
  file.percentage = upEvt.percent
}

/** 提交表单 */
const submitFileForm = () => {
  if (fileList.value.length == 0) {
    message.error('请上传文件')
    return
  }
  formLoading.value = true
  unref(uploadRef)?.submit()
}

/** 文件上传成功处理 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitFormSuccess = () => {
  // 清理
  dialogVisible.value = false
  formLoading.value = false
  unref(uploadRef)?.clearFiles()
  // 提示成功，并刷新
  message.success('创建成功！')
  emit('success')
}

/** 上传错误提示 */
const submitFormError = (): void => {
  message.error('上传失败，请您重新上传！')
  formLoading.value = false
}

/** 重置表单 */
const resetForm = () => {
  // 重置上传状态和文件
  formLoading.value = false
  uploadRef.value?.clearFiles()
}

/** 文件数超出提示 */
const handleExceed = (): void => {
  message.error(`最多只能上传 ${maxUploadCount.value} 个文件！`)
}
</script>
