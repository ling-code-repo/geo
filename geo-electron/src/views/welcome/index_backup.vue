<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, markRaw } from "vue";
import * as echarts from "echarts";
import { useRouter } from "vue-router";
import {
  Key,
  Document,
  FolderOpened,
  EditPen,
  User
} from "@element-plus/icons-vue";



defineOptions({
  name: "Welcome"
});

const router = useRouter();


// 静态数据 - 工作流程步骤
const workflowSteps = ref([
  { id: 1, number: "01", title: "蒸馏词", subtitle: "选择创作模板", status: "completed", statusText: "已完成", color: "#8B5CF6", bgColor: "#F3E8FF" ,path:"/word"},
  { id: 2, number: "02", title: "上传图库", subtitle: "上传产品图片", status: "completed", statusText: "已完成", color: "#8B5CF6", bgColor: "#F3E8FF" ,path:"/picture"},
  { id: 3, number: "03", title: "创建指令", subtitle: "设置创作规则", status: "completed", statusText: "已完成", color: "#8B5CF6", bgColor: "#F3E8FF" ,path:"/instruction"},
  { id: 4, number: "04", title: "AI创作", subtitle: "生成内容", status: "completed", statusText: "已完成", color: "#8B5CF6", bgColor: "#F3E8FF" ,path:"/creation"},
  { id: 5, number: "05", title: "授权账号", subtitle: "账号授权", status: "completed", statusText: "已完成", color: "#8B5CF6", bgColor: "#F3E8FF" ,path:"/account"},
  { id: 6, number: "06", title: "发布任务", subtitle: "创建发布任务", status: "completed", statusText: "已完成", color: "#8B5CF6", bgColor: "#F3E8FF" ,path:"/publish"},
  // { id: 7, number: "07", title: "收录明细", subtitle: "查看收录情况", status: "pending", statusText: "客户端发布", color: "#8B5CF6", bgColor: "#F3E8FF" }
]);

// 静态数据 - 指标卡片
const metricCards = ref([
  { id: 1, title: "AI蒸缩词", value: 5, total: 5, unit: "个", icon: markRaw(Key), color: "#10B981", bgColor: "#ECFDF5" },
  { id: 2, title: "训练问题", value: 248, total: 500, unit: "条", icon: markRaw(Document), color: "#3B82F6", bgColor: "#EFF6FF" },
  // { id: 3, title: "收录问题", value: 2, total: 0, unit: "条", icon: markRaw(FolderOpened), color: "#14B8A6", bgColor: "#F0FDFA" },
  { id: 4, title: "创作数量", value: 103, total: 500, unit: "篇", icon: markRaw(EditPen), color: "#8B5CF6", bgColor: "#F3E8FF" },
  // { id: 5, title: "媒体投喂", value: 11, total: 500, unit: "次", icon: "🗳️", color: "#EC4899", bgColor: "#FDF2F8" },
  { id: 6, title: "投喂账号", value: 8, total: 12, unit: "个", icon: markRaw(User), color: "#F97316", bgColor: "#FFFBEB" }
]);

// 静态数据 - 工具集成
const toolIntegrations = ref([
  { id: 1, name: "deepseek", icon: "🤖", count: 0, unit: "收录", color: "#3B82F6" },
  { id: 2, name: "豆包", icon: "🫘", count: 0, unit: "收录", color: "#8B5CF6" },
  { id: 3, name: "腾讯元宝", icon: "💎", count: 2, unit: "收录", color: "#10B981" },
  { id: 4, name: "通义千问", icon: "💡", count: 0, unit: "收录", color: "#8B5CF6" },
  { id: 5, name: "文心一言", icon: "🧠", count: 2, unit: "收录", color: "#3B82F6" },
  { id: 6, name: "纳米", icon: "🔬", count: 0, unit: "收录", color: "#EF4444" },
  { id: 7, name: "KIMI", icon: "🌟", count: 0, unit: "收录", color: "#1F2937" },
  { id: 8, name: "智谱", icon: "🎯", count: 0, unit: "收录", color: "#3B82F6" }
]);

// 图表引用
const aiCreationChartRef = ref<HTMLDivElement>();
const collectionRatioChartRef = ref<HTMLDivElement>();
const feedDataChartRef = ref<HTMLDivElement>();

// 图表实例
let aiCreationChart: echarts.ECharts | null = null;
let collectionRatioChart: echarts.ECharts | null = null;
let feedDataChart: echarts.ECharts | null = null;

// Resize 处理函数
const handleResize = () => {
  aiCreationChart?.resize();
  collectionRatioChart?.resize();
  feedDataChart?.resize();
};

// 初始化AI创作图表
const initAiCreationChart = () => {
  try {
    if (!aiCreationChartRef.value) return;

    aiCreationChart = echarts.init(aiCreationChartRef.value);
    const option = {
      grid: {
        left: 40,
        right: 20,
        top: 10,
        bottom: 30
      },
      xAxis: {
        type: "category",
        data: ["01-07", "01-08", "01-09", "01-10", "01-11"],
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: {
          color: "#6B7280",
          fontSize: 10
        }
      },
      yAxis: {
        type: "value",
        splitLine: {
          lineStyle: {
            type: "dashed",
            color: "#E5E7EB"
          }
        },
        axisLabel: {
          color: "#9CA3AF",
          fontSize: 10
        }
      },
      series: [
        {
          data: [15, 23, 18, 32, 28],
          type: "line",
          smooth: true,
          lineStyle: {
            color: "#14B8A6",
            width: 3
          },
          itemStyle: {
            color: "#14B8A6"
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "rgba(20, 184, 166, 0.3)" },
              { offset: 1, color: "rgba(20, 184, 166, 0)" }
            ])
          }
        }
      ]
    };
    aiCreationChart.setOption(option);
  } catch (error) {
    console.error("AI创作图表初始化失败:", error);
  }
};

// 初始化收录比例图表
const initCollectionRatioChart = () => {
  try {
    if (!collectionRatioChartRef.value) return;

    collectionRatioChart = echarts.init(collectionRatioChartRef.value);
    const option = {
      series: [
        {
          name: "收录率",
          type: "pie",
          radius: ["60%", "80%"],
          avoidLabelOverlap: false,
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: false
            }
          },
          labelLine: {
            show: false
          },
          data: [
            {
              value: 68,
              name: "已收录",
              itemStyle: {
                color: "#14B8A6"
              }
            },
            {
              value: 32,
              name: "未收录",
              itemStyle: {
                color: "#E5E7EB"
              }
            }
          ]
        }
      ],
      graphic: [
        {
          type: "text",
          left: "center",
          top: "40%",
          style: {
            text: "68%",
            fontSize: 32,
            fontWeight: "bold",
            fill: "#14B8A6"
          }
        },
        {
          type: "text",
          left: "center",
          top: "55%",
          style: {
            text: "收录率",
            fontSize: 14,
            fill: "#6B7280"
          }
        }
      ]
    };
    collectionRatioChart.setOption(option);
  } catch (error) {
    console.error("收录比例图表初始化失败:", error);
  }
};

// 初始化投喂数据图表
const initFeedDataChart = () => {
  try {
    if (!feedDataChartRef.value) return;

    feedDataChart = echarts.init(feedDataChartRef.value);
    const option = {
      grid: {
        left: 40,
        right: 20,
        top: 10,
        bottom: 30
      },
      xAxis: {
        type: "category",
        data: ["01-07", "01-08", "01-09", "01-10", "01-11"],
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: {
          color: "#6B7280",
          fontSize: 10
        }
      },
      yAxis: {
        type: "value",
        splitLine: {
          lineStyle: {
            type: "dashed",
            color: "#E5E7EB"
          }
        },
        axisLabel: {
          color: "#9CA3AF",
          fontSize: 10
        }
      },
      series: [
        {
          data: [42, 38, 55, 48, 62],
          type: "line",
          smooth: true,
          lineStyle: {
            color: "#8B5CF6",
            width: 3
          },
          itemStyle: {
            color: "#8B5CF6"
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "rgba(139, 92, 246, 0.3)" },
              { offset: 1, color: "rgba(139, 92, 246, 0)" }
            ])
          }
        }
      ]
    };
    feedDataChart.setOption(option);
  } catch (error) {
    console.error("投喂数据图表初始化失败:", error);
  }
};

const openTab = (step: any) => {
  router.push(step.path);
};

onMounted(() => {
  // 使用 nextTick 确保 DOM 已经渲染完成
  nextTick(() => {
    // 使用 setTimeout 确保 el-card 完全渲染
    setTimeout(() => {
      initAiCreationChart();
      initCollectionRatioChart();
      initFeedDataChart();
      window.addEventListener("resize", handleResize);
    }, 100);
  });
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  aiCreationChart?.dispose();
  collectionRatioChart?.dispose();
  feedDataChart?.dispose();
});
</script>

<template>
  <div class="dashboard-container">
    <!-- 顶部工作流程 -->
    <el-card class="workflow-bar" shadow="never">
      <div class="workflow-steps-horizontal">
        <div
          v-for="step in workflowSteps"
          :key="step.id"
          class="workflow-step-horizontal"
          @click="openTab(step)"
          :style="{ backgroundColor: step.bgColor }"
        >
          <div class="step-header-horizontal">
            <div class="step-number-horizontal" :style="{ backgroundColor: step.color }">
              {{ step.number }}
            </div>
            <div class="step-content-horizontal">
              <div class="step-title-horizontal">{{ step.title }}</div>
              <div class="step-subtitle-horizontal">{{ step.subtitle }}</div>
            </div>
          </div>
<!--          <el-button
            :type="step.status === 'completed' ? 'primary' : 'primary'"
            :style="{ backgroundColor: step.color, border: 'none', marginLeft: '8px' }"
            size="small"
          >
            {{ step.statusText }}
          </el-button>-->
        </div>
      </div>
    </el-card>

    <!-- 主内容区域 -->
    <el-row :gutter="20" class="main-content">
      <!-- 中间内容区 - 占满整个宽度 -->
      <el-col :span="24">
        <!-- 指标卡片 -->
        <el-card shadow="never" class="metrics-card">
          <el-row :gutter="16">
            <el-col :span="6" v-for="card in metricCards" :key="card.id">
              <div
                class="metric-card"
                :style="{ backgroundColor: card.bgColor }"
              >
                <div class="metric-icon" :style="{ color: card.color }">
                  <el-icon v-if="typeof card.icon !== 'string'" :size="28">
                    <component :is="card.icon" />
                  </el-icon>
                  <span v-else class="emoji-icon">{{ card.icon }}</span>
                </div>
                <div class="metric-info">
                  <div class="metric-title">{{ card.title }}</div>
                  <div class="metric-value" :style="{ color: card.color }">
                    {{ card.total > 0 ? `${card.value}/${card.total}` : card.value }}
                    <span class="metric-unit">{{ card.unit }}</span>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 工具集成 -->
<!--        <el-card shadow="never" class="tools-integration-card">-->
<!--          <template #header>-->
<!--            <span class="card-title">工具集成</span>-->
<!--          </template>-->
<!--          <div class="tools-integration-container">-->
<!--            <div-->
<!--              v-for="tool in toolIntegrations"-->
<!--              :key="tool.id"-->
<!--              class="tool-integration-card"-->
<!--            >-->
<!--              <div class="tool-icon-circle" :style="{ backgroundColor: tool.color + '20' }">-->
<!--                <span class="tool-icon">{{ tool.icon }}</span>-->
<!--              </div>-->
<!--              <div class="tool-name">{{ tool.name }}</div>-->
<!--              <div class="tool-count">{{ tool.count }} {{ tool.unit }}</div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </el-card>-->

        <!-- 图表区域 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span class="chart-title">AI创作</span>
<!--                  <span class="chart-subtitle">根据最新时间统计数据</span>-->
                </div>
              </template>
              <div ref="aiCreationChartRef" class="chart-container"></div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span class="chart-title">投喂数据</span>
<!--                  <span class="chart-subtitle">根据最新时间统计数据</span>-->
                </div>
              </template>
              <div ref="feedDataChartRef" class="chart-container"></div>
            </el-card>
          </el-col>

<!--          <el-col :span="8">
            <el-card shadow="never" class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span class="chart-title">收录比例</span>
&lt;!&ndash;                  <span class="chart-subtitle">根据最新时间统计数据</span>&ndash;&gt;
                </div>
              </template>
              <div ref="collectionRatioChartRef" class="chart-container"></div>
            </el-card>
          </el-col>-->
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.dashboard-container {
  min-height: 70vh;
  background-color: #f9fafb;
  padding: 20px;
}

/* 顶部工作流程 */
.workflow-bar {
  margin-bottom: 20px;
}

.workflow-steps-horizontal {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 4px;
  justify-content: center;
}

.workflow-step-horizontal {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 200px;
  padding: 14px 18px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.step-header-horizontal {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
}

.step-number-horizontal {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
}

.step-content-horizontal {
  flex: 1;
}

.step-title-horizontal {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.step-subtitle-horizontal {
  font-size: 13px;
  color: #6b7280;
}

/* 主内容区域 */
.main-content {
  margin-top: 0;
}

/* 指标卡片 */
.metrics-card {
  margin-bottom: 20px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  margin-bottom: 20px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.metric-icon {
  flex-shrink: 0;
}

.emoji-icon {
  font-size: 28px;
  display: inline-block;
}

.metric-info {
  flex: 1;
}

.metric-title {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
}

.metric-value {
  font-size: 22px;
  font-weight: 700;
}

.metric-unit {
  font-size: 16px;
  font-weight: 500;
  margin-left: 2px;
}

/* 工具集成 */
.tools-integration-card {
  margin-bottom: 0;
}

.tools-integration-container {
  display: flex;
  gap: 12px;
  justify-content: space-between;
  flex-wrap: wrap;
}

.tool-integration-card {
  flex: 0 0 calc(25% - 9px);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border-radius: 10px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    background: #f3f4f6;
    border-color: #d1d5db;
    transform: translateY(-2px);
  }
}

.tool-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.tool-icon {
  font-size: 24px;
}

.tool-name {
  font-size: 13px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.tool-count {
  font-size: 12px;
  color: #6b7280;
}

/* 图表卡片 */
.chart-card {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}

.chart-header {
  display: flex;
  flex-direction: column;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.chart-subtitle {
  font-size: 12px;
  color: #9ca3af;
}

.chart-container {
  height: 200px;
}

.donut-chart-container {
  height: 220px;
}

.feed-chart-container {
  height: 250px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .tool-integration-card {
    flex: 0 0 calc(33.333% - 8px);
  }
}

@media (max-width: 1200px) {
  .dashboard-container {
    padding: 10px;
  }

  .tool-integration-card {
    flex: 0 0 calc(50% - 6px);
  }
}

@media (max-width: 768px) {
  .workflow-steps-horizontal {
    flex-direction: column;
  }

  .workflow-step-horizontal {
    min-width: auto;
  }

  .tool-integration-card {
    flex: 0 0 calc(50% - 6px);
  }
}
</style>
