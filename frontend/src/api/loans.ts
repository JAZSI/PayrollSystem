import { api } from './client'
import type { Loan, LoanRequest } from '@/types/loan'

export const getEmployeeLoans = (employeeId: string) =>
  api.get<Loan[]>(`/employees/${employeeId}/loans`).then((r) => r.data)

export const createLoan = (body: LoanRequest) =>
  api.post<Loan>('/loans', body).then((r) => r.data)

export const updateLoan = (id: number, body: LoanRequest) =>
  api.put<Loan>(`/loans/${id}`, body).then((r) => r.data)

export const cancelLoan = (id: number) =>
  api.post<Loan>(`/loans/${id}/cancel`).then((r) => r.data)

/** Self-service: the current user's own loans. */
export const getMyLoans = () => api.get<Loan[]>('/me/loans').then((r) => r.data)
