<template>
  <div>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="创作名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入创作名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item label="创作状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择创作状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.GEO_CREATION_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创作时间" prop="creationTime">
        <el-date-picker
          v-model="queryParams.creationTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
        > 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
        > 导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
        > 批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
        row-key="id"
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        @selection-change="handleRowCheckboxChange"
    >
    <el-table-column type="selection" width="55" />
      <el-table-column label="创作名称" align="center" prop="name" />
      <el-table-column label="主词" align="center" prop="word" />
      <el-table-column label="创作数量" align="center" prop="count" />
      <el-table-column label="已创作数量" align="center" prop="successCount" />
      <el-table-column label="知识库" align="center" prop="knowledgeId" >
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.knowledgeId">
            引用知识库
          </el-tag>
          <el-tag v-else>
            未引用知识库
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创作状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.GEO_CREATION_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="错误消息" align="center" prop="errorMessage" >
        <template #default="scope">
          {{scope.row.errorMessage == null ? '-' : scope.row.errorMessage}}
        </template>
      </el-table-column>
      <el-table-column label="文章" align="center" prop="articleFlag" >
        <template #default="scope">
          <el-link
            v-if="scope.row.articleFlag"
            type="primary"
            @click="handleArticleClick(scope.row)"
          >
            查看文章
          </el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
        label="最新创作时间"
        align="center"
        prop="creationTime"
        :formatter="dateFormatter"
        width="180px"
      >
        <template #default="scope">
          {{scope.row.creationTime == null ? '-' : scope.row.creationTime}}
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button :disabled="scope.row.status != 0"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"

          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"

          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <CreationForm ref="formRef" @success="getList" />
  </div>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { CreationApi, Creation } from '@/api/geo/creation'
import CreationForm from './CreationForm.vue'
import { useRouter } from 'vue-router'
import {useMessage} from '@/hooks/web/useMessage'
import {ref,reactive,onMounted} from 'vue'
/** AI创作 列表 */
defineOptions({ name: 'GeoCreation' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<Creation[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  wordId: undefined,
  status: undefined,
  creationTime: [],
  createTime: [],
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const router = useRouter()
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CreationApi.getCreationPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const handleArticleClick = (row) => {
  router.push(`/article/detail/${row.id}`)
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await CreationApi.deleteCreation(id)
    message.success('删除成功！')
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除AI创作 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await CreationApi.deleteCreationList(checkedIds.value);
    checkedIds.value = [];
    message.success('删除成功！')
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: Creation[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await CreationApi.exportCreation(queryParams)
    download.excel(data, 'AI创作.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
