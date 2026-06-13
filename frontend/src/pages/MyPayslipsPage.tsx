import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faReceipt, faFilePdf } from '@fortawesome/free-solid-svg-icons'
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
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { PayslipView } from '@/components/PayslipView'
import { useMyPayslips } from '@/hooks/usePayroll'
import { downloadPayslipPdf } from '@/api/payroll'
import { toApiError } from '@/api/client'
import { peso } from '@/lib/format'
import type { Payslip } from '@/types/payroll'

export function MyPayslipsPage() {
  const { data: payslips, isLoading } = useMyPayslips()
  const [selected, setSelected] = useState<Payslip | null>(null)

  async function downloadPdf(id: number) {
    try {
      await downloadPayslipPdf(id)
    } catch (err) {
      toast.error(toApiError(err).message)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <FontAwesomeIcon icon={faReceipt} className="text-muted-foreground" />
          My Payslips
        </CardTitle>
        <CardDescription>Your payroll history. Click a row to view the breakdown.</CardDescription>
      </CardHeader>
      <CardContent>
        {isLoading && <p className="text-muted-foreground text-sm">Loading…</p>}
        {payslips && (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>#</TableHead>
                <TableHead>Cut-off</TableHead>
                <TableHead className="text-right">Gross</TableHead>
                <TableHead className="text-right">Net</TableHead>
                <TableHead>Date</TableHead>
                <TableHead></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {payslips.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-muted-foreground text-center">
                    No payslips yet.
                  </TableCell>
                </TableRow>
              )}
              {payslips.map((s) => (
                <TableRow key={s.id}>
                  <TableCell className="font-mono">{s.id}</TableCell>
                  <TableCell>{s.cutoffPeriod}</TableCell>
                  <TableCell className="text-right tabular-nums">{peso(s.grossPay)}</TableCell>
                  <TableCell className="text-right font-semibold tabular-nums">
                    {peso(s.netPay)}
                  </TableCell>
                  <TableCell className="text-muted-foreground">
                    {s.createdAt?.slice(0, 16).replace('T', ' ')}
                  </TableCell>
                  <TableCell className="space-x-1 text-right">
                    <Button variant="ghost" size="sm" onClick={() => setSelected(s)}>
                      View
                    </Button>
                    <Button variant="ghost" size="sm" onClick={() => downloadPdf(s.id)}>
                      <FontAwesomeIcon icon={faFilePdf} className="mr-1" />
                      PDF
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>

      <Dialog open={selected !== null} onOpenChange={(o) => !o && setSelected(null)}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>Payslip #{selected?.id}</DialogTitle>
          </DialogHeader>
          {selected && <PayslipView slip={selected} />}
        </DialogContent>
      </Dialog>
    </Card>
  )
}
