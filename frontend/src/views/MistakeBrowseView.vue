<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { mistakeApi, questionTypeApi, subjectApi } from '../api'
import MathText from '../components/MathText.vue'
import MultiSelect from '../components/MultiSelect.vue'

const subjects = ref([])
const questionTypes = ref([])
const mistakes = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const errorMessage = ref('')
const expandedIds = ref(new Set())

// 筛选条件：多选，空数组表示不筛选。
const selectedSubjectIds = ref([])
const selectedQuestionTypeIds = ref([])

const subjectOptions = computed(() =>
  subjects.value.map((subject) => ({
    value: subject.subjectId,
    label: subject.subjectName
  }))
)

const questionTypeOptions = computed(() =>
  questionTypes.value.map((questionType) => ({
    value: questionType.questionTypeId,
    label: questionType.typeName
  }))
)

const subjectMap = computed(() => {
  const map = {}
  subjects.value.forEach((subject) => {
    map[subject.subjectId] = subject.subjectName
  })
  return map
})

const questionTypeMap = computed(() => {
  const map = {}
  questionTypes.value.forEach((questionType) => {
    map[questionType.questionTypeId] = questionType.typeName
  })
  return map
})

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size.value)))

function toggleAnalysis(mistakeId) {
  const next = new Set(expandedIds.value)

  if (next.has(mistakeId)) {
    next.delete(mistakeId)
  } else {
    next.add(mistakeId)
  }

  expandedIds.value = next
}

function formatTime(value) {
  return value ? value.replace('T', ' ').slice(0, 16) : ''
}

async function loadBaseData() {
  const [subjectResponse, typeResponse] = await Promise.all([
    subjectApi.list(),
    questionTypeApi.list()
  ])

  subjects.value = subjectResponse.data.code === 200 ? subjectResponse.data.data || [] : []
  questionTypes.value = typeResponse.data.code === 200 ? typeResponse.data.data || [] : []
}

async function loadMistakes() {
  loading.value = true
  errorMessage.value = ''

  const params = { page: page.value, size: size.value }
  // 多选值以英文逗号拼接，后端按 List<Long> 接收。
  if (selectedSubjectIds.value.length > 0) {
    params.subjectId = selectedSubjectIds.value.join(',')
  }
  if (selectedQuestionTypeIds.value.length > 0) {
    params.questionTypeId = selectedQuestionTypeIds.value.join(',')
  }

  try {
    const { data } = await mistakeApi.page(params)

    if (data.code === 200) {
      mistakes.value = data.data?.list || []
      total.value = data.data?.total || 0
    } else {
      mistakes.value = []
      total.value = 0
      errorMessage.value = data.message || '错题列表加载失败'
    }
  } catch (error) {
    mistakes.value = []
    total.value = 0
    errorMessage.value = error.response?.data?.message || '错题列表加载失败'
  } finally {
    loading.value = false
  }
}

function goToPage(target) {
  if (target < 1 || target > totalPages.value || target === page.value) return
  page.value = target
}

// 科目或题型变化时自动重新请求，并回到第一页。
watch([selectedSubjectIds, selectedQuestionTypeIds], () => {
  if (page.value === 1) {
    loadMistakes()
  } else {
    page.value = 1
  }
})

watch(page, () => {
  loadMistakes()
})

onMounted(async () => {
  await loadBaseData()
  await loadMistakes()
})
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>错题展示</h1>
        <p>科目和题型均可多选（组内任意、组间同时满足），按录入时间倒序展示</p>
      </div>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <section class="panel">
      <h2 class="panel-title">筛选条件</h2>

      <div class="browse-filter">
        <div class="field">
          <label>科目</label>
          <MultiSelect
            v-model="selectedSubjectIds"
            :options="subjectOptions"
            placeholder="全部科目"
          />
        </div>

        <div class="field">
          <label>题型</label>
          <MultiSelect
            v-model="selectedQuestionTypeIds"
            :options="questionTypeOptions"
            placeholder="全部题型"
          />
        </div>
      </div>
    </section>

    <section class="panel">
      <h2 class="panel-title">错题列表</h2>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="mistakes.length === 0" class="empty">暂无符合条件的错题</div>

      <template v-else>
        <div class="grid">
          <div v-for="mistake in mistakes" :key="mistake.mistakeId" class="question-card">
            <div class="muted">
              <span class="badge badge-green">
                {{ subjectMap[mistake.subjectId] || '未知科目' }}
              </span>
              <span class="badge badge-orange" style="margin-left: 8px">
                {{ questionTypeMap[mistake.questionTypeId] || '未知题型' }}
              </span>
              <span v-if="mistake.entryTime" style="margin-left: 10px">
                录入时间：{{ formatTime(mistake.entryTime) }}
              </span>
            </div>

            <div style="margin-top: 10px; font-weight: 600">
              <MathText :text="mistake.questionContent" />
            </div>

            <div class="answer-line">
              <div><strong>错误答案：</strong><MathText :text="mistake.wrongAnswer" /></div>
              <div><strong>正确答案：</strong><MathText :text="mistake.correctAnswer" /></div>
              <div v-if="mistake.knowledgePoint">
                <strong>知识点：</strong>{{ mistake.knowledgePoint }}
              </div>
            </div>

            <div v-if="mistake.analysis" class="analysis-block">
              <button
                class="analysis-toggle"
                type="button"
                :aria-expanded="expandedIds.has(mistake.mistakeId)"
                @click="toggleAnalysis(mistake.mistakeId)"
              >
                <span
                  class="analysis-caret"
                  :class="{ 'is-open': expandedIds.has(mistake.mistakeId) }"
                >▸</span>
                解题过程
              </button>

              <div v-if="expandedIds.has(mistake.mistakeId)" class="analysis-content">
                <MathText :text="mistake.analysis" />
              </div>
            </div>
          </div>
        </div>

        <div class="pagination">
          <div class="pagination-info">
            共 {{ total }} 条 · 第 {{ page }} / {{ totalPages }} 页
          </div>
          <div class="pagination-controls">
            <button
              class="button button-outline"
              type="button"
              :disabled="page <= 1"
              @click="goToPage(page - 1)"
            >
              上一页
            </button>
            <button
              class="button button-outline"
              type="button"
              :disabled="page >= totalPages"
              @click="goToPage(page + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </template>
    </section>
  </div>
</template>
