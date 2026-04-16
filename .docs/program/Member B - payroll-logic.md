# Payroll Logic

This part handles all payroll calculations. It reads the employee type, attendance records, and salary basis, then computes hours worked, pay, deductions, penalties, and final net pay.

## 1. PayrollCalculator

This is the main calculation class. It contains only static methods, so it behaves like a payroll utility.

### What it does
- Computes worked hours
- Computes overtime and undertime
- Computes gross pay
- Computes deductions like SSS, PhilHealth, Pag-IBIG, and tax
- Computes penalties for undertime and absences
- Combines everything into a final payroll entry

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| computeHoursWorked(record) | Computes how many hours were worked in one day, minus lunch break | TimeRecord record | double |
| computeTotalHours(records) | Adds all worked hours in the cut-off | TimeRecord[] records | double |
| computeOvertimeHours(records) | Adds all overtime hours across days | TimeRecord[] records | double |
| computeUndertimeHours(records) | Adds all undertime hours across days | TimeRecord[] records | double |
| computeAbsentDays(records) | Counts how many days were marked absent | TimeRecord[] records | int |
| computeGrossPay(employee, records) | Computes total earnings before deductions | Employee employee, TimeRecord[] records | double |
| computeBasicPay(employee, records) | Computes basic salary without overtime | Employee employee, TimeRecord[] records | double |
| computeOvertimePay(employee, records) | Computes overtime earnings | Employee employee, TimeRecord[] records | double |
| computeSSSDeduction(salary) | Computes SSS deduction based on monthly salary | double salary | double |
| computePhilHealthDeduction(monthlyRate) | Computes PhilHealth deduction based on salary | double monthlyRate | double |
| computePagibigDeduction(monthlyRate) | Computes Pag-IBIG deduction based on salary | double monthlyRate | double |
| computeWithholdingTax(taxableIncome) | Computes withholding tax from taxable income | double taxableIncome | double |
| computeUndertimePenalty(undertimeHours, hourlyRate) | Converts undertime hours into money penalty | double undertimeHours, double hourlyRate | double |
| computeAbsencePenalty(employee, absentDays) | Computes penalty for absences, depending on employee type and leave credits | Employee employee, int absentDays | double |
| computeNetPay(entry) | Computes final take-home pay from the payroll entry | PayrollEntry entry | double |
| buildPayrollEntry(employee, records, cutOffPeriod, loanAmount) | Builds the complete payroll result in one step | Employee employee, TimeRecord[] records, String cutOffPeriod, double loanAmount | PayrollEntry |

### Payroll flow
1. Count attendance data.
2. Compute hours worked, overtime, undertime, and absences.
3. Compute basic pay and overtime pay.
4. Compute deductions.
5. Compute undertime and absence penalties.
6. Subtract everything from gross pay to get net pay.

## 2. How employee type affects payroll

- Regular and Probationary employees
  - Monthly-paid
  - Have leave benefits
  - Basic pay is half of the monthly rate per cut-off
- Contractual employees
  - Monthly-paid
  - No leave benefits
  - Basic pay is also half of the monthly rate per cut-off
- Part-timer employees
  - Hourly-paid
  - No leave benefits
  - Basic pay depends on total hours worked

## 3. Holiday and overtime rules used

- Regular day overtime uses the standard 25 percent increase
- Regular holiday overtime uses a higher multiplier
- Special holiday or rest day overtime also uses a higher multiplier
- These rules are applied per time record based on the holiday type