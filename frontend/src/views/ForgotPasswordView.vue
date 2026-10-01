<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import logoUrl from '../assets/logo.png'

const EMAIL_PATTERN = /^[\w.%+-]+@[\w.-]+\.[A-Za-z]{2,}$/
const PHONE_PATTERN = /^1[3-9]\d{9}$/

const router = useRouter()
const step = ref(1)
const done = ref(false)
const verifyForm = reactive({ email: '', phone: '' })
const resetForm = reactive({ newPassword: '', confirmPassword: '' })
const resetToken = ref('')
const errorMessage = ref('')
const loading = ref(false)

async function verify() {
  errorMessage.value = ''

  if (!verifyForm.email.trim()) {
    errorMessage.value = '请输入邮箱'
    return
  }
  if (!EMAIL_PATTERN.test(verifyForm.email.trim())) {
    errorMessage.value = '邮箱格式不正确'
    return
  }
  if (!verifyForm.phone.trim()) {
    errorMessage.value = '请输入手机号'
    return
  }
  if (!PHONE_PATTERN.test(verifyForm.phone.trim())) {
    errorMessage.value = '手机号格式不正确，必须为 11 位手机号'
    return
  }

  loading.value = true

  try {
    const { data } = await userApi.verifyIdentity({
      email: verifyForm.email.trim(),
      phone: verifyForm.phone.trim()
    })

    if (data.code !== 200) {
      errorMessage.value = data.message || '账号不存在或信息不符'
      return
    }

    resetToken.value = data.data.token
    step.value = 2
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '验证失败，请检查网络或后端服务'
  } finally {
    loading.value = false
  }
}

async function reset() {
  errorMessage.value = ''

  if (resetForm.newPassword.length < 6) {
    errorMessage.value = '密码长度不能少于6位'
    return
  }
  if (resetForm.newPassword !== resetForm.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  loading.value = true

  try {
    const { data } = await userApi.resetPassword({
      token: resetToken.value,
      newPassword: resetForm.newPassword
    })

    if (data.code !== 200) {
      errorMessage.value = data.message || '密码修改失败'
      return
    }

    done.value = true
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '密码修改失败，请检查网络或后端服务'
  } finally {
    loading.value = false
  }
}

function backToVerify() {
  step.value = 1
  done.value = false
  errorMessage.value = ''
  resetToken.value = ''
  resetForm.newPassword = ''
  resetForm.confirmPassword = ''
}
</script>

<template>
  <section class="auth-card">
    <img class="auth-logo" :src="logoUrl" alt="WrongWeb 错题管理系统" />

    <h1>忘记密码</h1>
    <p v-if="done">密码已修改成功，请使用新密码登录</p>
    <p v-else-if="step === 1">第一步：验证注册邮箱与手机号</p>
    <p v-else>第二步：设置新密码</p>

    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>

    <template v-if="done">
      <RouterLink class="button button-primary" to="/login">返回登录</RouterLink>
    </template>

    <template v-else-if="step === 1">
      <form @submit.prevent="verify">
        <div class="field">
          <label for="email">邮箱</label>
          <input
            id="email"
            v-model="verifyForm.email"
            type="email"
            required
            autocomplete="username"
            placeholder="请输入注册邮箱"
          />
        </div>

        <div class="field">
          <label for="phone">手机号码</label>
          <input
            id="phone"
            v-model="verifyForm.phone"
            type="tel"
            required
            inputmode="numeric"
            maxlength="11"
            autocomplete="tel"
            placeholder="请输入注册时填写的手机号"
          />
        </div>

        <button class="button button-primary" type="submit" :disabled="loading">
          {{ loading ? '验证中...' : '验证' }}
        </button>
      </form>

      <div class="button-row" style="margin-top: 14px">
        <span class="muted">想起密码了？</span>
        <RouterLink class="button button-outline" to="/login">返回登录</RouterLink>
      </div>
    </template>

    <template v-else>
      <form @submit.prevent="reset">
        <div class="field">
          <label for="newPassword">新密码</label>
          <input
            id="newPassword"
            v-model="resetForm.newPassword"
            type="password"
            required
            autocomplete="new-password"
            placeholder="请输入不少于 6 位的新密码"
          />
        </div>

        <div class="field">
          <label for="confirmPassword">确认新密码</label>
          <input
            id="confirmPassword"
            v-model="resetForm.confirmPassword"
            type="password"
            required
            autocomplete="new-password"
            placeholder="请再次输入新密码"
          />
        </div>

        <button class="button button-primary" type="submit" :disabled="loading">
          {{ loading ? '提交中...' : '确认修改' }}
        </button>
      </form>

      <div class="button-row" style="margin-top: 14px">
        <button class="button button-outline" type="button" @click="backToVerify">返回上一步</button>
        <RouterLink class="button button-outline" to="/login">返回登录</RouterLink>
      </div>
    </template>
  </section>
</template>
