import { useEffect, useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faScaleBalanced, faPlus, faTrash, faFloppyDisk } from '@fortawesome/free-solid-svg-icons'
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
import {
  useCreateStatutoryTable,
  useDeleteStatutoryTable,
  useStatutoryTables,
  useUpdateStatutoryTable,
} from '@/hooks/useStatutory'
import { toApiError } from '@/api/client'
import {
  AGENCIES,
  AGENCY_HINT,
  type ContributionAgency,
  type ContributionBracket,
  type ContributionTable,
} from '@/types/statutory'

export function StatutoryTablesPage() {
  const { data: tables } = useStatutoryTables()
  const create = useCreateStatutoryTable()
  const [agency, setAgency] = useState<ContributionAgency>('SSS')
  const [effectiveFrom, setEffectiveFrom] = useState('')

  function addVersion() {
    if (!effectiveFrom) {
      toast.error('Pick an effectivity date')
      return
    }
    create.mutate(
      { agency, effectiveFrom, active: true, note: 'New version', brackets: [] },
      {
        onSuccess: () => toast.success(`${agency} version added — edit its brackets below`),
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader className="flex flex-row items-start justify-between gap-4">
          <div>
            <CardTitle className="flex items-center gap-2">
              <FontAwesomeIcon icon={faScaleBalanced} className="text-muted-foreground" />
              Statutory Tables
            </CardTitle>
            <CardDescription>
              Effective-dated SSS / PhilHealth / Pag-IBIG / BIR rates used by payroll.
            </CardDescription>
          </div>
          <div className="flex items-end gap-2">
            <Select value={agency} onValueChange={(v) => setAgency((v as ContributionAgency) ?? agency)}>
              <SelectTrigger className="w-36">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {AGENCIES.map((a) => (
                  <SelectItem key={a} value={a}>
                    {a}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Input
              type="date"
              className="w-40"
              value={effectiveFrom}
              onChange={(e) => setEffectiveFrom(e.target.value)}
            />
            <Button onClick={addVersion} disabled={create.isPending}>
              <FontAwesomeIcon icon={faPlus} className="mr-2" />
              Add version
            </Button>
          </div>
        </CardHeader>
      </Card>

      {tables?.map((t) => <TableEditor key={t.id} table={t} />)}
    </div>
  )
}

function TableEditor({ table }: { table: ContributionTable }) {
  const update = useUpdateStatutoryTable()
  const remove = useDeleteStatutoryTable()
  const [rows, setRows] = useState<ContributionBracket[]>(table.brackets)
  const [active, setActive] = useState(table.active)

  useEffect(() => {
    setRows(table.brackets)
    setActive(table.active)
  }, [table])

  function setCell(i: number, key: keyof ContributionBracket, value: string) {
    setRows((rs) => rs.map((r, idx) => (idx === i ? { ...r, [key]: Number(value) } : r)))
  }

  function addRow() {
    setRows((rs) => [...rs, { seq: rs.length, lowerBound: 0, upperBound: 0, amount: 0, rate: 0 }])
  }

  function removeRow(i: number) {
    setRows((rs) => rs.filter((_, idx) => idx !== i))
  }

  function save() {
    update.mutate(
      {
        id: table.id,
        body: {
          agency: table.agency,
          effectiveFrom: table.effectiveFrom,
          active,
          note: table.note,
          brackets: rows.map((r, seq) => ({ ...r, seq })),
        },
      },
      {
        onSuccess: () => toast.success(`${table.agency} table saved`),
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  function onDelete() {
    remove.mutate(table.id, {
      onSuccess: () => toast.success('Version deleted'),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-start justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2 text-base">
            {table.agency}
            <Badge variant={active ? 'default' : 'secondary'}>
              {active ? 'Active' : 'Inactive'}
            </Badge>
            <span className="text-muted-foreground text-sm font-normal">
              effective {table.effectiveFrom}
            </span>
          </CardTitle>
          <CardDescription>{AGENCY_HINT[table.agency]}</CardDescription>
        </div>
        <div className="flex items-center gap-2">
          <label className="flex items-center gap-1.5 text-sm">
            <input type="checkbox" checked={active} onChange={(e) => setActive(e.target.checked)} />
            Active
          </label>
          <Button variant="outline" size="sm" onClick={save} disabled={update.isPending}>
            <FontAwesomeIcon icon={faFloppyDisk} className="mr-2" />
            Save
          </Button>
          <Button variant="ghost" size="sm" onClick={onDelete} disabled={remove.isPending}>
            <FontAwesomeIcon icon={faTrash} />
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="text-right">Lower</TableHead>
              <TableHead className="text-right">Upper</TableHead>
              <TableHead className="text-right">Amount</TableHead>
              <TableHead className="text-right">Rate</TableHead>
              <TableHead></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((r, i) => (
              <TableRow key={i}>
                <TableCell>
                  <NumCell value={r.lowerBound} onChange={(v) => setCell(i, 'lowerBound', v)} />
                </TableCell>
                <TableCell>
                  <NumCell value={r.upperBound} onChange={(v) => setCell(i, 'upperBound', v)} />
                </TableCell>
                <TableCell>
                  <NumCell value={r.amount} onChange={(v) => setCell(i, 'amount', v)} />
                </TableCell>
                <TableCell>
                  <NumCell value={r.rate} step="0.001" onChange={(v) => setCell(i, 'rate', v)} />
                </TableCell>
                <TableCell className="text-right">
                  <Button variant="ghost" size="sm" onClick={() => removeRow(i)}>
                    <FontAwesomeIcon icon={faTrash} />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <Button variant="outline" size="sm" className="mt-3" onClick={addRow}>
          <FontAwesomeIcon icon={faPlus} className="mr-2" />
          Add bracket
        </Button>
      </CardContent>
    </Card>
  )
}

function NumCell({
  value,
  step,
  onChange,
}: {
  value: number
  step?: string
  onChange: (v: string) => void
}) {
  return (
    <Input
      type="number"
      step={step}
      className="w-28 text-right tabular-nums"
      value={value}
      onChange={(e) => onChange(e.target.value)}
    />
  )
}
