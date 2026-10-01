<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { paperApi, subjectApi } from '../api'

const router = useRouter()
const papers = ref([])
const subjects = ref([])
const loading = ref(false)
const errorMessage = ref('')

const subjectMap = computed(() => {
  const map = {}
  subjects.value.forEach((subject) => {
    map[subject.subjectId] = subject.subjectName
  })
  return map
})

const form = reactive({
  subjectId: '',
  totalCount: '',
  choiceCount: '',
  blankCount: '',
  solveCount: ''
})

async function loadData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [paperResponse, subjectResponse] = await Promise.all([
      paperApi.list(),
      subjectApi.list()
    ])

    papers.value = paperResponse.data.code === 200 ? paperResponse.data.data || [] : []
    subjects.value = subjectResponse.data.code === 200 ? subjectResponse.data.data || [] : []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '试卷数据加载失败'
  } finally {
    loading.value = false
  }
}

function toNumber(value) {
  return value === '' || value === null || value === undefined ? null : Number(value)
}

async function generatePaper() {
  errorMessage.value = ''

  const payload = {
    subjectId: Number(form.subjectId),
    totalCount: toNumber(form.totalCount),
    choiceCount: toNumber(form.choiceCount),
    blankCount: toNumber(form.blankCount),
    solveCount: toNumber(form.solveCount)
  }

  try {
    const { data } = await paperApi.generate(payload)

    if (data.code !== 200) {
      errorMessage.value = data.message || '组卷失败'
      return
    }

    router.push(`/papers/${data.data.paperId}`)
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '组卷失败'
  }
}

function openPaper(paper) {
  router.push(`/papers/${paper.paperId}`)
}

onMounted(loadData)
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>试卷中心</h1>
        <p>按科目随机组卷，永久保存历史试卷</p>
      </div>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <section class="panel">
      <h2 class="panel-title">随机组卷</h2>
      <form class="form-grid" @submit.prevent="generatePaper">
        <div class="field">
          <label for="paperSubjectId">抽题科目</label>
          <select id="paperSubjectId" v-model="form.subjectId" required>
            <option value="" disabled>请选择科目</option>
            <option v-for="subject in subjects" :key="subject.subjectId" :value="subject.subjectId">
              {{ subject.subjectName }}
            </option>
          </select>
        </div>

        <div class="field">
          <label for="totalCount">总题数</label>
          <input id="totalCount" v-model="form.totalCount" type="number" min="0" placeholder="默认 22" />
        </div>

        <div class="field">
          <label for="choiceCount">选择题数量</label>
          <input id="choiceCount" v-model="form.choiceCount" type="number" min="0" placeholder="默认 12" />
        </div>

        <div class="field">
          <label for="blankCount">填空题数量</label>
          <input id="blankCount" v-model="form.blankCount" type="number" min="0" placeholder="默认 5" />
        </div>

        <div class="field">
          <label for="solveCount">解答题数量</label>
          <input id="solveCount" v-model="form.solveCount" type="number" min="0" placeholder="默认 5" />
        </div>

        <div class="button-row" style="grid-column: 1 / -1">
          <button class="button button-primary" type="submit">生成试卷</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <h2 class="panel-title">历史试卷</h2>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="papers.length === 0" class="empty">暂无历史试卷</div>

      <table v-else class="table">
        <thead>
          <tr>
            <th>试卷ID</th>
            <th>科目</th>
            <th>总题数</th>
            <th>选择题</th>
            <th>填空题</th>
            <th>解答题</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="paper in papers" :key="paper.paperId">
            <td>{{ paper.paperId }}</td>
            <td>{{ subjectMap[paper.subjectId] || paper.subjectId }}</td>
            <td>{{ paper.totalCount }}</td>
            <td>{{ paper.choiceCount }}</td>
            <td>{{ paper.blankCount }}</td>
            <td>{{ paper.solveCount }}</td>
            <td>
              <span :class="['badge', paper.status === 1 ? 'badge-green' : 'badge-orange']">
                {{ paper.status === 1 ? '已提交' : '未提交' }}
              </span>
            </td>
            <td>
              <button class="button" type="button" @click="openPaper(paper)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>
