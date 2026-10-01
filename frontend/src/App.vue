<script setup>
import { computed, ref } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { clearUser, getStoredUser } from './api'
import logoUrl from './assets/logo.png'

const route = useRoute()
const router = useRouter()
const currentUser = ref(getStoredUser())

const isAuthPage = computed(() => Boolean(route.meta.public))

function logout() {
  clearUser()
  currentUser.value = null
  router.push('/login')
}
</script>

<template>
  <div v-if="isAuthPage" class="auth-shell">
    <RouterView />
  </div>

  <div v-else class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <img class="brand-logo" :src="logoUrl" alt="WrongWeb 错题管理系统" />
      </div>

      <nav>
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/subjects">科目管理</RouterLink>
        <RouterLink to="/question-types">题型管理</RouterLink>
        <RouterLink to="/mistakes">错题管理</RouterLink>
        <RouterLink to="/mistake-browse">错题展示</RouterLink>
        <RouterLink to="/papers">试卷中心</RouterLink>
      </nav>

      <div class="sidebar-user">
        <p>{{ currentUser?.account || currentUser?.userId || '当前用户' }}</p>
        <button class="button button-outline" type="button" @click="logout">
          退出登录
        </button>
      </div>
    </aside>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>
