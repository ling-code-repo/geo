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
      <el-form-item label="创作名称" prop="name">
        <el-input :disabled="formData.id != null" v-model="formData.name" placeholder="请输入创作名称" />
      </el-form-item>
      <el-form-item label="主词" prop="wordId">
        <el-select v-model="formData.wordId" placeholder="请选择主词">
          <el-option
            v-for="dict in wordList"
            :key="dict.id"
            :label="dict.label"
            :value="dict.id!"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="内容指令" prop="contentId">
        <el-select v-model="formData.contentId" placeholder="请选择内容指令">
          <el-option
            v-for="dict in articleList"
            :key="dict.id"
            :label="dict.instructionName"
            :value="dict.id!"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标题指令" prop="titleId">
        <el-select v-model="formData.titleId" placeholder="请选择标题指令">
          <el-option
            v-for="dict in titleList"
            :key="dict.id"
            :label="dict.instructionName"
            :value="dict.id!"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创作数量" prop="count">
        <el-input v-model.number="formData.count" type="number" min="1" max="10"   placeholder="请输入创作文章数量" />
      </el-form-item>
      <el-form-item label="图片" prop="pictureIdList">
        <el-button @click="openPictureDialog">选择图片</el-button>
        <div class="selected-pictures" v-if="selectedPictures.length > 0">
          <div
            v-for="pic in selectedPictures"
            :key="pic.id"
            class="picture-item"
          >
            <img :src="pic.url" :alt="pic.label" />
            <div class="picture-label" :title="pic.label">{{ pic.label }}</div>
            <el-icon class="remove-icon" @click="removePicture(pic.id)">
              <Close />
            </el-icon>
          </div>
        </div>
        <div class="picture-hint" v-else>已选择 0/5 张图片</div>
        <div class="picture-hint" v-if="selectedPictures.length > 0">
          已选择 {{ selectedPictures.length }}/5 张图片
        </div>
      </el-form-item>
<!--      <el-form-item label="配图数量" prop="pictureCount">-->
<!--        <el-input type="number" :min="1" :max="10" v-model.number="formData.pictureCount" placeholder="请输入配图数量" />-->
<!--      </el-form-item>-->
      <el-form-item label="知识库" prop="knowledgeId">
        <el-select v-model="formData.knowledgeId" placeholder="请选择知识库">
          <el-option
            v-for="dict in knowledgeList"
            :key="dict.id"
            :label="dict.label"
            :value="dict.id!"
          />
        </el-select>
      </el-form-item>



    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>

  <!-- 图片选择弹框 -->
  <el-dialog
    v-model="pictureDialogVisible"
    title="选择图片"
    width="70%"
    :close-on-click-modal="false"
  >
    <div class="picture-dialog-content">
      <div class="picture-grid">
        <div
          v-for="pic in pictureList"
          :key="pic.id"
          :class="['picture-card', { selected: isPictureSelected(pic.id) }]"
          @click="togglePicture(pic)"
        >
          <img :src="pic.url" :alt="pic.label" />
          <div class="picture-card-label" :title="pic.label">{{ pic.label }}</div>
          <div v-if="isPictureSelected(pic.id)" class="check-mark">
            <el-icon><Check /></el-icon>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <span class="selection-count">已选择 {{ tempSelectedPictures.length }}/5 张</span>
        <el-button @click="pictureDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmPictureSelection">确 定</el-button>
      </span>
    </template>
  </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { CreationApi, Creation } from '@/api/geo/creation'
import { WordApi,  SimpleWord } from '@/api/geo/word'
import { InstructionApi,  Instruction } from '@/api/geo/instruction'
import { getSimpleFileList, SimpleFile } from '@/api/geo/file'
import { useMessage } from "@/hooks/web/useMessage";
import { ref, reactive, onMounted, computed } from "vue";
import { title } from "@/utils/utils";
import { Check, Close } from '@element-plus/icons-vue';
/** AI创作 表单 */
defineOptions({ name: 'CreationForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  wordId: undefined,
  count: undefined,
  knowledgeId: undefined,
  pictureIdList: undefined,
  pictureCount: undefined,
  contentId: undefined,
  titleId: undefined,
})
const formRules = reactive({
  name: [{ required: true, message: '创作名称不能为空', trigger: 'blur' }],
  wordId: [{ required: true, message: '蒸馏词不能为空', trigger: 'blur' }],
  pictureIdList: [{ required: true, message: '图片不能为空', trigger: 'blur' }],
  contentId: [{ required: true, message: '内容指令不能为空', trigger: 'blur' }],
  titleId: [{ required: true, message: '标题指令不能为空', trigger: 'blur' }],
  count: [{ required: true, message: '创作数量不能为空', trigger: 'blur' },{
    type: 'number',
    min: 1,
    max: 10,
    message: '创作数量必须在1-10之间',
    trigger: 'blur'
  }],
  pictureCount: [{
  type: 'number',
  min: 1,
  max: 10,
  message: '配图数量必须在1-10之间',
  trigger: 'blur'
}],

})
const formRef = ref() // 表单 Ref


const wordList = ref([] as SimpleWord[])
const pictureList = ref([] as SimpleFile[])
const knowledgeList = ref([] as SimpleFile[])
const titleList = ref([] as Instruction[])
const articleList = ref([] as Instruction[])

// 图片选择弹框相关
const pictureDialogVisible = ref(false)
const selectedPictures = ref([] as SimpleFile[])
const tempSelectedPictures = ref([] as SimpleFile[])

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = title(type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CreationApi.getCreation(id)
      formData.value.pictureIdList = formData.value['pictureIds'].split(',').map(Number)
    } finally {
      formLoading.value = false
    }
  }

  wordList.value = await WordApi.getSimpleWordList()
  // 'GEO_PICTURE' GEO_KNOWLEDGE
  const temp:[SimpleFile]= await getSimpleFileList()

  pictureList.value = temp.filter(t=>t.category == 'GEO_PICTURE')
  knowledgeList.value =temp.filter(t=>t.category == 'GEO_KNOWLEDGE')

  const temp2:[Instruction] = await InstructionApi.getSimpleInstructionList()

  articleList.value = temp2.filter(t=>t.instructionType == 1)
  titleList.value = temp2.filter(t=>t.instructionType == 2)

  // 初始化已选择的图片
  updateSelectedPictures()


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
    const data = formData.value as unknown as Creation
    data.pictureIds = data.pictureIdList?.join(",")
    if (formType.value === 'create') {
      await CreationApi.createCreation(data)
      message.success('创建成功！')
    } else {
      await CreationApi.updateCreation(data)
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
    wordId: undefined,
    count: undefined,
    knowledgeId: undefined,
    pictureIdList: undefined,
    pictureCount: undefined,
    contentId: undefined,
    titleId: undefined,
  }
  selectedPictures.value = []
  tempSelectedPictures.value = []
  formRef.value?.resetFields()
}

/** 打开图片选择弹框 */
const openPictureDialog = () => {
  tempSelectedPictures.value = [...selectedPictures.value]
  pictureDialogVisible.value = true
}

/** 判断图片是否被选中 */
const isPictureSelected = (id: number) => {
  return tempSelectedPictures.value.some(pic => pic.id === id)
}

/** 切换图片选中状态 */
const togglePicture = (picture: SimpleFile) => {
  const index = tempSelectedPictures.value.findIndex(pic => pic.id === picture.id)
  if (index > -1) {
    // 已选中，取消选中
    tempSelectedPictures.value.splice(index, 1)
  } else {
    // 未选中，检查是否超过5张
    if (tempSelectedPictures.value.length >= 5) {
      message.warning('最多只能选择5张图片')
      return
    }
    tempSelectedPictures.value.push(picture)
  }
}

/** 确认图片选择 */
const confirmPictureSelection = () => {
  selectedPictures.value = [...tempSelectedPictures.value]
  formData.value.pictureIdList = selectedPictures.value.map(pic => pic.id)
  pictureDialogVisible.value = false
}

/** 移除已选择的图片 */
const removePicture = (id: number) => {
  const index = selectedPictures.value.findIndex(pic => pic.id === id)
  if (index > -1) {
    selectedPictures.value.splice(index, 1)
    formData.value.pictureIdList = selectedPictures.value.map(pic => pic.id)
  }
}

/** 更新已选择的图片列表 */
const updateSelectedPictures = () => {
  if (formData.value.pictureIdList && formData.value.pictureIdList.length > 0) {
    selectedPictures.value = pictureList.value.filter(pic =>
      formData.value.pictureIdList.includes(pic.id)
    )
  } else {
    selectedPictures.value = []
  }
}
</script>

<style scoped>
/* 已选择图片展示区域 */
.selected-pictures {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.picture-item {
  position: relative;
  width: 100px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.picture-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.picture-item img {
  width: 100%;
  height: 80px;
  object-fit: cover;
}

.picture-label {
  padding: 4px 8px;
  font-size: 12px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background-color: #f5f7fa;
}

.remove-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  background-color: rgba(255, 0, 0, 0.7);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.picture-item:hover .remove-icon {
  opacity: 1;
}

.picture-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

/* 图片选择弹框 */
.picture-dialog-content {
  max-height: 500px;
  overflow-y: auto;
}

.picture-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  padding: 10px;
}

.picture-card {
  position: relative;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.picture-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.picture-card.selected {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.picture-card img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.picture-card-label {
  padding: 8px;
  font-size: 13px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  background-color: #fff;
}

.check-mark {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.selection-count {
  color: #606266;
  font-size: 14px;
  margin-right: auto;
}
</style>
