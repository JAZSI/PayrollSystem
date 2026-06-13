import type { Role } from '@/types/auth'

/** Staff (ADMIN/HR) manage payroll; EMPLOYEE self-serves. */
export const isStaff = (role?: Role) => role === 'ADMIN' || role === 'HR'

/** Only ADMIN can manage user accounts. */
export const isAdmin = (role?: Role) => role === 'ADMIN'
