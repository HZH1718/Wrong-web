<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeUser, userApi } from '../api'
import logoUrl from '../assets/logo.png'

const router = useRouter()
const form = reactive({
  account: '',
  password: ''
})
const errorMessage = ref('')
const loading = ref(false)

async function submit() {
  errorMessage.value = ''
  loading.value = true

  try {
    const { data } = await userApi.login(form)

    if (data.code !== 200) {
      errorMessage.value = data.message || '登录失败'
      return
    }

    storeUser(data.data)
    router.push('/')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登录失败，请检查网络或后端服务'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-card">
    <img class="auth-logo" :src="logoUrl" alt="WrongWeb 错题管理系统" />

    <h1>登录</h1>
    <p>使用注册邮箱登录</p>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <form @submit.prevent="submit">
      <div class="field">
        <label for="account">邮箱</label>
        <input
          id="account"
          v-model="form.account"
          type="email"
          required
          autocomplete="username"
          placeholder="请输入注册邮箱"
        />
      </div>

      <div class="field">
        <label for="password">密码</label>
        <input id="password" v-model="form.password" type="password" required autocomplete="current-password" />
      </div>

      <button class="button button-primary" type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>

    <div class="auth-links">
      <RouterLink class="auth-link" to="/forgot-password">忘记密码？</RouterLink>
    </div>

    <div class="button-row" style="margin-top: 14px">
      <span class="muted">还没有账号？</span>
      <RouterLink class="button button-outline" to="/register">注册</RouterLink>
    </div>
  </section>
</template>
