import { createContext, useContext, useState, type ReactNode } from 'react'
import { clearAuth, loadAuth, saveAuth } from './token'
import { login as apiLogin } from '@/api/auth'
import type { AuthResponse, LoginRequest } from '@/types/auth'

interface AuthState {
  user: AuthResponse | null
  isAuthenticated: boolean
  login: (creds: LoginRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthResponse | null>(() => loadAuth())

  async function login(creds: LoginRequest) {
    const auth = await apiLogin(creds)
    saveAuth(auth)
    setUser(auth)
  }

  function logout() {
    clearAuth()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: Boolean(user), login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
