import { useEffect, useState } from 'react'
import { toast } from 'sonner'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useCancelLoan, useCreateLoan, useEmployeeLoans } from '@/hooks/useLoans'
import { toApiError } from '@/api/client'
import { peso, PERIODS } from '@/lib/format'
import { LOAN_TYPES, type LoanStatus, type LoanType } from '@/types/loan'
import type { Employee } from '@/types/employee'

const STATUS_VARIANT: Record<LoanStatus, 'default' | 'secondary' | 'outline'> = {
  ACTIVE: 'default',
  PAID: 'secondary',
  CANCELLED: 'outline',
}

const emptyForm = {
  type: 'COMPANY' as LoanType,
  principal: '',
  perCutoffAmount: '',
  startPeriod: PERIODS[0] as string,
}

export function LoansDialog({
  open,
  onOpenChange,
  employee,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  employee: Employee | null
}) {
  const { data: loans, isLoading } = useEmployeeLoans(open ? employee?.id ?? null : null)
  const create = useCreateLoan()
  const cancel = useCancelLoan()
  const [form, setForm] = useState(emptyForm)

  useEffect(() => {
    if (open) setForm(emptyForm)
  }, [open, employee])

  function add() {
    if (!employee) return
    create.mutate(
      {
        employeeId: employee.id,
        type: form.type,
        principal: Number(form.principal || 0),
        perCutoffAmount: Number(form.perCutoffAmount || 0),
        startPeriod: form.startPeriod,
      },
      {
        onSuccess: () => {
          toast.success('Loan added')
          setForm(emptyForm)
        },
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  function onCancel(id: number) {
    cancel.mutate(id, {
      onSuccess: () => toast.success('Loan cancelled'),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Loans — {employee?.fullName}</DialogTitle>
          <DialogDescription>
            Active loans auto-deduct each cut-off and post on lock.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-5">
          {isLoading && <p className="text-muted-foreground text-sm">Loading…</p>}
          {loans && (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Type</TableHead>
                  <TableHead className="text-right">Per cut-off</TableHead>
                  <TableHead className="text-right">Balance</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Action</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {loans.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={5} className="text-muted-foreground text-center">
                      No loans yet.
                    </TableCell>
                  </TableRow>
                )}
                {loans.map((l) => (
                  <TableRow key={l.id}>
                    <TableCell>{l.typeLabel}</TableCell>
                    <TableCell className="text-right tabular-nums">
                      {peso(l.perCutoffAmount)}
                    </TableCell>
                    <TableCell className="text-right tabular-nums">{peso(l.balance)}</TableCell>
                    <TableCell>
                      <Badge variant={STATUS_VARIANT[l.status]}>{l.status}</Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      {l.status === 'ACTIVE' && (
                        <Button
                          variant="ghost"
                          size="sm"
                          disabled={cancel.isPending}
                          onClick={() => onCancel(l.id)}
                        >
                          Cancel
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}

          <div className="grid gap-3 rounded-md border p-4">
            <p className="text-sm font-medium">Add loan</p>
            <div className="flex flex-wrap items-end gap-3">
              <div className="grid gap-1.5">
                <Label>Type</Label>
                <Select
                  value={form.type}
                  onValueChange={(v) => setForm((f) => ({ ...f, type: (v as LoanType) ?? f.type }))}
                >
                  <SelectTrigger className="w-44">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {LOAN_TYPES.map((t) => (
                      <SelectItem key={t.value} value={t.value}>
                        {t.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="grid gap-1.5">
                <Label>Principal (PHP)</Label>
                <Input
                  type="number"
                  min={0}
                  className="w-32"
                  value={form.principal}
                  onChange={(e) => setForm((f) => ({ ...f, principal: e.target.value }))}
                />
              </div>
              <div className="grid gap-1.5">
                <Label>Per cut-off (PHP)</Label>
                <Input
                  type="number"
                  min={0}
                  className="w-32"
                  value={form.perCutoffAmount}
                  onChange={(e) => setForm((f) => ({ ...f, perCutoffAmount: e.target.value }))}
                />
              </div>
              <div className="grid gap-1.5">
                <Label>Start cut-off</Label>
                <Select
                  value={form.startPeriod}
                  onValueChange={(v) => setForm((f) => ({ ...f, startPeriod: v ?? f.startPeriod }))}
                >
                  <SelectTrigger className="w-32">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {PERIODS.map((p) => (
                      <SelectItem key={p} value={p}>
                        {p}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <Button onClick={add} disabled={create.isPending}>
                {create.isPending ? 'Adding…' : 'Add'}
              </Button>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
