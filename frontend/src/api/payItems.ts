import { api } from './client'
import type { PayItem, PayItemRequest } from '@/types/payItem'

export const getEmployeePayItems = (employeeId: string) =>
  api.get<PayItem[]>(`/employees/${employeeId}/pay-items`).then((r) => r.data)

export const createPayItem = (body: PayItemRequest) =>
  api.post<PayItem>('/pay-items', body).then((r) => r.data)

export const updatePayItem = (id: number, body: PayItemRequest) =>
  api.put<PayItem>(`/pay-items/${id}`, body).then((r) => r.data)

export const deletePayItem = (id: number) =>
  api.delete(`/pay-items/${id}`).then(() => undefined)
