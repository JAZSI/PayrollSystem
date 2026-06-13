import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faMoneyCheckDollar, faRightToBracket } from '@fortawesome/free-solid-svg-icons'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/auth/AuthContext'
import { toApiError } from '@/api/client'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login({ username, password })
      navigate('/', { replace: true })
    } catch (err) {
      setError(toApiError(err).message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="bg-background flex min-h-screen items-center justify-center p-6">
      <Card className="w-full max-w-sm">
        <CardHeader className="text-center">
          <div className="mb-2 flex justify-center">
            <FontAwesomeIcon
              icon={faMoneyCheckDollar}
              className="text-primary text-3xl"
            />
          </div>
          <CardTitle>PayrollPal</CardTitle>
          <CardDescription>Sign in to manage payroll</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={onSubmit} className="grid gap-4">
            <div className="grid gap-1.5">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoFocus
              />
            </div>
            <div className="grid gap-1.5">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            {error && <p className="text-destructive text-sm">{error}</p>}
            <Button type="submit" disabled={loading}>
              <FontAwesomeIcon icon={faRightToBracket} className="mr-2" />
              {loading ? 'Signing in…' : 'Sign in'}
            </Button>
            <p className="text-muted-foreground text-center text-xs">
              Default admin: <code>admin</code> / <code>admin123</code>
            </p>
          </form>
          <div className="mt-4 text-center">
            <button
              type="button"
              onClick={() => navigate('/kiosk')}
              className="text-muted-foreground hover:text-foreground text-xs underline"
            >
              Open Time Clock Kiosk →
            </button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
