<script setup lang="ts">
import Motion from "./utils/motion";
import { useRouter } from "vue-router";
import { message } from "@/utils/message";
import { loginRules, REGEXP_PWD } from "./utils/rule";
import { ref, reactive, toRaw } from "vue";
import { debounce } from "@pureadmin/utils";
import { useNav } from "@/layout/hooks/useNav";
import { useEventListener } from "@vueuse/core";
import type { FormInstance } from "element-plus";
import { useLayout } from "@/layout/hooks/useLayout";
import { useUserStoreHook } from "@/store/modules/user";
import { initRouter, getTopMenu } from "@/router/utils";
import { bg, avatar, illustration } from "./utils/static";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { useDataThemeChange } from "@/layout/hooks/useDataThemeChange";
import { ElLoading } from "element-plus";

import dayIcon from "@/assets/svg/day.svg?component";
import darkIcon from "@/assets/svg/dark.svg?component";
import Lock from "~icons/ri/lock-fill";
import User from "~icons/ri/user-3-fill";
import { GeoUserApi } from "@/api/geo/user";

defineOptions({
  name: "Login",
});

const router = useRouter();
const loading = ref(false);
const disabled = ref(false);
const ruleFormRef = ref<FormInstance>();
const registerFormRef = ref<FormInstance>();
const isLogin = ref(true); // 登录/注册切换

const { initStorage } = useLayout();
initStorage();

const { dataTheme, overallStyle, dataThemeChange } = useDataThemeChange();
dataThemeChange(overallStyle.value);
const { title } = useNav();

const ruleForm = reactive({
  username: "",
  password: "",
});

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
});

// 注册表单验证规则
const registerRules = reactive({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度在 4 到 20 个字符", trigger: "blur" },
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"));
        } else if (!REGEXP_PWD.test(value)) {
          callback(
            new Error("密码格式应为8-18位数字、字母、符号的任意两种组合")
          );
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error("两次输入密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
});

const onLogin = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate( async (valid) => {
    if (valid) {
// 创建加载蒙层
      const loadingInstance = ElLoading.service({
        lock: true,
        text: "登录中...",
        background: "rgba(0, 0, 0, 0.7)",
        spinner: "el-icon-loading",
      });

      try{
        // loading.value = true;
        let result = await useUserStoreHook()
          .loginByUsername({
            username: ruleForm.username,
            password: ruleForm.password,
          });
        loading.value = false;
        // disabled.value = false;
        if (result) {
          return initRouter().then(() => {
            disabled.value = true;
            router
              .push(getTopMenu(true).path)
              .then(() => {
                message("登录成功", { type: "success" });
              })
            ;
          });
        }
      }catch (e){
        loadingInstance.close();
        throw e;
      }finally {
        loadingInstance.close();
      }

    }
  });
};

const immediateDebounce: any = debounce(
  (formRef) => onLogin(formRef),
  1000,
  true,
);

// 注册功能
const onRegister = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: "注册中...",
        background: "rgba(0, 0, 0, 0.7)",
        spinner: "el-icon-loading",
      });

      try {
        loading.value = true;
        debugger
        // 这里调用你的注册 API
      let result = await GeoUserApi.register({
          username: registerForm.username,
          password: registerForm.password,
        });
      console.log("result",result)
        // 示例：await useUserStoreHook().registerByUsername({...});
        // 暂时模拟注册成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        message("注册成功，请登录", { type: "success" });
        isLogin.value = true; // 注册成功后切换到登录
        // 清空注册表单
        registerForm.username = "";
        registerForm.password = "";
        registerForm.confirmPassword = "";
        loading.value = false;
      } catch (e) {
        loading.value = false;
        loadingInstance.close();
        throw e;
      } finally {
        loadingInstance.close();
      }
    }
  });
};

// 切换登录/注册
const toggleLoginRegister = () => {
  isLogin.value = !isLogin.value;
  // 切换时清空表单
  if (isLogin.value) {
    registerForm.username = "";
    registerForm.password = "";
    registerForm.confirmPassword = "";
  } else {
    ruleForm.username = "admin";
    ruleForm.password = "admin123";
  }
};

useEventListener(document, "keydown", ({ code }) => {
  if (
    ["Enter", "NumpadEnter"].includes(code) &&
    !disabled.value &&
    !loading.value
  ) {
    if (isLogin.value) {
      immediateDebounce(ruleFormRef.value);
    }
  }
});
</script>

<template>
  <div class="select-none">
    <img :src="bg" class="wave" />
    <div class="flex-c absolute right-5 top-3">
      <!-- 主题 -->
      <el-switch
        v-model="dataTheme"
        inline-prompt
        :active-icon="dayIcon"
        :inactive-icon="darkIcon"
        @change="dataThemeChange"
      />
    </div>
    <div class="login-container">
      <div class="img">
        <component :is="toRaw(illustration)" />
      </div>
      <div class="login-box">
        <div class="login-form">
          <avatar class="avatar" />
          <Motion>
            <h2 class="outline-hidden">{{ isLogin ? title : "注册用户名" }}</h2>
          </Motion>

          <!-- 登录表单 -->
          <el-form
            v-if="isLogin"
            ref="ruleFormRef"
            :model="ruleForm"
            :rules="loginRules"
            size="large"
          >
            <Motion :delay="100">
              <el-form-item
                :rules="[
                  {
                    required: true,
                    message: '请输入用户名',
                    trigger: 'blur',
                  },
                ]"
                prop="username"
              >
                <el-input
                  v-model="ruleForm.username"
                  clearable
                  placeholder="用户名"
                  :prefix-icon="useRenderIcon(User)"
                />
              </el-form-item>
            </Motion>

            <Motion :delay="150">
              <el-form-item prop="password">
                <el-input
                  v-model="ruleForm.password"
                  clearable
                  show-password
                  placeholder="密码"
                  :prefix-icon="useRenderIcon(Lock)"
                />
              </el-form-item>
            </Motion>

            <Motion :delay="250">
              <el-button
                class="w-full mt-4!"
                size="default"
                type="primary"
                :loading="loading"
                :disabled="disabled"
                @click="onLogin(ruleFormRef)"
              >
                登录
              </el-button>
            </Motion>

            <Motion :delay="300">
              <div class="w-full mt-4 text-center">
                <span class="text-gray-500">还没有用户名？</span>
                <el-button
                  type="primary"
                  link
                  @click="toggleLoginRegister"
                >
                  立即注册
                </el-button>
              </div>
            </Motion>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-else
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            size="large"
          >
            <Motion :delay="100">
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  clearable
                  placeholder="用户名"
                  :prefix-icon="useRenderIcon(User)"
                />
              </el-form-item>
            </Motion>

            <Motion :delay="150">
              <el-form-item prop="password">
                <el-input
                  v-model="registerForm.password"
                  clearable
                  show-password
                  placeholder="密码（8-18个字符）"
                  :prefix-icon="useRenderIcon(Lock)"
                />
              </el-form-item>
            </Motion>

            <Motion :delay="200">
              <el-form-item prop="confirmPassword">
                <el-input
                  v-model="registerForm.confirmPassword"
                  clearable
                  show-password
                  placeholder="确认密码"
                  :prefix-icon="useRenderIcon(Lock)"
                />
              </el-form-item>
            </Motion>

            <Motion :delay="250">
              <el-button
                class="w-full mt-4!"
                size="default"
                type="primary"
                :loading="loading"
                @click="onRegister(registerFormRef)"
              >
                注册
              </el-button>
            </Motion>

            <Motion :delay="300">
              <div class="w-full mt-4 text-center">
                <span class="text-gray-500">已有用户名？</span>
                <el-button
                  type="primary"
                  link
                  @click="toggleLoginRegister"
                >
                  立即登录
                </el-button>
              </div>
            </Motion>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url("@/style/login.css");
</style>

<style lang="scss" scoped>
:deep(.el-input-group__append, .el-input-group__prepend) {
  padding: 0;
}
</style>
