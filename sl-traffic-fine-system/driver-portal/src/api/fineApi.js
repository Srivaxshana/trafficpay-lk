import axios from 'axios'

const api = axios.create({ baseURL: '/api/v1' })

export const lookupFine = (ref, cat) =>
  api.get('/fines/lookup', { params: { ref, cat } })

export const getCategories = () =>
  api.get('/categories')

export const initiatePayment = (data) =>
  api.post('/payments/initiate', data)

export const confirmPayment = (data) =>
  api.post('/payments/confirm', data)

export const getReceipt = (paymentId) =>
  api.get(`/payments/${paymentId}/receipt`)
