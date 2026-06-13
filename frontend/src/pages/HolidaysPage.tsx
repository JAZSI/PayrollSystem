import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCalendarDay, faPlus, faPen, faTrash } from '@fortawesome/free-solid-svg-icons'
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
import {
  useHolidays,
  useCreateHoliday,
  useUpdateHoliday,
  useDeleteHoliday,
} from '@/hooks/useHolidays'
import { toApiError } from '@/api/client'
import type { Holiday, HolidayType } from '@/types/payroll'

const TYPE_OPTIONS: { value: HolidayType; label: string }[] = [
  { value: 'REGULAR_HOLIDAY', label: 'Regular Holiday' },
  { value: 'SPECIAL_OR_REST_DAY', label: 'Special / Rest Day' },
]

const typeLabel = (t: HolidayType) =>
  TYPE_OPTIONS.find((o) => o.value === t)?.label ?? t

function weekday(iso: string) {
  return new Date(iso + 'T00:00:00').toLocaleDateString('en-PH', { weekday: 'short' })
}

export function HolidaysPage() {
  const { data: holidays, isLoading } = useHolidays()
  const create = useCreateHoliday()
  const update = useUpdateHoliday()
  const remove = useDeleteHoliday()

  const [open, setOpen] = useState(false)
  const [editId, setEditId] = useState<number | null>(null)
  const [date, setDate] = useState('')
  const [name, setName] = useState('')
  const [type, setType] = useState<HolidayType>('REGULAR_HOLIDAY')
  const [errors, setErrors] = useState<Record<string, string>>({})

  function openAdd() {
    setEditId(null)
    setDate('')
    setName('')
    setType('REGULAR_HOLIDAY')
    setErrors({})
    setOpen(true)
  }

  function openEdit(h: Holiday) {
    setEditId(h.id)
    setDate(h.date)
    setName(h.name)
    setType(h.type)
    setErrors({})
    setOpen(true)
  }

  function submit() {
    setErrors({})
    const body = { date, name: name.trim(), type }
    const onSuccess = () => {
      toast.success(editId ? 'Holiday updated' : 'Holiday added')
      setOpen(false)
    }
    const onError = (err: unknown) => {
      const e = toApiError(err)
      setErrors(e.fieldErrors ?? {})
      toast.error(e.message)
    }
    if (editId) update.mutate({ id: editId, body }, { onSuccess, onError })
    else create.mutate(body, { onSuccess, onError })
  }

  function onDelete(h: Holiday) {
    remove.mutate(h.id, {
      onSuccess: () => toast.success(`"${h.name}" removed`),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  const saving = create.isPending || update.isPending

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faCalendarDay} className="text-muted-foreground" />
            Holidays
          </CardTitle>
          <CardDescription>
            Manage the holiday calendar used to classify attendance automatically.
          </CardDescription>
        </div>
        <Button onClick={openAdd}>
          <FontAwesomeIcon icon={faPlus} className="mr-2" />
          Add holiday
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading && <p className="text-muted-foreground text-sm">Loading…</p>}
        {holidays && (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Name</TableHead>
                <TableHead>Type</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {holidays.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} className="text-muted-foreground text-center">
                    No holidays — add one.
                  </TableCell>
                </TableRow>
              )}
              {holidays.map((h) => (
                <TableRow key={h.id}>
                  <TableCell className="font-mono text-sm">
                    {h.date} <span className="text-muted-foreground">({weekday(h.date)})</span>
                  </TableCell>
                  <TableCell className="font-medium">{h.name}</TableCell>
                  <TableCell>
                    <Badge variant={h.type === 'REGULAR_HOLIDAY' ? 'default' : 'secondary'}>
                      {typeLabel(h.type)}
                    </Badge>
                  </TableCell>
                  <TableCell className="space-x-1 text-right">
                    <Button variant="ghost" size="sm" onClick={() => openEdit(h)}>
                      <FontAwesomeIcon icon={faPen} className="mr-1" />
                      Edit
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      disabled={remove.isPending}
                      onClick={() => onDelete(h)}
                    >
                      <FontAwesomeIcon icon={faTrash} className="mr-1" />
                      Delete
                    </Button>
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
            <DialogTitle>{editId ? 'Edit holiday' : 'Add holiday'}</DialogTitle>
            <DialogDescription>
              Holidays drive the automatic day-type on attendance and the kiosk.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-2">
            <div className="grid gap-1.5">
              <Label>Date</Label>
              <Input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
              {errors.date && <p className="text-destructive text-sm">{errors.date}</p>}
            </div>
            <div className="grid gap-1.5">
              <Label>Name</Label>
              <Input value={name} onChange={(e) => setName(e.target.value)} />
              {errors.name && <p className="text-destructive text-sm">{errors.name}</p>}
            </div>
            <div className="grid gap-1.5">
              <Label>Type</Label>
              <Select value={type} onValueChange={(v) => setType((v as HolidayType) ?? type)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {TYPE_OPTIONS.map((o) => (
                    <SelectItem key={o.value} value={o.value}>
                      {o.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpen(false)}>
              Cancel
            </Button>
            <Button onClick={submit} disabled={saving || !date || !name.trim()}>
              {saving ? 'Saving…' : 'Save'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  )
}
