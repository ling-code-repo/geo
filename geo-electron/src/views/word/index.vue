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
        <el-form-item label="主词" prop="word">
          <el-input
              v-model="queryParams.word"
              placeholder="请输入主词"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
          />
        </el-form-item>
        <el-form-item label="转化目标" prop="target">
          <el-input
              v-model="queryParams.target"
              placeholder="请输入转化目标"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="优化" prop="optimized">
          <el-select
              v-model="queryParams.optimized"
              placeholder="请选择优化"
              clearable
              style="width: 240px"
          >
            <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.GEO_OPTIMIZED)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label-width="90" label="查询拓展词" prop="expand">
          <el-select
              v-model="queryParams.expand"
              placeholder="请选择查询拓展词"
              clearable
              style="width: 240px"
          >

            <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.GEO_EXPAND)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            />
          </el-select>
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
        <el-table-column label="主词" align="center" prop="word" />
        <el-table-column label="转化目标" align="center" prop="target" />
        <el-table-column label="问题数量" align="center" prop="questionCount" />
        <el-table-column label="优化" align="center" prop="optimized">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.GEO_OPTIMIZED" :value="scope.row.optimized" />
          </template>
        </el-table-column>
        <el-table-column label="查询拓展词" align="center" prop="expand">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.GEO_EXPAND" :value="scope.row.expand" />
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
    <WordForm ref="formRef" @success="getList" />
  </div>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { WordApi, Word } from '@/api/geo/word'
import WordForm from './WordForm.vue'
import {ref,reactive,onMounted} from 'vue'
import {useMessage} from '@/hooks/web/useMessage'
/** 蒸馏词 列表 */
defineOptions({ name: 'GeoWord' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<Word[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  word: undefined,
  target: undefined,
  optimized: undefined,
  expand: undefined,
  createTime: [],
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WordApi.getWordPage(queryParams)
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

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await WordApi.deleteWord(id)
    message.success('刪除成功！')
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除蒸馏词 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await WordApi.deleteWordList(checkedIds.value);
    checkedIds.value = [];
    message.success('刪除成功！')
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: Word[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await WordApi.exportWord(queryParams)
    download.excel(data, '蒸馏词.xls')
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
