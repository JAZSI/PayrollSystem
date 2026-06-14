import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChartColumn, faFileCsv } from '@fortawesome/free-solid-svg-icons'
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
import { Button } from '@/components/ui/button'
import { useBank, useRegister, useRemittance } from '@/hooks/useReports'
import { downloadReportCsv } from '@/api/reports'
import { toApiError } from '@/api/client'
import { peso, PERIODS } from '@/lib/format'
import type { ReportType } from '@/types/report'

const REPORTS: { value: ReportType; label: string }[] = [
  { value: 'register', label: 'Payroll Register' },
  { value: 'remittance', label: 'Statutory Remittance' },
  { value: 'bank', label: 'Bank Disbursement' },
]

export function ReportsPage() {
  const [type, setType] = useState<ReportType>('register')
  const [period, setPeriod] = useState<string>(PERIODS[0])

  function exportCsv() {
    downloadReportCsv(type, period).catch((err) => toast.error(toApiError(err).message))
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-start justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faChartColumn} className="text-muted-foreground" />
            Reports
          </CardTitle>
          <CardDescription>Aggregated from saved payslips for a cut-off.</CardDescription>
        </div>
        <div className="flex items-end gap-2">
          <Select value={type} onValueChange={(v) => setType((v as ReportType) ?? type)}>
            <SelectTrigger className="w-52">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {REPORTS.map((r) => (
                <SelectItem key={r.value} value={r.value}>
                  {r.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Select value={period} onValueChange={(v) => setPeriod(v ?? period)}>
            <SelectTrigger className="w-36">
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
          <Button onClick={exportCsv}>
            <FontAwesomeIcon icon={faFileCsv} className="mr-2" />
            Export CSV
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        {type === 'register' && <RegisterTable period={period} />}
        {type === 'remittance' && <RemittanceTable period={period} />}
        {type === 'bank' && <BankTable period={period} />}
      </CardContent>
    </Card>
  )
}

function RegisterTable({ period }: { period: string }) {
  const { data } = useRegister(period)
  if (!data) return <p className="text-muted-foreground text-sm">Loading…</p>
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Employee</TableHead>
          <TableHead className="text-right">Gross</TableHead>
          <TableHead className="text-right">Deductions</TableHead>
          <TableHead className="text-right">Net</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.rows.length === 0 && (
          <TableRow>
            <TableCell colSpan={4} className="text-muted-foreground text-center">
              No payslips for {period}.
            </TableCell>
          </TableRow>
        )}
        {data.rows.map((r) => (
          <TableRow key={r.employeeId}>
            <TableCell className="font-medium">{r.employeeName}</TableCell>
            <TableCell className="text-right tabular-nums">{peso(r.grossPay)}</TableCell>
            <TableCell className="text-right tabular-nums">{peso(r.totalDeductions)}</TableCell>
            <TableCell className="text-right font-semibold tabular-nums">{peso(r.netPay)}</TableCell>
          </TableRow>
        ))}
        {data.rows.length > 0 && (
          <TableRow className="border-t-2 font-semibold">
            <TableCell>Total</TableCell>
            <TableCell className="text-right tabular-nums">{peso(data.totalGross)}</TableCell>
            <TableCell className="text-right tabular-nums">{peso(data.totalDeductions)}</TableCell>
            <TableCell className="text-right tabular-nums">{peso(data.totalNet)}</TableCell>
          </TableRow>
        )}
      </TableBody>
    </Table>
  )
}

function RemittanceTable({ period }: { period: string }) {
  const { data } = useRemittance(period)
  if (!data) return <p className="text-muted-foreground text-sm">Loading…</p>
  const rows = [
    { agency: 'SSS', ee: data.sssEmployee, er: data.sssEmployer },
    { agency: 'SSS EC', ee: 0, er: data.sssEc },
    { agency: 'PhilHealth', ee: data.philhealthEmployee, er: data.philhealthEmployer },
    { agency: 'Pag-IBIG', ee: data.pagibigEmployee, er: data.pagibigEmployer },
    { agency: 'BIR (Withholding Tax)', ee: data.tax, er: 0 },
  ]
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Agency</TableHead>
          <TableHead className="text-right">Employee Share</TableHead>
          <TableHead className="text-right">Employer Share</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {rows.map((r) => (
          <TableRow key={r.agency}>
            <TableCell className="font-medium">{r.agency}</TableCell>
            <TableCell className="text-right tabular-nums">{peso(r.ee)}</TableCell>
            <TableCell className="text-right tabular-nums">{peso(r.er)}</TableCell>
          </TableRow>
        ))}
        <TableRow className="border-t-2 font-semibold">
          <TableCell>Grand Total</TableCell>
          <TableCell className="text-right tabular-nums" colSpan={2}>
            {peso(data.grandTotal)}
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  )
}

function BankTable({ period }: { period: string }) {
  const { data } = useBank(period)
  if (!data) return <p className="text-muted-foreground text-sm">Loading…</p>
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Employee</TableHead>
          <TableHead>Bank Account</TableHead>
          <TableHead className="text-right">Net Pay</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.rows.length === 0 && (
          <TableRow>
            <TableCell colSpan={3} className="text-muted-foreground text-center">
              No payslips for {period}.
            </TableCell>
          </TableRow>
        )}
        {data.rows.map((r) => (
          <TableRow key={r.employeeId}>
            <TableCell className="font-medium">{r.employeeName}</TableCell>
            <TableCell className="font-mono text-sm">
              {r.bankAccount || <span className="text-muted-foreground">—</span>}
            </TableCell>
            <TableCell className="text-right font-semibold tabular-nums">{peso(r.netPay)}</TableCell>
          </TableRow>
        ))}
        {data.rows.length > 0 && (
          <TableRow className="border-t-2 font-semibold">
            <TableCell colSpan={2}>Total</TableCell>
            <TableCell className="text-right tabular-nums">{peso(data.totalNet)}</TableCell>
          </TableRow>
        )}
      </TableBody>
    </Table>
  )
}
