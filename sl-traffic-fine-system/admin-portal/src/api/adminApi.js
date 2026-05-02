import axios from 'axios'

let authToken = null

export const setToken = (token) => { authToken = token }
export const clearToken = () => { authToken = null }

const api = axios.create({ baseURL: '/api/v1' })

api.interceptors.request.use(config => {
  if (authToken) config.headers.Authorization = `Bearer ${authToken}`
  return config
})

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      clearToken()
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const login = (badgeNumber, password) =>
  api.post('/auth/login', { badgeNumber, password })

export const getSummary = (from, to) =>
  api.get('/admin/dashboard/summary', { params: { from, to } })

export const getByDistrict = (from, to) =>
  api.get('/admin/dashboard/by-district', { params: { from, to } })

export const getByCategory = (from, to, district) =>
  api.get('/admin/dashboard/by-category', { params: { from, to, district } })

export const getRecentPayments = () =>
  api.get('/admin/dashboard/recent-payments')

export const getAdminFines = (params) =>
  api.get('/admin/fines', { params })

export const createOfficer = (data) =>
  api.post('/admin/officers', data)

export const cancelFine = (fineId, reason) =>
  api.patch(`/fines/${fineId}/cancel`, { reason })
