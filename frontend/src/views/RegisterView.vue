<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import logoUrl from '../assets/logo.png'

const EMAIL_PATTERN = /^[\w.%+-]+@[\w.-]+\.[A-Za-z]{2,}$/
const PHONE_PATTERN = /^1[3-9]\d{9}$/

const router = useRouter()
const form = reactive({
  email: '',
  phone: '',
  password: '',
  birthDate: '',
  gender: 1
})
const errorMessage = ref('')
const loading = ref(false)

function validate() {
  if (!form.email.trim()) {
    return '请输入邮箱'
  }
  if (!EMAIL_PATTERN.test(form.email.trim())) {
    return '邮箱格式不正确'
  }
  if (!form.phone.trim()) {
    return '请输入手机号'
  }
  if (!PHONE_PATTERN.test(form.phone.trim())) {
    return '手机号格式不正确，必须为 11 位手机号'
  }
  if (form.password.length < 6) {
    return '密码长度不能少于6位'
  }
  if (!form.birthDate) {
    return '请选择出生日期'
  }
  return ''
}

async function submit() {
  errorMessage.value = validate()
  if (errorMessage.value) {
    return
  }

  loading.value = true

  try {
    const { data } = await userApi.register({
      email: form.email.trim(),
      phone: form.phone.trim(),
      password: form.password,
      birthDate: form.birthDate,
      gender: Number(form.gender)
    })

    if (data.code !== 200) {
      errorMessage.value = data.message || '注册失败'
      return
    }

    router.push('/login')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '注册失败，请检查网络或后端服务'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-card">
    <img class="auth-logo" :src="logoUrl" alt="WrongWeb 错题管理系统" />

    <h1>注册</h1>
    <p>创建新的错题本账号</p>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <form @submit.prevent="submit">
      <div class="field">
        <label for="email">邮箱</label>
        <input
          id="email"
          v-model="form.email"
          type="email"
          required
          autocomplete="username"
          placeholder="请输入邮箱，将作为登录账号"
        />
      </div>

      <div class="field">
        <label for="phone">手机号码</label>
        <input
          id="phone"
          v-model="form.phone"
          type="tel"
          required
          inputmode="numeric"
          maxlength="11"
          autocomplete="tel"
          placeholder="请输入 11 位手机号"
        />
      </div>

      <div class="field">
        <label for="password">密码</label>
        <input id="password" v-model="form.password" type="password" required autocomplete="new-password" />
      </div>

      <div class="field">
        <label for="birthDate">出生日期</label>
        <input id="birthDate" v-model="form.birthDate" type="date" required />
      </div>

      <div class="field">
        <label for="gender">性别</label>
        <select id="gender" v-model="form.gender" required>
          <option :value="1">男</option>
          <option :value="2">女</option>
        </select>
      </div>

      <button class="button button-primary" type="submit" :disabled="loading">
        {{ loading ? '注册中...' : '注册' }}
      </button>
    </form>

    <div class="button-row" style="margin-top: 14px">
      <span class="muted">已有账号？</span>
      <RouterLink class="button button-outline" to="/login">返回登录</RouterLink>
    </div>
  </section>
</template>
