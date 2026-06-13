import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCalendarDays, faPaperPlane } from '@fortawesome/free-solid-svg-icons'
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
  useFileLeave,
  useLeaveTypes,
  useMyLeaveBalances,
  useMyLeaveRequests,
} from '@/hooks/useLeave'
import { toApiError } from '@/api/client'
import type { LeaveStatus } from '@/types/leave'

const STATUS_VARIANT: Record<LeaveStatus, 'default' | 'secondary' | 'outline'> = {
  PENDING: 'default',
  APPROVED: 'secondary',
  REJECTED: 'outline',
}

export function MyLeavePage() {
  const { data: balances } = useMyLeaveBalances()
  const { data: requests } = useMyLeaveRequests()
  const { data: types } = useLeaveTypes()
  const file = useFileLeave(true)
  const [form, setForm] = useState({ leaveTypeId: '', startDate: '', endDate: '', reason: '' })

  function submit() {
    if (!form.leaveTypeId || !form.startDate || !form.endDate) {
      toast.error('Pick a type and date range')
      return
    }
    file.mutate(
      {
        leaveTypeId: Number(form.leaveTypeId),
        startDate: form.startDate,
        endDate: form.endDate,
        reason: form.reason || undefined,
      },
      {
        onSuccess: () => {
          toast.success('Leave request filed')
          setForm({ leaveTypeId: '', startDate: '', endDate: '', reason: '' })
        },
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">My Leave</h1>
        <p className="text-muted-foreground text-sm">File leave and track your balances.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faCalendarDays} className="text-muted-foreground" />
            Balances
          </CardTitle>
          <CardDescription>Credits for the current year.</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Type</TableHead>
                <TableHead className="text-right">Credits</TableHead>
                <TableHead className="text-right">Used</TableHead>
                <TableHead className="text-right">Remaining</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {balances && balances.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} className="text-muted-foreground text-center">
                    No leave balances yet.
                  </TableCell>
                </TableRow>
              )}
              {balances?.map((b) => (
                <TableRow key={b.leaveTypeId}>
                  <TableCell>
                    {b.leaveTypeName}{' '}
                    {!b.paid && <span className="text-muted-foreground text-xs">(unpaid)</span>}
                  </TableCell>
                  <TableCell className="text-right tabular-nums">{b.credits}</TableCell>
                  <TableCell className="text-right tabular-nums">{b.used}</TableCell>
                  <TableCell className="text-right font-medium tabular-nums">{b.remaining}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>File a request</CardTitle>
          <CardDescription>Weekends and holidays are excluded from the day count.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex flex-wrap items-end gap-3">
            <div className="grid gap-1.5">
              <Label>Type</Label>
              <Select
                value={form.leaveTypeId}
                onValueChange={(v) => setForm((f) => ({ ...f, leaveTypeId: v ?? '' }))}
              >
                <SelectTrigger className="w-48">
                  <SelectValue placeholder="Select type" />
                </SelectTrigger>
                <SelectContent>
                  {types?.map((t) => (
                    <SelectItem key={t.id} value={String(t.id)}>
                      {t.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-1.5">
              <Label>Start</Label>
              <Input
                type="date"
                className="w-40"
                value={form.startDate}
                onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
              />
            </div>
            <div className="grid gap-1.5">
              <Label>End</Label>
              <Input
                type="date"
                className="w-40"
                value={form.endDate}
                onChange={(e) => setForm((f) => ({ ...f, endDate: e.target.value }))}
              />
            </div>
            <div className="grid flex-1 gap-1.5">
              <Label>Reason (optional)</Label>
              <Input
                value={form.reason}
                onChange={(e) => setForm((f) => ({ ...f, reason: e.target.value }))}
              />
            </div>
            <Button onClick={submit} disabled={file.isPending}>
              <FontAwesomeIcon icon={faPaperPlane} className="mr-2" />
              File
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>My requests</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Type</TableHead>
                <TableHead>Dates</TableHead>
                <TableHead className="text-right">Days</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {requests && requests.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} className="text-muted-foreground text-center">
                    No requests yet.
                  </TableCell>
                </TableRow>
              )}
              {requests?.map((r) => (
                <TableRow key={r.id}>
                  <TableCell>{r.leaveTypeName}</TableCell>
                  <TableCell className="text-sm">
                    {r.startDate} → {r.endDate}
                  </TableCell>
                  <TableCell className="text-right tabular-nums">{r.days}</TableCell>
                  <TableCell>
                    <Badge variant={STATUS_VARIANT[r.status]}>{r.status}</Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
