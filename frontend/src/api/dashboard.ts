import { api } from './client'
import type { Dashboard } from '@/types/payroll'

export const getDashboard = () =>
  api.get<Dashboard>('/dashboard').then((r) => r.data)
