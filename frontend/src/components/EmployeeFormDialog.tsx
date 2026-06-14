import { useEffect, useState, type ReactNode } from 'react'
import { toast } from 'sonner'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useCreateEmployee, useUpdateEmployee } from '@/hooks/useEmployees'
import { toApiError } from '@/api/client'
import { EMPLOYEE_TYPES, type Employee, type EmployeeType } from '@/types/employee'

const empty = {
  id: '',
  fullName: '',
  type: 'REGULAR' as EmployeeType,
  monthlyRate: '',
  hourlyRate: '',
  bankAccount: '',
  password: '',
}

export function EmployeeFormDialog({
  open,
  onOpenChange,
  employee,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  employee?: Employee | null
}) {
  const isEdit = Boolean(employee)
  const [form, setForm] = useState(empty)
  const [errors, setErrors] = useState<Record<string, string>>({})
  const create = useCreateEmployee()
  const update = useUpdateEmployee()

  useEffect(() => {
    if (!open) return
    setErrors({})
    setForm(
      employee
        ? {
            id: employee.id,
            fullName: employee.fullName,
            type: employee.type,
            monthlyRate: employee.type === 'PART_TIMER' ? '' : String(employee.monthlyRate),
            hourlyRate: employee.type === 'PART_TIMER' ? String(employee.hourlyRate) : '',
            bankAccount: employee.bankAccount ?? '',
            password: '',
          }
        : empty,
    )
  }, [open, employee])

  const isPartTimer = form.type === 'PART_TIMER'

  function update_<K extends keyof typeof form>(key: K, value: (typeof form)[K]) {
    setForm((f) => ({ ...f, [key]: value }))
  }

  function submit() {
    setErrors({})
    const body = {
      id: form.id.trim(),
      fullName: form.fullName.trim(),
      type: form.type,
      monthlyRate: isPartTimer ? 0 : Number(form.monthlyRate || 0),
      hourlyRate: isPartTimer ? Number(form.hourlyRate || 0) : 0,
      bankAccount: form.bankAccount.trim() ? form.bankAccount.trim() : null,
      ...(isEdit ? {} : { password: form.password ? form.password : null }),
    }
    const onSuccess = (emp: Employee) => {
      toast.success(isEdit ? `Employee "${emp.fullName}" updated` : `Employee "${emp.fullName}" added`)
      onOpenChange(false)
    }
    const onError = (err: unknown) => {
      const e = toApiError(err)
      setErrors(e.fieldErrors ?? {})
      toast.error(e.message)
    }
    if (isEdit) update.mutate({ id: form.id, body }, { onSuccess, onError })
    else create.mutate(body, { onSuccess, onError })
  }

  const saving = create.isPending || update.isPending

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Edit Employee' : 'Add Employee'}</DialogTitle>
          <DialogDescription>
            {isEdit
              ? 'Update the employee record.'
              : 'Create an employee. Set a password to give them a login (username = ID).'}
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-2">
          <Field label="Employee ID" error={errors.id}>
            <Input
              placeholder="1234-5678-90"
              value={form.id}
              disabled={isEdit}
              onChange={(e) => update_('id', e.target.value)}
            />
          </Field>

          <Field label="Full Name" error={errors.fullName}>
            <Input
              placeholder="Juan Dela Cruz"
              value={form.fullName}
              onChange={(e) => update_('fullName', e.target.value)}
            />
          </Field>

          <Field label="Type">
            <Select value={form.type} onValueChange={(v) => update_('type', (v as EmployeeType) ?? form.type)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {EMPLOYEE_TYPES.map((t) => (
                  <SelectItem key={t.value} value={t.value}>
                    {t.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>

          {isPartTimer ? (
            <Field label="Hourly Rate (PHP)" error={errors.hourlyRate}>
              <Input
                type="number"
                min={0}
                value={form.hourlyRate}
                onChange={(e) => update_('hourlyRate', e.target.value)}
              />
            </Field>
          ) : (
            <Field label="Monthly Rate (PHP)" error={errors.monthlyRate}>
              <Input
                type="number"
                min={0}
                value={form.monthlyRate}
                onChange={(e) => update_('monthlyRate', e.target.value)}
              />
            </Field>
          )}

          <Field label="Bank Account (optional)" error={errors.bankAccount}>
            <Input
              placeholder="e.g. BDO 1234567890"
              value={form.bankAccount}
              onChange={(e) => update_('bankAccount', e.target.value)}
            />
          </Field>

          {!isEdit && (
            <Field label="Login Password (optional)" error={errors.password}>
              <Input
                type="password"
                placeholder="Leave blank for no login"
                value={form.password}
                onChange={(e) => update_('password', e.target.value)}
              />
            </Field>
          )}
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={submit} disabled={saving}>
            {saving ? 'Saving…' : 'Save'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

function Field({
  label,
  error,
  children,
}: {
  label: string
  error?: string
  children: ReactNode
}) {
  return (
    <div className="grid gap-1.5">
      <Label>{label}</Label>
      {children}
      {error && <p className="text-destructive text-sm">{error}</p>}
    </div>
  )
}
