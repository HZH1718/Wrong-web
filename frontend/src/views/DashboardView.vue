<script setup>
import { onMounted, ref } from 'vue'
import { mistakeApi } from '../api'
import SubjectPieChart from '../components/SubjectPieChart.vue'
import MathText from '../components/MathText.vue'

const loading = ref(false)
const errorMessage = ref('')
const statistics = ref({
  totalCount: 0,
  subjectGroups: []
})
const homeGroups = ref([])

async function loadData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [statisticsResponse, homeResponse] = await Promise.all([
      mistakeApi.statistics(),
      mistakeApi.homeList()
    ])

    if (statisticsResponse.data.code === 200) {
      statistics.value = statisticsResponse.data.data || {
        totalCount: 0,
        subjectGroups: []
      }
    }

    if (homeResponse.data.code === 200) {
      homeGroups.value = homeResponse.data.data || []
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '首页数据加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>错题首页</h1>
        <p>查看当前错题总量和按科目分组情况</p>
      </div>
      <button class="button button-outline" type="button" :disabled="loading" @click="loadData">
        刷新
      </button>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <section class="grid grid-4" style="margin-bottom: 20px">
      <div class="panel" style="margin: 0">
        <div class="muted">错题总数</div>
        <div style="font-size: 30px; font-weight: 700">{{ statistics.totalCount }}</div>
      </div>

      <div
        v-for="group in statistics.subjectGroups"
        :key="group.subjectId"
        class="panel"
        style="margin: 0"
      >
        <div class="muted">{{ group.subjectName || '未命名科目' }}</div>
        <div style="font-size: 30px; font-weight: 700">{{ group.mistakeCount }}</div>
      </div>
    </section>

    <SubjectPieChart :groups="statistics.subjectGroups" />

    <section>
      <h2 class="panel-title">按科目查看错题</h2>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="homeGroups.length === 0" class="panel empty">暂无错题</div>

      <div v-else class="grid">
        <div v-for="group in homeGroups" :key="group.subjectId" class="panel" style="margin: 0">
          <h3 class="panel-title">{{ group.subjectName || '未命名科目' }}</h3>

          <table class="table">
            <thead>
              <tr>
                <th>题目</th>
                <th>录入时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="mistake in group.mistakes" :key="mistake.mistakeId">
                <td><MathText :text="mistake.questionContent" /></td>
                <td class="muted">{{ mistake.entryTime || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </div>
</template>
