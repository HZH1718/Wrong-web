<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { paperApi } from '../api'
import MathText from '../components/MathText.vue'

const route = useRoute()
const paper = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const expandedIds = ref(new Set())

function toggleAnalysis(key) {
  const next = new Set(expandedIds.value)

  if (next.has(key)) {
    next.delete(key)
  } else {
    next.add(key)
  }

  expandedIds.value = next
}

async function loadPaper() {
  loading.value = true
  errorMessage.value = ''

  try {
    const { data } = await paperApi.detail(route.params.paperId)

    if (data.code !== 200) {
      errorMessage.value = data.message || '试卷加载失败'
      return
    }

    paper.value = data.data
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '试卷加载失败'
  } finally {
    loading.value = false
  }
}

async function submitPaper() {
  if (!window.confirm('确认提交试卷吗？提交后可以查看完整答案。')) return

  const { data } = await paperApi.submit(route.params.paperId)

  if (data.code === 200) {
    await loadPaper()
  } else {
    errorMessage.value = data.message || '提交失败'
  }
}

onMounted(loadPaper)
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>试卷详情</h1>
        <p v-if="paper">试卷 #{{ paper.paperId }}</p>
      </div>

      <button
        v-if="paper && paper.status === 0"
        class="button button-primary"
        type="button"
        @click="submitPaper"
      >
        提交试卷
      </button>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-if="loading" class="empty">加载中...</div>

    <section v-else-if="paper" class="grid grid-4" style="margin-bottom: 20px">
      <div class="panel" style="margin: 0">
        <div class="muted">状态</div>
        <div>
          <span :class="['badge', paper.status === 1 ? 'badge-green' : 'badge-orange']">
            {{ paper.status === 1 ? '已提交' : '未提交' }}
          </span>
        </div>
      </div>

      <div class="panel" style="margin: 0">
        <div class="muted">总题数</div>
        <div style="font-size: 24px; font-weight: 700">{{ paper.totalCount }}</div>
      </div>

      <div class="panel" style="margin: 0">
        <div class="muted">选择题 / 填空 / 解答</div>
        <div style="font-size: 20px; font-weight: 700">
          {{ paper.choiceCount }} / {{ paper.blankCount }} / {{ paper.solveCount }}
        </div>
      </div>

      <div class="panel" style="margin: 0">
        <div class="muted">提交时间</div>
        <div>{{ paper.submittedAt || '-' }}</div>
      </div>
    </section>

    <section v-if="paper" class="grid">
      <div
        v-for="(question, index) in paper.questions"
        :key="question.mistakeId"
        class="question-card"
      >
        <div class="muted">第 {{ index + 1 }} 题</div>
        <div style="margin-top: 8px; font-weight: 600">
          <MathText :text="question.questionContent" />
        </div>

        <div v-if="question.knowledgePoint" class="muted" style="margin-top: 6px">
          知识点：{{ question.knowledgePoint }}
        </div>

        <div v-if="paper.status === 1" class="answer-line">
          <div><strong>错误答案：</strong><MathText :text="question.wrongAnswer" /></div>
          <div><strong>正确答案：</strong><MathText :text="question.correctAnswer" /></div>
        </div>

        <div v-if="paper.status === 1 && question.analysis" class="analysis-block">
          <button
            class="analysis-toggle"
            type="button"
            :aria-expanded="expandedIds.has(`${question.mistakeId}-${index}`)"
            @click="toggleAnalysis(`${question.mistakeId}-${index}`)"
          >
            <span
              class="analysis-caret"
              :class="{ 'is-open': expandedIds.has(`${question.mistakeId}-${index}`) }"
            >▸</span>
            解题过程
          </button>

          <div v-if="expandedIds.has(`${question.mistakeId}-${index}`)" class="analysis-content">
            <MathText :text="question.analysis" />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
