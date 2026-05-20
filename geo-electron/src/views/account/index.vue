<template>
  <div>
    <ContentWrap>
      <div class="platform-container flex flex-wrap gap-4">
        <a
          v-for="platform in platforms"
          :key="platform.id"
          class="simple-platform-item"
          @click="handlePlatformClick(platform)"
        >
          <el-card class="simple-platform-card" shadow="hover">
            <div class="platform-content">
              <img alt="" class="message-icon" :src="platform.icon" />
              <span>{{ platform.type.valueOf() }}</span>
            </div>
          </el-card>
        </a>
      </div>
    </ContentWrap>
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form
        class="-mb-15px"
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
      >
        <el-form-item label="账号名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入账号名称"
            clearable
            @keyup.enter="handleQuery"
            class="!w-240px"
          />
        </el-form-item>
        <el-form-item label="发布状态" prop="publishStatus">
          <el-select
            v-model="queryParams.publishStatus"
            placeholder="请选择发布状态"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.GEO_PUBLISH_STATUS)"
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
        <el-table-column label="账号名称" align="center" prop="name" />
        <el-table-column label="头像" align="center" prop="path">
          <template #default="scope">
            <img
              width="40px"
              height="40px"
              :src="scope.row.path"
              alt=""
              style="border-radius: 50%"
            />
          </template>
        </el-table-column>
        <el-table-column label="平台" align="center" prop="platform">
          <template #default="scope">
            <dict-tag
              :type="DICT_TYPE.GEO_PUBLISH_PLATFORM"
              :value="scope.row.platform"
            />
          </template>
        </el-table-column>
        <el-table-column label="发布状态" align="center" prop="publishStatus">
          <template #default="scope">
            <dict-tag
              :type="DICT_TYPE.GEO_PUBLISH_STATUS"
              :value="scope.row.publishStatus"
            />
          </template>
        </el-table-column>

        <el-table-column label="授权状态" align="center" prop="authorizeStatus">
          <template #default="scope">
            <dict-tag
              :type="DICT_TYPE.GEO_AUTHORIZE_STATUS"
              :value="scope.row.authorizeStatus"
            />
          </template>
        </el-table-column>
        <el-table-column label="发布次数" align="center" prop="publishCount" />
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
            <el-button link type="danger" @click="handleDelete(scope.row.id)">
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
    <AccountForm ref="formRef" @success="getList" />
  </div>
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from "@/utils/dict";
import { isEmpty } from "@/utils/is";
import { dateFormatter } from "@/utils/formatTime";
import download from "@/utils/download";
import { Account, AccountApi } from "@/api/geo/account";
import AccountForm from "./AccountForm.vue";
import { useMessage } from "@/hooks/web/useMessage";
import { onMounted, reactive, ref } from "vue";
import { useLocalSocket } from "@/composables/useLocalSocket";
import { platforms } from "@/utils/geo/platform";
import { useRouter } from "vue-router";
import { accountAuthorization } from "@/api/local/";
import { client_id_key } from "@/utils/constants";
import { getDataPath } from "@/utils/electronAPI";

/** 授权账号 列表 */
defineOptions({ name: "GeoAccount" });
const message_type = "ws:account_authorization";

const { subscribeLocalEvent } = useLocalSocket();
subscribeLocalEvent(message_type, async (data) => {
  const { platform, avatar, name, code } = data;
  if (code != 1) {
    console.log(platform + "授权失败！");
  } else {
    const p = platforms.filter((e) => e.type.valueOf() == platform)[0];
    await AccountApi.saveOrUpdate({ name: name, avatar: avatar, value: p.id });
    console.log(platform + "授权成功！");
    await getList();
  }
});

const message = useMessage(); // 消息弹窗
const handlePlatformClick = async (platform) => {
  try {
    const {userDataPath} = await getDataPath()
    const result = await accountAuthorization({
      platform: platform.type.valueOf(),
      clientId: client_id_key,
      timestamp: Date.now(),
      dataDir: userDataPath,
    });
    if (result.code != 1) {
      message.error(result.message);
    }
  } catch (error) {
    console.log("授权错误！", error);
  }
};

const loading = ref(true); // 列表的加载中
const list = ref<Account[]>([]); // 列表的数据
const total = ref(0); // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  platform: undefined,
  publishStatus: undefined,
  createTime: [],
});
const queryFormRef = ref(); // 搜索的表单
const exportLoading = ref(false); // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true;
  try {
    const data = await AccountApi.getAccountPage(queryParams);
    list.value = data.list;
    total.value = data.total;
  } finally {
    loading.value = false;
  }
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields();
  handleQuery();
};

/** 添加/修改操作 */
const formRef = ref();
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id);
};

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm();
    // 发起删除
    await AccountApi.deleteAccount(id);
    message.success("删除成功！");
    // 刷新列表
    await getList();
  } catch {}
};

/** 批量删除授权账号 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm();
    await AccountApi.deleteAccountList(checkedIds.value);
    checkedIds.value = [];
    message.success("删除成功！");
    await getList();
  } catch {}
};

const checkedIds = ref<number[]>([]);
const handleRowCheckboxChange = (records: Account[]) => {
  checkedIds.value = records.map((item) => item.id!);
};

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm();
    // 发起导出
    exportLoading.value = true;
    const data = await AccountApi.exportAccount(queryParams);
    download.excel(data, "授权账号.xls");
  } catch {
  } finally {
    exportLoading.value = false;
  }
};

/** 初始化 **/
onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.simple-platform-item {
  width: 140px;
  height: 100px;
  img {
    width: 30px;
    height: 30px;
  }
}

/* 平台内容容器 */
.platform-content {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 6px !important;
  width: 100%;
}
.simple-platform-card {
  flex: 1;
  border: 2px solid #ebeef5;
  outline: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.simple-platform-card:hover {
  border-color: #409eff;
}

/* 文字样式 */
.platform-content span {
  font-size: 16px;
  font-weight: 600;
  margin-left: 8px;
  white-space: nowrap;
  flex-shrink: 0;
}

.platform-container {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center; /* 文本也居中 */
}

.simple-platform-item.disabled {
  pointer-events: none; /* 禁用点击事件 */
  opacity: 0.5; /* 变灰效果 */
  cursor: not-allowed; /* 禁止光标 */
}

/* 如果需要禁用 hover 效果 */
.simple-platform-item.disabled .simple-platform-card:hover {
  box-shadow: none;
  transform: none;
}
</style>
