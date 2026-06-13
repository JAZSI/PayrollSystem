import type { Employee } from './employee'

export type Role = 'ADMIN' | 'HR' | 'EMPLOYEE'

export interface Me {
  username: string
  role: Role
  employee: Employee | null
}

export const ROLES: { value: Role; label: string }[] = [
  { value: 'ADMIN', label: 'Admin' },
  { value: 'HR', label: 'HR' },
  { value: 'EMPLOYEE', label: 'Employee' },
]

export interface User {
  username: string
  role: Role
  employeeId: string | null
}

export interface RegisterRequest {
  username: string
  password: string
  role: Role
  employeeId?: string | null
}

export interface AuthResponse {
  token: string
  username: string
  role: Role
  employeeId: string | null
}

export interface LoginRequest {
  username: string
  password: string
}
