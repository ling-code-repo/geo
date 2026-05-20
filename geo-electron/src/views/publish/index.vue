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
      <el-form-item label="任务名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入任务名"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="发布状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择发布状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.GEO_PUBLISH_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="执行时间" prop="executeTime">
        <el-date-picker
          v-model="queryParams.executeTime"
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
        >
           新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
        >
        导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
        >
          批量删除
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
      <el-table-column label="任务名" align="center" prop="name" />
      <el-table-column label="启停状态" align="center" prop="status" >
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.GEO_PUBLISH_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
<!--      <el-table-column label="任务状态" align="center" prop="taskStatus" >-->
<!--        <template #default="scope">-->
<!--          <dict-tag :type="DICT_TYPE.GEO_PUBLISH_TASK_STATUS" :value="scope.row.taskStatus" />-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="AI声明" align="center" prop="declareAi" >
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.GEO_DECLARE_AI" :value="scope.row.declareAi" />
        </template>
      </el-table-column>
      <el-table-column label="平台数" align="center" prop="platforms" >
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.platforms">
            {{ scope.row.platforms ? scope.row.platforms?.split(",").length : 0 }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误消息" align="center" prop="errorMessage" >
        <template #default="scope">
          {{ scope.row.errorMessage == null ? '-' : scope.row.errorMessage }}
        </template>
      </el-table-column>
      <el-table-column
        label="执行时间"
        align="center"
        prop="executeTime"
        :formatter="dateFormatter"
        width="180px"
      >
        <template #default="scope">
          {{ scope.row.executeTime == null ? '-' : scope.row.executeTime }}
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
          <el-button :disabled="scope.row.taskStatus == 2"
            link
            type="success"
            @click="executePublishTask(scope.row.id)"
          >
            发布
          </el-button>
          <el-button
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
  <PublishTaskForm ref="formRef" @success="getList" />
  </div>
</template>

<script setup lang="ts">
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { PublishTaskApi, PublishTask } from '@/api/geo/publish'
import PublishTaskForm from './PublishTaskForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import {useMessage} from '@/hooks/web/useMessage'
import {ref,reactive,onMounted} from 'vue'
// import { useGlobalSocket } from '@/composables/useGlobalSocket'

/** 发布任务 列表 */
defineOptions({ name: 'GeoPublishTask' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<PublishTask[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  status: undefined,
  executeTime: [],
  createTime: [],
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PublishTaskApi.getPublishTaskPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const executePublishTask =async (id: number) =>{
  await PublishTaskApi.executePublishTask({id})
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await PublishTaskApi.deletePublishTask(id)
    message.success('删除成功！')
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除发布任务 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await PublishTaskApi.deletePublishTaskList(checkedIds.value);
    checkedIds.value = [];
    message.success('删除成功！')
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: PublishTask[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await PublishTaskApi.exportPublishTask(queryParams)
    download.excel(data, '发布任务.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
  // 订阅 WebSocket 消息（不需要初始化，登录时已经自动初始化）
  // setupWebSocketListeners()
})
/*
/!** 设置 WebSocket 监听器 *!/
const setupWebSocketListeners = () => {
  const { subscribe, onStatusChange } = useGlobalSocket()

  // 1. 订阅文章发布任务更新事件（来自后台服务）
  subscribe('publish-task-update', (data) => {
    console.log('[Publish] 收到任务更新消息:', data)
    const { taskId, status, result, error } = data

    if (status === 'completed') {
      message.success(`文章 "${result?.title}" 发布成功！`)
    } else if (status === 'failed') {
      message.error(`文章发布失败: ${error?.message || '未知错误'}`)
    }

    // 刷新列表
    getList()
  })

  // 2. 订阅发布进度事件（实时显示进度）
  subscribe('publish-progress', (data) => {
    console.log('[Publish] 收到进度更新:', data)
    const { taskId, progress, message: progressMsg } = data

    // 可以在这里更新进度显示
    // 例如：显示进度条、更新状态文字等
    console.log(`任务 ${taskId} 进度: ${progress}% - ${progressMsg}`)

    // 如果需要，可以更新列表中对应任务的状态
    const task = list.value.find(t => t.id === taskId)
    if (task) {
      // 可以在表格中显示进度
      console.log(`更新任务 ${taskId} 的进度显示`)
    }
  })

  // 3. 订阅系统通知
  subscribe('notice-push', (data) => {
    console.log('[Publish] 收到系统通知:', data)
    message.info(data.title || data.content)
  })

  // 4. 监听 WebSocket 连接状态
  onStatusChange((status) => {
    console.log('[Publish] WebSocket 连接状态:', status)
    if (status === 'connected') {
      console.log('[Publish] WebSocket 已连接，可以接收实时消息')
    } else if (status === 'disconnected') {
      console.warn('[Publish] WebSocket 已断开，将无法接收实时消息')
    }
  })
}*/
</script>
