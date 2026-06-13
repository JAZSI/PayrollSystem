import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './AuthContext'
import { isAdmin } from './roles'

/** Restricts a route subtree to ADMIN. */
export function AdminRoute() {
  const { user } = useAuth()
  return isAdmin(user?.role) ? <Outlet /> : <Navigate to="/" replace />
}
