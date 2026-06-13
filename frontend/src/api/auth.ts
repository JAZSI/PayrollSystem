import { api } from './client'
import type { AuthResponse, LoginRequest } from '@/types/auth'

export const login = (body: LoginRequest) =>
  api.post<AuthResponse>('/auth/login', body).then((r) => r.data)
