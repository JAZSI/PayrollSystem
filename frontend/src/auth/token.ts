import type { AuthResponse } from '@/types/auth'

const KEY = 'pp_auth'

/** Non-React auth storage so the axios interceptor can read the token. */
export function loadAuth(): AuthResponse | null {
  const raw = localStorage.getItem(KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthResponse
  } catch {
    return null
  }
}

export function saveAuth(auth: AuthResponse) {
  localStorage.setItem(KEY, JSON.stringify(auth))
}

export function clearAuth() {
  localStorage.removeItem(KEY)
}

export function getToken(): string | null {
  return loadAuth()?.token ?? null
}
