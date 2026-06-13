import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUserShield, faUserPlus } from '@fortawesome/free-solid-svg-icons'
import { toast } from 'sonner'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useUsers, useCreateUser } from '@/hooks/useUsers'
import { useEmployees } from '@/hooks/useEmployees'
import { toApiError } from '@/api/client'
import { ROLES, type Role } from '@/types/auth'

export function UsersPage() {
  const { data: users, isLoading } = useUsers()
  const { data: employees } = useEmployees()
  const create = useCreateUser()

  const [open, setOpen] = useState(false)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<Role>('HR')
  const [employeeId, setEmployeeId] = useState('')
  const [errors, setErrors] = useState<Record<string, string>>({})

  function reset() {
    setUsername('')
    setPassword('')
    setRole('HR')
    setEmployeeId('')
    setErrors({})
  }

  function submit() {
    setErrors({})
    create.mutate(
      {
        username: username.trim(),
        password,
        role,
        employeeId: role === 'EMPLOYEE' ? employeeId || null : null,
      },
      {
        onSuccess: (u) => {
          toast.success(`User "${u.username}" created`)
          reset()
          setOpen(false)
        },
        onError: (err) => {
          const e = toApiError(err)
          setErrors(e.fieldErrors ?? {})
          toast.error(e.message)
        },
      },
    )
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faUserShield} className="text-muted-foreground" />
            Users
          </CardTitle>
          <CardDescription>Manage login accounts and roles (admin only).</CardDescription>
        </div>
        <Button onClick={() => setOpen(true)}>
          <FontAwesomeIcon icon={faUserPlus} className="mr-2" />
          Add user
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading && <p className="text-muted-foreground text-sm">Loading…</p>}
        {users && (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Username</TableHead>
                <TableHead>Role</TableHead>
                <TableHead>Linked employee</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {users.map((u) => (
                <TableRow key={u.username}>
                  <TableCell className="font-medium">{u.username}</TableCell>
                  <TableCell>
                    <Badge variant={u.role === 'ADMIN' ? 'default' : 'secondary'}>
                      {u.role}
                    </Badge>
                  </TableCell>
                  <TableCell className="font-mono text-sm">
                    {u.employeeId ?? '—'}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Add user</DialogTitle>
            <DialogDescription>Create a login account with a role.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-2">
            <div className="grid gap-1.5">
              <Label>Username</Label>
              <Input value={username} onChange={(e) => setUsername(e.target.value)} />
              {errors.username && (
                <p className="text-destructive text-sm">{errors.username}</p>
              )}
            </div>
            <div className="grid gap-1.5">
              <Label>Password</Label>
              <Input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              {errors.password && (
                <p className="text-destructive text-sm">{errors.password}</p>
              )}
            </div>
            <div className="grid gap-1.5">
              <Label>Role</Label>
              <Select value={role} onValueChange={(v) => setRole((v as Role) ?? 'HR')}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {ROLES.map((r) => (
                    <SelectItem key={r.value} value={r.value}>
                      {r.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            {role === 'EMPLOYEE' && (
              <div className="grid gap-1.5">
                <Label>Linked employee</Label>
                <Select value={employeeId} onValueChange={(v) => setEmployeeId(v ?? '')}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select employee" />
                  </SelectTrigger>
                  <SelectContent>
                    {employees?.map((e) => (
                      <SelectItem key={e.id} value={e.id}>
                        {e.fullName} ({e.id})
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <p className="text-muted-foreground text-xs">
                  Required so the employee sees their own payslips.
                </p>
              </div>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpen(false)}>
              Cancel
            </Button>
            <Button onClick={submit} disabled={create.isPending}>
              {create.isPending ? 'Saving…' : 'Create user'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  )
}
