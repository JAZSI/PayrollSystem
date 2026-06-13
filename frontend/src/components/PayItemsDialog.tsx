import { useEffect, useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTrash } from '@fortawesome/free-solid-svg-icons'
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
import {
  useCreatePayItem,
  useDeletePayItem,
  useEmployeePayItems,
} from '@/hooks/usePayItems'
import { toApiError } from '@/api/client'
import { peso } from '@/lib/format'
import { PAY_ITEM_KINDS, type PayItemKind } from '@/types/payItem'
import type { Employee } from '@/types/employee'

const emptyForm = {
  kind: 'ALLOWANCE' as PayItemKind,
  name: '',
  amount: '',
  taxable: 'true',
  recurring: 'true',
}

export function PayItemsDialog({
  open,
  onOpenChange,
  employee,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  employee: Employee | null
}) {
  const { data: payItems } = useEmployeePayItems(open ? employee?.id ?? null : null)
  const create = useCreatePayItem()
  const remove = useDeletePayItem(employee?.id ?? '')
  const [form, setForm] = useState(emptyForm)

  useEffect(() => {
    if (open) setForm(emptyForm)
  }, [open, employee])

  const isAllowance = form.kind === 'ALLOWANCE'

  function add() {
    if (!employee) return
    create.mutate(
      {
        employeeId: employee.id,
        kind: form.kind,
        name: form.name.trim(),
        amount: Number(form.amount || 0),
        taxable: isAllowance ? form.taxable === 'true' : false,
        recurring: form.recurring === 'true',
      },
      {
        onSuccess: () => {
          toast.success('Pay item added')
          setForm(emptyForm)
        },
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  function onDelete(id: number) {
    remove.mutate(id, {
      onSuccess: () => toast.success('Pay item removed'),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Pay Items — {employee?.fullName}</DialogTitle>
          <DialogDescription>
            Allowances add to gross (taxable ones are taxed); deductions reduce net.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-5">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Kind</TableHead>
                <TableHead className="text-right">Amount</TableHead>
                <TableHead>Taxable</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {payItems && payItems.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} className="text-muted-foreground text-center">
                    No pay items yet.
                  </TableCell>
                </TableRow>
              )}
              {payItems?.map((i) => (
                <TableRow key={i.id}>
                  <TableCell className="font-medium">{i.name}</TableCell>
                  <TableCell>
                    <Badge variant={i.kind === 'ALLOWANCE' ? 'default' : 'secondary'}>
                      {i.kind}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right tabular-nums">{peso(i.amount)}</TableCell>
                  <TableCell>
                    {i.kind === 'ALLOWANCE' ? (i.taxable ? 'Taxable' : 'Non-taxable') : '—'}
                  </TableCell>
                  <TableCell className="text-right">
                    <Button
                      variant="ghost"
                      size="sm"
                      disabled={remove.isPending}
                      onClick={() => onDelete(i.id)}
                    >
                      <FontAwesomeIcon icon={faTrash} />
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

          <div className="grid gap-3 rounded-md border p-4">
            <p className="text-sm font-medium">Add pay item</p>
            <div className="flex flex-wrap items-end gap-3">
              <div className="grid gap-1.5">
                <Label>Kind</Label>
                <Select
                  value={form.kind}
                  onValueChange={(v) => setForm((f) => ({ ...f, kind: (v as PayItemKind) ?? f.kind }))}
                >
                  <SelectTrigger className="w-36">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {PAY_ITEM_KINDS.map((k) => (
                      <SelectItem key={k.value} value={k.value}>
                        {k.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="grid gap-1.5">
                <Label>Name</Label>
                <Input
                  className="w-40"
                  placeholder="Transport"
                  value={form.name}
                  onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                />
              </div>
              <div className="grid gap-1.5">
                <Label>Amount (PHP)</Label>
                <Input
                  type="number"
                  min={0}
                  className="w-32"
                  value={form.amount}
                  onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
                />
              </div>
              {isAllowance && (
                <div className="grid gap-1.5">
                  <Label>Taxable</Label>
                  <Select
                    value={form.taxable}
                    onValueChange={(v) => setForm((f) => ({ ...f, taxable: v ?? f.taxable }))}
                  >
                    <SelectTrigger className="w-32">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="true">Taxable</SelectItem>
                      <SelectItem value="false">Non-taxable</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              )}
              <Button onClick={add} disabled={create.isPending || !form.name.trim()}>
                Add
              </Button>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}
