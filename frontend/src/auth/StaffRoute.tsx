import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './AuthContext'
import { isStaff } from './roles'

/** Restricts a route subtree to ADMIN/HR; EMPLOYEE is redirected to self-service. */
export function StaffRoute() {
  const { user } = useAuth()
  return isStaff(user?.role) ? <Outlet /> : <Navigate to="/my-payslips" replace />
}
