<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([PieChart, TitleComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const props = defineProps({
  /** 后端 /mistake/statistics 返回的 subjectGroups：[{ subjectName, mistakeCount }] */
  groups: {
    type: Array,
    default: () => []
  }
})

const chartRef = ref(null)
let chart = null

/* 与整站主题一致的配色，绿色打头 */
const palette = ['#67c23a', '#409eff', '#f5a623', '#8e6cf0', '#e0473d', '#14c9c9', '#7a8699']

const chartData = computed(() => {
  const groups = Array.isArray(props.groups) ? props.groups : []

  return groups
    .map((group) => ({
      name: group.subjectName || '未命名科目',
      value: Number(group.mistakeCount) || 0
    }))
    .filter((item) => item.value > 0)
})

const total = computed(() => chartData.value.reduce((sum, item) => sum + item.value, 0))

function buildOption() {
  return {
    color: palette,
    title: {
      text: '错题科目占比',
      subtext: `共 ${total.value} 道错题`,
      left: 4,
      top: 2,
      textStyle: { fontSize: 16, fontWeight: 600, color: '#0f1b2d' },
      subtextStyle: { fontSize: 12, color: '#7a8699' }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(11, 21, 36, 0.92)',
      borderWidth: 0,
      padding: [10, 14],
      textStyle: { color: '#e6edf7', fontSize: 13 },
      formatter: '{b}<br/>数量：{c} 道<br/>占比：{d}%'
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 12,
      icon: 'circle',
      textStyle: { color: '#475467', fontSize: 13 }
    },
    series: [
      {
        name: '错题科目占比',
        type: 'pie',
        radius: ['46%', '72%'],
        center: ['36%', '56%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2,
          borderRadius: 6
        },
        label: { show: false },
        labelLine: { show: false },
        emphasis: {
          scaleSize: 8,
          itemStyle: {
            shadowBlur: 18,
            shadowColor: 'rgba(103, 194, 58, 0.45)'
          }
        },
        data: chartData.value
      }
    ]
  }
}

function renderChart() {
  if (!chartRef.value) return

  if (!chart) {
    chart = echarts.init(chartRef.value)
  }

  chart.setOption(buildOption(), true)
}

function handleResize() {
  chart?.resize()
}

onMounted(() => {
  renderChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})

watch(chartData, () => {
  renderChart()
})
</script>

<template>
  <section class="panel chart-panel">
    <div ref="chartRef" class="pie-chart" :class="{ 'is-empty': chartData.length === 0 }"></div>

    <div v-if="chartData.length === 0" class="chart-empty">
      暂无错题数据，无法生成占比图
    </div>
  </section>
</template>

<style scoped>
.chart-panel {
  position: relative;
  padding: 18px 22px 22px;
}

.pie-chart {
  width: 100%;
  height: 320px;
  transition: opacity 0.2s ease;
}

.pie-chart.is-empty {
  opacity: 0;
}

.chart-empty {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7a8699;
  font-size: 14px;
}

@media (max-width: 640px) {
  .pie-chart {
    height: 360px;
  }
}
</style>
