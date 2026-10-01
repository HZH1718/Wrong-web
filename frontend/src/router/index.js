import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ForgotPasswordView from '../views/ForgotPasswordView.vue'
import SubjectView from '../views/SubjectView.vue'
import QuestionTypeView from '../views/QuestionTypeView.vue'
import MistakeView from '../views/MistakeView.vue'
import MistakeBrowseView from '../views/MistakeBrowseView.vue'
import PaperView from '../views/PaperView.vue'
import PaperDetailView from '../views/PaperDetailView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true }
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { public: true }
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: ForgotPasswordView,
      meta: { public: true }
    },
    {
      path: '/subjects',
      name: 'subjects',
      component: SubjectView,
      meta: { requiresAuth: true }
    },
    {
      path: '/question-types',
      name: 'question-types',
      component: QuestionTypeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/mistakes',
      name: 'mistakes',
      component: MistakeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/mistake-browse',
      name: 'mistake-browse',
      component: MistakeBrowseView,
      meta: { requiresAuth: true }
    },
    {
      path: '/papers',
      name: 'papers',
      component: PaperView,
      meta: { requiresAuth: true }
    },
    {
      path: '/papers/:paperId',
      name: 'paper-detail',
      component: PaperDetailView,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to) => {
  const user = localStorage.getItem('wrongweb_user')

  if (to.meta.requiresAuth && !user) {
    return { name: 'login' }
  }

  if (to.meta.public && user) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
