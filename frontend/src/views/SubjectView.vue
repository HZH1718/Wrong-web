<script setup>
import { onMounted, reactive, ref } from 'vue'
import { subjectApi } from '../api'

const subjects = ref([])
const loading = ref(false)
const errorMessage = ref('')
const editingId = ref(null)
const form = reactive({
  subjectName: ''
})

async function loadSubjects() {
  loading.value = true
  errorMessage.value = ''

  try {
    const { data } = await subjectApi.list()
    subjects.value = data.code === 200 ? data.data || [] : []
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '科目列表加载失败'
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingId.value = null
  form.subjectName = ''
}

function startEdit(subject) {
  editingId.value = subject.subjectId
  form.subjectName = subject.subjectName
}

async function submit() {
  errorMessage.value = ''

  try {
    const payload = { subjectName: form.subjectName }
    const response = editingId.value
      ? await subjectApi.update(editingId.value, payload)
      : await subjectApi.create(payload)

    if (response.data.code !== 200) {
      errorMessage.value = response.data.message || '保存失败'
      return
    }

    resetForm()
    await loadSubjects()
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '保存失败'
  }
}

async function removeSubject(subject) {
  if (!window.confirm(`确认删除科目“${subject.subjectName}”吗？`)) return

  const { data } = await subjectApi.delete(subject.subjectId)

  if (data.code === 200) {
    await loadSubjects()
  } else {
    errorMessage.value = data.message || '删除失败'
  }
}

onMounted(loadSubjects)
</script>

<template>
  <div>
    <header class="page-header">
      <div>
        <h1>科目管理</h1>
        <p>维护高数、线代、概率论等科目</p>
      </div>
    </header>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <section class="panel">
      <h2 class="panel-title">{{ editingId ? '编辑科目' : '新增科目' }}</h2>
      <form class="form-grid" @submit.prevent="submit">
        <div class="field">
          <label for="subjectName">科目名称</label>
          <input id="subjectName" v-model="form.subjectName" required />
        </div>

        <div class="button-row" style="align-self: end">
          <button class="button button-primary" type="submit">保存</button>
          <button class="button" type="button" @click="resetForm">取消</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <h2 class="panel-title">科目列表</h2>

      <div v-if="loading" class="empty">加载中...</div>
      <div v-else-if="subjects.length === 0" class="empty">暂无科目</div>

      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>科目名称</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="subject in subjects" :key="subject.subjectId">
            <td>{{ subject.subjectId }}</td>
            <td>{{ subject.subjectName }}</td>
            <td>
              <div class="button-row">
                <button class="button" type="button" @click="startEdit(subject)">编辑</button>
                <button class="button button-danger" type="button" @click="removeSubject(subject)">
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
