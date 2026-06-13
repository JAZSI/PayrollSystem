import { useEffect, useMemo, useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCalendarCheck } from '@fortawesome/free-solid-svg-icons'
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
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useEmployees } from '@/hooks/useEmployees'
import {
  useAttendance,
  useAttendanceCalendar,
  useIsPeriodLocked,
  useSaveAttendance,
} from '@/hooks/usePayroll'
import { LockedBanner } from '@/components/LockedBanner'
import { toApiError } from '@/api/client'
import { PERIODS } from '@/lib/format'
import type { HolidayType, TimeRecord } from '@/types/payroll'

const now = new Date()
const YEAR = now.getFullYear()
const MONTH = now.getMonth() + 1

interface Row extends TimeRecord {
  holidayName: string | null
}

export function AttendancePage() {
  const { data: employees } = useEmployees()
  const [employeeId, setEmployeeId] = useState('')
  const [period, setPeriod] = useState<string>(PERIODS[0])
  const [rows, setRows] = useState<Row[]>([])

  const calendar = useAttendanceCalendar(period, YEAR, MONTH)
  const attendance = useAttendance(employeeId, period)
  const save = useSaveAttendance()
  const locked = useIsPeriodLocked(period)

  const existingByDay = useMemo(() => {
    const map = new Map<number, TimeRecord>()
    attendance.data?.forEach((r) => map.set(r.dayNumber, r))
    return map
  }, [attendance.data])

  useEffect(() => {
    if (!calendar.data) return
    setRows(
      calendar.data.map((c) => {
        const existing = existingByDay.get(c.dayNumber)
        return {
          dayNumber: c.dayNumber,
          timeIn: existing?.timeIn ?? 800,
          timeOut: existing?.timeOut ?? 1700,
          absent: existing?.absent ?? false,
          holidayType: c.holidayType,
          holidayName: c.holidayName,
        }
      }),
    )
  }, [calendar.data, existingByDay])

  function patch(i: number, change: Partial<Row>) {
    setRows((rs) => rs.map((r, idx) => (idx === i ? { ...r, ...change } : r)))
  }

  function onSave() {
    if (!employeeId) {
      toast.error('Select an employee first')
      return
    }
    const records: TimeRecord[] = rows.map((r) => ({
      dayNumber: r.dayNumber,
      timeIn: r.timeIn,
      timeOut: r.timeOut,
      absent: r.absent,
      holidayType: r.holidayType,
    }))
    save.mutate(
      { employeeId, cutoffPeriod: period, year: YEAR, month: MONTH, records },
      {
        onSuccess: () => toast.success('Attendance saved'),
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  const monthLabel = now.toLocaleString('en-PH', { month: 'long', year: 'numeric' })

  function holidayBadge(type: HolidayType, name: string | null) {
    if (type === 'NONE') return <span className="text-muted-foreground">—</span>
    return (
      <Badge variant={type === 'REGULAR_HOLIDAY' ? 'default' : 'secondary'}>
        {name ?? (type === 'REGULAR_HOLIDAY' ? 'Regular Holiday' : 'Special / Rest Day')}
      </Badge>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <FontAwesomeIcon icon={faCalendarCheck} className="text-muted-foreground" />
          Attendance
        </CardTitle>
        <CardDescription>
          Enter time records for {monthLabel}. Weekends are excluded and holidays are
          detected automatically.
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {locked && <LockedBanner period={period} />}
        <div className="flex flex-wrap gap-4">
          <div className="grid gap-1.5">
            <Label>Employee</Label>
            <Select value={employeeId} onValueChange={(v) => setEmployeeId(v ?? '')}>
              <SelectTrigger className="w-64">
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
          </div>
          <div className="grid gap-1.5">
            <Label>Cut-off period</Label>
            <Select value={period} onValueChange={(v) => setPeriod(v ?? period)}>
              <SelectTrigger className="w-40">
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
        </div>

        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Day</TableHead>
              <TableHead>Day Type</TableHead>
              <TableHead>Absent</TableHead>
              <TableHead>Time In (HHMM)</TableHead>
              <TableHead>Time Out (HHMM)</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} className="text-muted-foreground text-center">
                  Select an employee and period to load working days.
                </TableCell>
              </TableRow>
            )}
            {rows.map((r, i) => (
              <TableRow key={r.dayNumber}>
                <TableCell className="font-medium">{r.dayNumber}</TableCell>
                <TableCell>{holidayBadge(r.holidayType, r.holidayName)}</TableCell>
                <TableCell>
                  <input
                    type="checkbox"
                    className="size-4 accent-current"
                    checked={r.absent}
                    onChange={(e) => patch(i, { absent: e.target.checked })}
                  />
                </TableCell>
                <TableCell>
                  <Input
                    type="number"
                    className="w-24"
                    disabled={r.absent}
                    value={r.absent ? 0 : r.timeIn}
                    onChange={(e) => patch(i, { timeIn: Number(e.target.value) })}
                  />
                </TableCell>
                <TableCell>
                  <Input
                    type="number"
                    className="w-24"
                    disabled={r.absent}
                    value={r.absent ? 0 : r.timeOut}
                    onChange={(e) => patch(i, { timeOut: Number(e.target.value) })}
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <Button onClick={onSave} disabled={save.isPending || rows.length === 0 || locked}>
          {save.isPending ? 'Saving…' : 'Save attendance'}
        </Button>
      </CardContent>
    </Card>
  )
}
