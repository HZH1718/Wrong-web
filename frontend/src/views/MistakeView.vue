<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { mistakeApi, questionTypeApi, subjectApi } from '../api'
import MathText from '../components/MathText.vue'

const mistakes = ref([])
const subjects = ref([])
const questionTypes = ref([])
const loading = ref(false)
const errorMessage = ref('')
const editingId = ref(null)
const expandedIds = ref(new Set())

const form = reactive({
  subjectId: '',
  questionTypeId: '',
  questionContent: '',
  wrongAnswer: '',
  correctAnswer: '',
  entryTime: '',
  knowledgePoint: '',
  analysis: ''
})

function toggleAnalysis(mistakeId) {
  const next = new Set(expandedIds.value)

  if (next.has(mistakeId)) {
    next.delete(mistakeId)
  } else {
    next.add(mistakeId)
  }

  expandedIds.value = next
}

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

  try {
    const { data } = await mistakeApi.list()
    mistakes.value = data.code === 200 ? data.data || [] : []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '错题列表加载失败'
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingId.value = null
  form.subjectId = ''
  form.questionTypeId = ''
  form.questionContent = ''
  form.wrongAnswer = ''
  form.correctAnswer = ''
  form.entryTime = ''
  form.knowledgePoint = ''
  form.analysis = ''
}

function startEdit(mistake) {
  editingId.value = mistake.mistakeId
  form.subjectId = mistake.subjectId
  form.questionTypeId = mistake.questionTypeId
  form.questionContent = mistake.questionContent
  form.wrongAnswer = mistake.wrongAnswer
  form.correctAnswer = mistake.correctAnswer
  form.entryTime = mistake.entryTime ? mistake.entryTime.slice(0, 16) : ''
  form.knowledgePoint = mistake.knowledgePoint || ''
  form.analysis = mistake.analysis || ''
}

async function submit() {
  errorMessage.value = ''

  const payload = {
    subjectId: Number(form.subjectId),
    questionTypeId: Number(form.questionTypeId),
    questionContent: form.questionContent,
    wrongAnswer: form.wrongAnswer,
    correctAnswer: form.correctAnswer,
    entryTime: form.entryTime || null,
    knowledgePoint: form.knowledgePoint,
    analysis: form.analysis
  }

  try {
    const response = editingId.value
      ? await mistakeApi.update(editingId.value, payload)
      : await mistakeApi.create(payload)

    if (response.data.code !== 200) {
      errorMessage.value = response.data.message || '错题保存失败'
      return
    }

    resetForm()
    await loadMistakes()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '错题保存失败'
  }
}

async function removeMistake(mistake) {
  if (!window.confirm('确认删除这条错题吗？')) return

  const { data } = await mistakeApi.delete(mistake.mistakeId)

  if (data.code === 200) {
    await loadMistakes()
  } else {
    errorMessage.value = data.message || '删除失败'
  }
}

onMounted(async () => {
  await loadBaseData()
  await loadMistakes()
})
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>错题管理</h1>
        <p>录入、修改和删除当前用户的错题</p>
      </div>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <section class="panel">
      <h2 class="panel-title">{{ editingId ? '编辑错题' : '录入错题' }}</h2>

      <form class="form-grid" @submit.prevent="submit">
        <div class="field">
          <label for="subjectId">科目</label>
          <select id="subjectId" v-model="form.subjectId" required>
            <option value="" disabled>请选择科目</option>
            <option v-for="subject in subjects" :key="subject.subjectId" :value="subject.subjectId">
              {{ subject.subjectName }}
            </option>
          </select>
        </div>

        <div class="field">
          <label for="questionTypeId">题型</label>
          <select id="questionTypeId" v-model="form.questionTypeId" required>
            <option value="" disabled>请选择题型</option>
            <option
              v-for="questionType in questionTypes"
              :key="questionType.questionTypeId"
              :value="questionType.questionTypeId"
            >
              {{ questionType.typeName }}
            </option>
          </select>
        </div>

        <div class="field" style="grid-column: 1 / -1">
          <label for="questionContent">题干</label>
          <textarea id="questionContent" v-model="form.questionContent" required></textarea>
        </div>

        <div class="field">
          <label for="wrongAnswer">错误答案</label>
          <textarea id="wrongAnswer" v-model="form.wrongAnswer" required></textarea>
        </div>

        <div class="field">
          <label for="correctAnswer">正确答案</label>
          <textarea id="correctAnswer" v-model="form.correctAnswer" required></textarea>
        </div>

        <div class="field">
          <label for="entryTime">录入时间</label>
          <input id="entryTime" v-model="form.entryTime" type="datetime-local" />
        </div>

        <div class="field">
          <label for="knowledgePoint">知识点</label>
          <input id="knowledgePoint" v-model="form.knowledgePoint" />
        </div>

        <div class="field" style="grid-column: 1 / -1">
          <label for="analysis">解题过程</label>
          <textarea
            id="analysis"
            v-model="form.analysis"
            placeholder="可填写解题步骤与过程，支持 LaTeX：行内 $x^2$，行间 $$...$$"
          ></textarea>
        </div>

        <div class="button-row" style="grid-column: 1 / -1">
          <button class="button button-primary" type="submit">保存</button>
          <button class="button" type="button" @click="resetForm">取消</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <h2 class="panel-title">错题列表</h2>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="mistakes.length === 0" class="empty">暂无错题</div>

      <div v-else class="grid">
        <div v-for="mistake in mistakes" :key="mistake.mistakeId" class="question-card">
          <div class="question-grid">
            <div>
              <div class="muted">
                {{ subjectMap[mistake.subjectId] || '未知科目' }} ·
                {{ questionTypeMap[mistake.questionTypeId] || '未知题型' }}
              </div>
              <div style="margin-top: 6px; font-weight: 600">
                <MathText :text="mistake.questionContent" />
              </div>
            </div>

            <div class="button-row" style="justify-content: flex-end">
              <button class="button" type="button" @click="startEdit(mistake)">编辑</button>
              <button class="button button-danger" type="button" @click="removeMistake(mistake)">
                删除
              </button>
            </div>
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
    </section>
  </div>
</template>
