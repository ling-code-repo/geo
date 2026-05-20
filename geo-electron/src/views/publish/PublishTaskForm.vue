<template>
  <div>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="任务名" prop="name">
        <el-input :disabled="formData.taskStatus && formData.taskStatus != 0" v-model="formData.name" placeholder="请输入任务名" />
      </el-form-item>


      <el-form-item label="文章" prop="articleId">
        <el-select :disabled="formData.taskStatus && formData.taskStatus != 0" v-model="formData.articleId" placeholder="请选择文章">
          <el-option
            v-for="dict in articleList"
            :key="dict.id"
            :label="dict.title"
            :value="dict.id!"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platformList">
        <el-checkbox-group
          :disabled="formData.taskStatus && formData.taskStatus != 0"
          v-model="formData.platformList"
          size="small"
          class="platform-group"
        >
          <!-- 每行显示5个平台 -->
          <div v-for="(row, rowIndex) in platformRows" :key="rowIndex" class="checkbox-row">
            <el-checkbox-button
              v-for="platform in row"
              :key="platform.id"
              :value="platform.id"
              class="platform-item"
            >
              <div class="platform-content">
                <img
                  alt=""
                  class="message-icon"
                  :src="platform.icon"
                />
                <span>{{ platform.type }}</span>
              </div>
            </el-checkbox-button>
          </div>
        </el-checkbox-group>
<!--        <el-input v-model="formData.index" placeholder="请输入平台" />-->
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group :disabled="formData.taskStatus && formData.taskStatus != 0" v-model="formData.status">
          <el-radio
            v-for="dict in publishStatusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-radio-group>
      </el-form-item>

      <el-form-item label="AI声明" prop="declareAi">
        <el-radio-group v-model="formData.declareAi">
          <el-radio
            v-for="dict in declareAiDictList"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-radio-group>
      </el-form-item>
      <el-form-item label="浏览模式" prop="headless">
        <el-radio-group v-model="formData.headless">
          <el-radio
            v-for="dict in publishViewOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-radio-group>
      </el-form-item>

    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  </div>
</template>
<script setup lang="ts">
import { PublishTaskApi, PublishTask } from '@/api/geo/publish'
import { ArticleApi, Article } from '@/api/geo/article'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { platforms } from "@/utils/geo/platform";

import {useMessage} from '@/hooks/web/useMessage'
import {ref,reactive,computed} from 'vue'
import { title } from "@/utils/utils";
/** 发布任务 表单 */
defineOptions({ name: 'PublishTaskForm' })

// 修改 platforms 结构，添加 name 字段
const enhancedPlatforms = platforms.map(platform => ({
  ...platform
}))

// 将 platforms 数组按每行5个分组
const platformRows = computed(() => {
  const rows = []
  const itemsPerRow = 5

  for (let i = 0; i < enhancedPlatforms.length; i += itemsPerRow) {
    rows.push(enhancedPlatforms.slice(i, i + itemsPerRow))
  }

  return rows
})

const publishStatusOptions = computed(() => {
  const options = getIntDictOptions(DICT_TYPE.GEO_PUBLISH_STATUS) || []

  // 如果当前没有选中值且选项不为空，设置默认值
  if (!formData.value.status && options.length > 0) {
    formData.value.status = options[0].value
  }

  return options
})

const publishViewOptions = computed(() => {
  const options = getIntDictOptions(DICT_TYPE.GEO_PUBLISH_VIEW_MODE) || []

  // 如果当前没有选中值且选项不为空，设置默认值
  if (!formData.value.headless && options.length > 0) {
    formData.value.headless = options[0].value
  }

  return options
})

const message = useMessage() // 消息弹窗

const declareAiDictList = ref()

// declareAiDicts
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  declareAi: undefined,
  articleId: undefined,
  status: undefined,
  headless: undefined,
  taskStatus: undefined,
  platformList: undefined,
})
const formRules = reactive({
  name: [{ required: true, message: '任务名不能为空', trigger: 'blur' }],
  articleId: [{ required: true, message: '文章id不能为空', trigger: 'blur' }],
  platformList: [{ required: true, message: '平台不能为空', trigger: 'blur' }],

})
const formRef = ref() // 表单 Ref
const articleList = ref([] as Article[])
/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = title(type)
  formType.value = type
  resetForm()
  declareAiDictList.value = getIntDictOptions(DICT_TYPE.GEO_DECLARE_AI)
  formData.value.declareAi = declareAiDictList.value[0]['value']
  articleList.value = await ArticleApi.getSimpleArticleList()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await PublishTaskApi.getPublishTask(id)
      formData.value.platformList = formData.value['platforms'].split(',').map(Number)
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
    const data = formData.value as unknown as PublishTask
    data.platforms = data['platformList']?.join(",")
    if (formType.value === 'create') {
      await PublishTaskApi.createPublishTask(data)
      message.success('创建成功！')
    } else {
      await PublishTaskApi.updatePublishTask(data)
      message.success('更新成功！')
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: undefined,
    status: undefined,
    headless: undefined,
    taskStatus: undefined,
    declareAi: undefined,
    articleId: undefined,
    platformList: undefined,
  }
  formRef.value?.resetFields()
}
</script>
<style scoped>
/*.platform-content {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 6px !important;
  line-height: 1;
  width: 100px;
  height: 35px;
}

.message-icon {
  vertical-align: middle; !* 图片垂直居中 *!
}

.platform-content span {
  display: inline-block;
  vertical-align: middle; !* 文字垂直居中 *!
}*/
/* 平台复选框组容器 */
.platform-group {
  width: 100%;
}

/* 每一行容器 */
.checkbox-row {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px; /* 多选框之间的间隙 */
  margin-bottom: 8px; /* 行与行之间的间隙 */
}

/* 每个平台项目 */
.platform-item {
  flex: 1; /* 让每个项目平均分配空间 */
  min-width: 0; /* 防止内容溢出 */
}

/* 覆盖 Element UI 的复选框按钮样式 */
:deep(.platform-item .el-checkbox-button__inner) {
  width: 100% !important;
  border-radius: 8px !important; /* 四个角都是圆角 */
  border: 1px solid #dcdfe6 !important; /* 四周都有框线 */
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  padding: 6px 4px !important;
  margin: 0 !important; /* 移除默认margin */
  background: #fff !important;
  transition: all 0.2s ease !important;
  box-shadow: none !important;
}

/* 悬停效果 */
:deep(.platform-item:not(.is-disabled) .el-checkbox-button__inner:hover) {
  border-color: #409EFF !important;
  background-color: #f5f9ff !important;
}

/* 选中状态 */
:deep(.platform-item.is-checked .el-checkbox-button__inner) {
  border-color: #409EFF !important;
  background-color: #ecf5ff !important;
  color: #409EFF !important;
}

/* 选中状态的圆角也要保持 */
:deep(.platform-item.is-checked .el-checkbox-button__inner) {
  border-radius: 8px !important;
}

/* 平台内容容器 */
.platform-content {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 6px !important;
  width: 100%;
}

/* 图标样式 */
.message-icon {
  width: 20px !important;
  height: 20px !important;
  object-fit: contain;
  flex-shrink: 0;
}

/* 文字样式 */
.platform-content span {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 响应式设计：在小屏幕上每行显示更少的项目 */
@media (max-width: 1200px) {
  .checkbox-row {
    display: grid;
    grid-template-columns: repeat(3, 1fr); /* 每行3个 */
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .checkbox-row {
    grid-template-columns: repeat(1, 1fr); /* 每行2个 */
  }
}


</style>
