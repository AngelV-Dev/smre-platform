import api from './axiosInstance'

export const loginApi = (email, password) =>
  api.post('/api/v1/auth/login', { email, password })

export const logoutApi = () =>
  api.post('/api/v1/auth/logout')

export const getMeApi = () =>
  api.get('/api/v1/auth/me')
