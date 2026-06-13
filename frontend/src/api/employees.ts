import { api } from './client'
import type { Employee, EmployeeRequest } from '@/types/employee'

export const listEmployees = () =>
  api.get<Employee[]>('/employees').then((r) => r.data)

export const getEmployee = (id: string) =>
  api.get<Employee>(`/employees/${id}`).then((r) => r.data)

export const createEmployee = (body: EmployeeRequest) =>
  api.post<Employee>('/employees', body).then((r) => r.data)

export const updateEmployee = (id: string, body: EmployeeRequest) =>
  api.put<Employee>(`/employees/${id}`, body).then((r) => r.data)

export const deactivateEmployee = (id: string) =>
  api.delete(`/employees/${id}`).then(() => undefined)
