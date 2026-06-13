import { api } from './client'
import type { Me } from '@/types/auth'

export const getMe = () => api.get<Me>('/me').then((r) => r.data)
