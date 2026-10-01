<script setup>
import { onMounted, reactive, ref } from 'vue'
import { questionTypeApi } from '../api'

const questionTypes = ref([])
const loading = ref(false)
const errorMessage = ref('')
const editingId = ref(null)
const form = reactive({
  typeName: ''
})

async function loadQuestionTypes() {
  loading.value = true
  errorMessage.value = ''

  try {
    const { data } = await questionTypeApi.list()
    questionTypes.value = data.code === 200 ? data.data || [] : []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '题型列表加载失败'
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingId.value = null
  form.typeName = ''
}

function startEdit(questionType) {
  editingId.value = questionType.questionTypeId
  form.typeName = questionType.typeName
}

async function submit() {
  errorMessage.value = ''

  try {
    const payload = { typeName: form.typeName }
    const response = editingId.value
      ? await questionTypeApi.update(editingId.value, payload)
      : await questionTypeApi.create(payload)

    if (response.data.code !== 200) {
      errorMessage.value = response.data.message || '保存失败'
      return
    }

    resetForm()
    await loadQuestionTypes()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '保存失败'
  }
}

async function removeQuestionType(questionType) {
  if (!window.confirm(`确认删除题型“${questionType.typeName}”吗？`)) return

  const { data } = await questionTypeApi.delete(questionType.questionTypeId)

  if (data.code === 200) {
    await loadQuestionTypes()
  } else {
    errorMessage.value = data.message || '删除失败'
  }
}

onMounted(loadQuestionTypes)
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>题型管理</h1>
        <p>维护选择、填空、解答等题型</p>
      </div>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <section class="panel">
      <h2 class="panel-title">{{ editingId ? '编辑题型' : '新增题型' }}</h2>
      <form class="form-grid" @submit.prevent="submit">
        <div class="field">
          <label for="typeName">题型名称</label>
          <input id="typeName" v-model="form.typeName" required />
        </div>

        <div class="button-row" style="align-self: end">
          <button class="button button-primary" type="submit">保存</button>
          <button class="button" type="button" @click="resetForm">取消</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <h2 class="panel-title">题型列表</h2>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="questionTypes.length === 0" class="empty">暂无题型</div>

      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>题型名称</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="questionType in questionTypes" :key="questionType.questionTypeId">
            <td>{{ questionType.questionTypeId }}</td>
            <td>{{ questionType.typeName }}</td>
            <td>
              <div class="button-row">
                <button class="button" type="button" @click="startEdit(questionType)">编辑</button>
                <button class="button button-danger" type="button" @click="removeQuestionType(questionType)">
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>
