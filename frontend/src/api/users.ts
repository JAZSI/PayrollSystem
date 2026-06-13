import { api } from './client'
import type { RegisterRequest, User } from '@/types/auth'

export const listUsers = () => api.get<User[]>('/users').then((r) => r.data)

export const createUser = (body: RegisterRequest) =>
  api.post<User>('/users', body).then((r) => r.data)
