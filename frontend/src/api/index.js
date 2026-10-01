import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const stored = localStorage.getItem('wrongweb_user')

  if (stored) {
    try {
      const user = JSON.parse(stored)
      if (user?.userId) {
        config.headers['X-User-Id'] = user.userId
      }
    } catch (error) {
      localStorage.removeItem('wrongweb_user')
    }
  }

  return config
})

request.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(error)
)

export const userApi = {
  register: (data) => request.post('/user/register', data),
  login: (data) => request.post('/user/login', data),
  verifyIdentity: (data) => request.post('/user/verify-identity', data),
  resetPassword: (data) => request.post('/user/reset-password', data)
}

export const subjectApi = {
  list: () => request.get('/subject/list'),
  create: (data) => request.post('/subject', data),
  update: (id, data) => request.put(`/subject/${id}`, data),
  delete: (id) => request.delete(`/subject/${id}`)
}

export const questionTypeApi = {
  list: () => request.get('/question-type/list'),
  create: (data) => request.post('/question-type', data),
  update: (id, data) => request.put(`/question-type/${id}`, data),
  delete: (id) => request.delete(`/question-type/${id}`)
}

export const mistakeApi = {
  list: () => request.get('/mistake/list'),
  page: (params) => request.get('/mistake/page', { params }),
  detail: (id) => request.get(`/mistake/${id}`),
  create: (data) => request.post('/mistake', data),
  update: (id, data) => request.put(`/mistake/${id}`, data),
  delete: (id) => request.delete(`/mistake/${id}`),
  statistics: () => request.get('/mistake/statistics'),
  homeList: () => request.get('/mistake/home-list')
}

export const paperApi = {
  generate: (data) => request.post('/paper/generate', data),
  list: () => request.get('/paper/list'),
  detail: (id) => request.get(`/paper/${id}`),
  submit: (id) => request.post(`/paper/${id}/submit`)
}

export function getStoredUser() {
  const stored = localStorage.getItem('wrongweb_user')
  if (!stored) return null

  try {
    return JSON.parse(stored)
  } catch (error) {
    localStorage.removeItem('wrongweb_user')
    return null
  }
}

export function storeUser(user) {
  localStorage.setItem('wrongweb_user', JSON.stringify(user))
}

export function clearUser() {
  localStorage.removeItem('wrongweb_user')
}
