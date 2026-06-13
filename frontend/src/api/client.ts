import axios from 'axios'
import { clearAuth, getToken } from '@/auth/token'

/** Axios instance; '/api' is proxied to the Spring Boot backend (see vite.config.ts). */
export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Attach the JWT to every request.
api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// On 401, clear auth and redirect (except the login request itself).
api.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status
    const url: string = error?.config?.url ?? ''
    if (status === 401 && !url.includes('/auth/login')) {
      clearAuth()
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export interface ApiErrorShape {
  message: string
  fieldErrors?: Record<string, string>
}

/** Normalizes any thrown error into the backend's ApiError shape. */
export function toApiError(err: unknown): ApiErrorShape {
  if (axios.isAxiosError(err) && err.response?.data) {
    const data = err.response.data as Partial<ApiErrorShape>
    return {
      message: data.message ?? 'Request failed',
      fieldErrors: data.fieldErrors,
    }
  }
  return { message: 'Network error — is the backend running?' }
}
