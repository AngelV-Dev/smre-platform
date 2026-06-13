import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
})

// Interceptor de request — agrega el token en cada petición
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('smre_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Interceptor de response — si 401, redirige a login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem('smre_token')
      sessionStorage.removeItem('smre_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api