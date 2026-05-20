<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="主词" prop="word">
        <el-input :disabled="formData.id != null" v-model="formData.word" placeholder="请输入主词" />
      </el-form-item>
      <el-form-item label="转化目标" prop="target">
        <el-input v-model="formData.target" placeholder="请输入转化目标" />
      </el-form-item>
      <el-form-item >
        <el-button v-if="!formData.id"  @click="distill" type="danger" :disabled="!formData.word || !formData.target"
        >蒸馏词</el-button
        >
      </el-form-item>

      <el-form-item v-if="!formData.id" label="蒸馏結果" prop="question">
        <el-input
                  v-model="formData.question"
          :autosize="{ minRows: 12, maxRows: 12 }"
          type="textarea"
          placeholder="请输入指令"
        />
      </el-form-item>
<!--      <el-form-item label="优化" prop="optimized">
        <el-select v-model="formData.optimized" placeholder="请选择优化">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.GEO_OPTIMIZED)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="查询拓展词" prop="expand">
        <el-select v-model="formData.expand" placeholder="请选择查询拓展词">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.GEO_EXPAND)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from "@/utils/dict";
import { WordApi, Word, DistillWord } from "@/api/geo/word";
import { useMessage } from "@/hooks/web/useMessage";
import { ref, reactive, onMounted } from "vue";
import { title } from "@/utils/utils";
/** 蒸馏词 表单 */
defineOptions({ name: "WordForm" });

const message = useMessage(); // 消息弹窗

const dialogVisible = ref(false); // 弹窗的是否展示
const dialogTitle = ref(""); // 弹窗的标题
const formLoading = ref(false); // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref(""); // 表单的类型：create - 新增；update - 修改
const formData = ref({
  word: undefined,
  target: undefined,
  optimized: undefined,
  id: undefined,
  question: undefined,
  expand: undefined,
});
const formRules = reactive({
  word: [{ required: true, message: "主词不能为空", trigger: "blur" }],
  target: [{ required: true, message: "转化目标不能为空", trigger: "blur" }],
});
const formRef = ref(); // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true;
  dialogTitle.value =title( type);
  formType.value = type;
  resetForm();
  // 修改时，设置数据
  if (id) {
    formLoading.value = true;
    try {
      formData.value = await WordApi.getWord(id);
    } finally {
      formLoading.value = false;
    }
  }
};
defineExpose({ open }); // 提供 open 方法，用于打开弹窗

const distill = async () => {

  // 提交请求
  formLoading.value = true;
  try {
    const data = formData.value as unknown as DistillWord;
     const questions:[] = await WordApi.distill(data);
     formData.value.question = questions.join('\n');
      message.success("蒸馏成功！");
  } finally {
    formLoading.value = false;
  }
};

/** 提交表单 */
const emit = defineEmits(["success"]); // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {

  // 校验表单
  await formRef.value.validate();
  // 提交请求
  formLoading.value = true;
  try {
    const data = formData.value as unknown as Word;
    if (formType.value === "create") {
      if(formData.value.question) {
        data.questions = formData.value.question.split('\n');
      }
      await WordApi.createWord(data);
      message.success("创建成功！");
    } else {
      await WordApi.updateWord(data);
      message.success("更新成功！");
    }
    dialogVisible.value = false;
    // 发送操作成功的事件
    emit("success");
  } finally {
    formLoading.value = false;
  }
};

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    word: undefined,
    target: undefined,
    id: undefined,
    question: undefined,
    optimized: undefined,
    expand: undefined,
  };
  formRef.value?.resetFields();
};
</script>
