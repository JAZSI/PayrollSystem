import type { ReactNode } from 'react'
import { peso, hours } from '@/lib/format'
import type { Payslip } from '@/types/payroll'

function Row({ label, value, strong }: { label: string; value: string; strong?: boolean }) {
  return (
    <div
      className={
        'flex justify-between py-1 ' + (strong ? 'border-t pt-2 font-semibold' : '')
      }
    >
      <span className={strong ? '' : 'text-muted-foreground'}>{label}</span>
      <span className="tabular-nums">{value}</span>
    </div>
  )
}

function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div>
      <h4 className="text-muted-foreground mb-1 text-xs font-semibold tracking-wide uppercase">
        {title}
      </h4>
      {children}
    </div>
  )
}

export function PayslipView({ slip }: { slip: Payslip }) {
  return (
    <div className="space-y-4 text-sm">
      <div className="rounded-md border p-4">
        <div className="grid grid-cols-2 gap-x-6 gap-y-1">
          <Row label="Employee ID" value={slip.employeeId} />
          <Row label="Name" value={slip.employeeName} />
          <Row label="Type" value={slip.employeeTypeLabel} />
          <Row label="Cut-off" value={slip.cutoffPeriod} />
        </div>
      </div>

      <Section title="Attendance Summary">
        <Row label="Total Hours Worked" value={hours(slip.totalHours)} />
        <Row label="Overtime Hours" value={hours(slip.overtimeHours)} />
        <Row label="Undertime Hours" value={hours(slip.undertimeHours)} />
        <Row label="Absent Days" value={`${slip.absentDays} day(s)`} />
      </Section>

      <Section title="Earnings">
        <Row label="Basic Pay" value={peso(slip.basicPay)} />
        <Row label="Overtime Pay" value={peso(slip.overtimePay)} />
        <Row label="Night Differential" value={peso(slip.nightDiffPay)} />
        <Row label="Allowances" value={peso(slip.allowances)} />
        <Row label="Gross Pay" value={peso(slip.grossPay)} strong />
      </Section>

      <Section title="Deductions">
        <Row label="SSS" value={peso(slip.sss)} />
        <Row label="PhilHealth" value={peso(slip.philhealth)} />
        <Row label="Pag-IBIG" value={peso(slip.pagibig)} />
        <Row label="Withholding Tax" value={peso(slip.tax)} />
        <Row label="Loan" value={peso(slip.loan)} />
        <Row label="Other Deductions" value={peso(slip.otherDeductions)} />
        <Row label="Undertime Penalty" value={peso(slip.undertimePenalty)} />
        <Row label="Absence Penalty" value={peso(slip.absencePenalty)} />
      </Section>

      <div className="bg-primary text-primary-foreground flex justify-between rounded-md px-4 py-3 text-base font-bold">
        <span>NET PAY</span>
        <span className="tabular-nums">{peso(slip.netPay)}</span>
      </div>

      <Section title="Employer Contributions (info only)">
        <Row label="SSS" value={peso(slip.employerSss)} />
        <Row label="PhilHealth" value={peso(slip.employerPhilhealth)} />
        <Row label="Pag-IBIG" value={peso(slip.employerPagibig)} />
        <Row label="EC" value={peso(slip.employerEc)} />
      </Section>
    </div>
  )
}
