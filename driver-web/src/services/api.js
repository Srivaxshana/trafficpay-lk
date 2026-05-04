import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

const api = axios.create({ baseURL: BASE_URL })

export const lookupFine = (referenceNumber, categoryCode) =>
  api.get('/fines/lookup', { params: { referenceNumber, categoryCode } })

export const payFine = (paymentData) =>
  api.post('/payments/pay', paymentData)
