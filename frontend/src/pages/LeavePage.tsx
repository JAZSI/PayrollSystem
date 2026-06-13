import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCalendarDays, faCheck, faXmark, faTrash, faPlus } from '@fortawesome/free-solid-svg-icons'
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
  useCreateLeaveType,
  useDecideLeaveRequest,
  useDeleteLeaveType,
  useLeaveRequests,
  useLeaveTypes,
} from '@/hooks/useLeave'
import { toApiError } from '@/api/client'
import type { LeaveStatus } from '@/types/leave'

const STATUS_VARIANT: Record<LeaveStatus, 'default' | 'secondary' | 'outline'> = {
  PENDING: 'default',
  APPROVED: 'secondary',
  REJECTED: 'outline',
}

const STATUSES: LeaveStatus[] = ['PENDING', 'APPROVED', 'REJECTED']

export function LeavePage() {
  const [filter, setFilter] = useState<LeaveStatus>('PENDING')
  const { data: requests } = useLeaveRequests(filter)
  const approve = useDecideLeaveRequest('approve')
  const reject = useDecideLeaveRequest('reject')

  function decide(kind: 'approve' | 'reject', id: number) {
    const m = kind === 'approve' ? approve : reject
    m.mutate(id, {
      onSuccess: () => toast.success(`Request ${kind === 'approve' ? 'approved' : 'rejected'}`),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between gap-4">
          <div>
            <CardTitle className="flex items-center gap-2">
              <FontAwesomeIcon icon={faCalendarDays} className="text-muted-foreground" />
              Leave Requests
            </CardTitle>
            <CardDescription>Review and decide employee leave applications.</CardDescription>
          </div>
          <Select value={filter} onValueChange={(v) => setFilter((v as LeaveStatus) ?? filter)}>
            <SelectTrigger className="w-40">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {STATUSES.map((s) => (
                <SelectItem key={s} value={s}>
                  {s}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Employee</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Dates</TableHead>
                <TableHead className="text-right">Days</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {requests && requests.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-muted-foreground text-center">
                    No {filter.toLowerCase()} requests.
                  </TableCell>
                </TableRow>
              )}
              {requests?.map((r) => (
                <TableRow key={r.id}>
                  <TableCell className="font-mono text-sm">{r.employeeId}</TableCell>
                  <TableCell>{r.leaveTypeName}</TableCell>
                  <TableCell className="text-sm">
                    {r.startDate} → {r.endDate}
                  </TableCell>
                  <TableCell className="text-right tabular-nums">{r.days}</TableCell>
                  <TableCell>
                    <Badge variant={STATUS_VARIANT[r.status]}>{r.status}</Badge>
                  </TableCell>
                  <TableCell className="space-x-1 text-right">
                    {r.status === 'PENDING' && (
                      <>
                        <Button
                          variant="ghost"
                          size="sm"
                          disabled={approve.isPending}
                          onClick={() => decide('approve', r.id)}
                        >
                          <FontAwesomeIcon icon={faCheck} className="mr-1" />
                          Approve
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          disabled={reject.isPending}
                          onClick={() => decide('reject', r.id)}
                        >
                          <FontAwesomeIcon icon={faXmark} className="mr-1" />
                          Reject
                        </Button>
                      </>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <LeaveTypesCard />
    </div>
  )
}

function LeaveTypesCard() {
  const { data: types } = useLeaveTypes()
  const create = useCreateLeaveType()
  const remove = useDeleteLeaveType()
  const [form, setForm] = useState({ name: '', paid: 'true', credits: '0' })

  function add() {
    create.mutate(
      {
        name: form.name.trim(),
        paid: form.paid === 'true',
        defaultAnnualCredits: Number(form.credits || 0),
      },
      {
        onSuccess: () => {
          toast.success('Leave type added')
          setForm({ name: '', paid: 'true', credits: '0' })
        },
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  function onDelete(id: number) {
    remove.mutate(id, {
      onSuccess: () => toast.success('Leave type removed'),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Leave Types</CardTitle>
        <CardDescription>
          Defaults seed each employee's annual credits. Paid types waive absence penalties.
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-5">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Paid</TableHead>
              <TableHead className="text-right">Annual credits</TableHead>
              <TableHead className="text-right">Action</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {types?.map((t) => (
              <TableRow key={t.id}>
                <TableCell className="font-medium">{t.name}</TableCell>
                <TableCell>
                  <Badge variant={t.paid ? 'default' : 'secondary'}>
                    {t.paid ? 'Paid' : 'Unpaid'}
                  </Badge>
                </TableCell>
                <TableCell className="text-right tabular-nums">{t.defaultAnnualCredits}</TableCell>
                <TableCell className="text-right">
                  <Button
                    variant="ghost"
                    size="sm"
                    disabled={remove.isPending}
                    onClick={() => onDelete(t.id)}
                  >
                    <FontAwesomeIcon icon={faTrash} />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <div className="flex flex-wrap items-end gap-3 rounded-md border p-4">
          <div className="grid gap-1.5">
            <Label>Name</Label>
            <Input
              className="w-48"
              placeholder="Bereavement Leave"
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
            />
          </div>
          <div className="grid gap-1.5">
            <Label>Paid</Label>
            <Select value={form.paid} onValueChange={(v) => setForm((f) => ({ ...f, paid: v ?? f.paid }))}>
              <SelectTrigger className="w-28">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="true">Paid</SelectItem>
                <SelectItem value="false">Unpaid</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="grid gap-1.5">
            <Label>Annual credits</Label>
            <Input
              type="number"
              min={0}
              className="w-28"
              value={form.credits}
              onChange={(e) => setForm((f) => ({ ...f, credits: e.target.value }))}
            />
          </div>
          <Button onClick={add} disabled={create.isPending || !form.name.trim()}>
            <FontAwesomeIcon icon={faPlus} className="mr-2" />
            Add type
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
