# PayrollSystem Formulas Documentation

## Overview
This documents all mathematical formulas in the current implementation, as they appear in `PayrollCalculator` and supporting Tax classes.

---

## 1. TIME & ATTENDANCE CALCULATIONS

### 1.1 Hours Worked Per Day
**Formula:**
```
effectiveTimeIn = MAX(timeIn, workdayStartHour)   // default: 8.0
hoursWorked = outHours - effectiveTimeIn
if outHours > lunchBreakStartHour:                // default: 11.0
    hoursWorked = hoursWorked - 1.0               // subtract lunch
hoursWorked = MAX(0, hoursWorked)

// Short shifts: work < 1 hour → 0 hours credited (no pay, no undertime penalty)
if hoursWorked < 1.0:
    return 0.0
return hoursWorked
```

**Overnight shift handling:**
```
if outHours <= inHours:
    outHours = outHours + 24.0
```

**Implementation:** `PayrollCalculator.computeHoursWorked(TimeRecord record, PayrollSettings settings)`
**Source:** `src/main/java/com/com253/payrollsystem/Service/PayrollCalculator.java:43`

---

### 1.2 Total Hours Worked (Cut-off Period)
**Formula:**
```
totalHours = Σ(computeHoursWorked(record) for each record)
```
**Implementation:** `computeTotalHours(TimeRecord[] records, PayrollSettings settings)`

---

### 1.3 Overtime Hours
**Formula:**
```
otHours = MAX(0, timeOut - overtimeStartHour)    // default threshold: 17.0
```
**Note:** OT hours are hours beyond 17:00, not hours beyond 8 worked. Absent days are skipped.
**Implementation:** `computeOvertimeHours(TimeRecord[] records, PayrollSettings settings)`

---

### 1.4 Undertime Hours
**Formula:**
```
undertimeHours = Σ(MAX(0, 8.0 - hoursWorked)) for each non-absent day with hoursWorked > 0
```
**Note:** Records where `computeHoursWorked` returns 0 (short shifts) are skipped — no undertime penalty applied.
**Implementation:** `computeUndertimeHours(TimeRecord[] records, PayrollSettings settings)`

---

### 1.5 Absent Days
**Formula:**
```
absentDays = COUNT(records where record.isAbsent() == true)
```

**Short shift rule:** If `computeHoursWorked < 1.0`, the record is not considered absent. It simply contributes 0 hours — no pay, no leave consumed, no undertime penalty, no absence penalty.

**Constants:**
```
MINIMUM_PAID_HOURS = 1.0  // work less than this → 0 hours credited
```

**Implementation:** `computeAbsentDays(TimeRecord[] records, PayrollSettings settings)`

---

### 1.6 Night Shift Differential (NSD)
**Formula:**
```
nsdHours = overlap of shift with [22:00, 06:00 next day]
nsdPay = nsdHours × hourlyRate × 0.10 (10% premium)
```
**Implementation:** `computeNSD(Employee employee, TimeRecord[] records, PayrollSettings settings)`
**Details:** The night window is treated as hours `[22.0, 46.0)` (6 AM becomes 30.0 after the +24 offset for overnight shifts).

---

## 2. PAY CALCULATIONS

### 2.1 Daily Rate

| Employee Type | Formula |
|---|---|
| Regular | `monthlyRate / workingDaysPerMonth` (default: 26) |
| Probationary | `monthlyRate / workingDaysPerMonth` |
| Contractual | `monthlyRate / workingDaysPerMonth` |
| PartTimer | `hourlyRate × 8.0` |

**Implementation:** `PayrollCalculator.computeDailyRate(Employee, PayrollSettings)` (private)

---

### 2.2 Basic Pay

| Employee Type | Formula |
|---|---|
| Regular / Probationary / Contractual | `monthlyRate / 2.0` (half-monthly) |
| PartTimer | `totalHours × hourlyRate` |

**Implementation:** `computeBasicPay(Employee employee, TimeRecord[] records, PayrollSettings settings)`

---

### 2.3 Holiday Pay (premium only)

For each holiday worked:

| Holiday Type | Formula |
|---|---|
| Regular Holiday | `hoursWorked × hourlyRate × (2.00 - 1.0)` = `hours × rate × 1.0` |
| Special/Rest Day | `hoursWorked × hourlyRate × (1.30 - 1.0)` = `hours × rate × 0.3` |

**Note:** Absent days receive 0 holiday pay. The multiplier is the *premium* on top of the base rate (the base is already covered by basicPay).
**Implementation:** `computeHolidayPay(Employee, TimeRecord[], PayrollSettings)`

---

### 2.4 Overtime Pay
**Formula:**
```
for each day:
    otHours = MAX(0, timeOut - overtimeStartHour)
    multiplier = applyOtMultiplier(record)      // see below
    otPay += otHours × hourlyRate × (multiplier - 1.0)
```
**Multipliers:**
| Day Type | Extra OT Premium | Total for OT Hours |
|---|---|---|
| Regular Day | `(1.25 - 1.0)` = 0.25 | rate × 1.25 |
| Regular Holiday OT | `1.60 - 1.0` = 0.60 | rate × 1.60 (200% base paid via holidayPay) |
| Rest Day/Special OT | `1.39 - 1.0` = 0.39 | rate × 1.39 (130% base paid via holidayPay) |

**Note:** For holiday days, the base multiplier (2.0× or 1.3×) is paid via holidayPay; only the *extra* OT premium comes from this method.
**Implementation:** `computeOvertimePay(Employee, TimeRecord[], PayrollSettings)`

---

### 2.5 Gross Pay
**Formula:**
```
grossPay = basicPay + nsd + holidayPay + overtimePay
```
**For PartTimer employees**, `monthlyRate` used in deductions is derived as `grossPay × 2.0`.

**Implementation:** `buildPayrollEntry()` → `computeGrossPay()` = `basicPay + nsd + holidayPay + overtimePay`

---

## 3. DEDUCTIONS

### 3.1 SSS (Social Security System)
**Formula:**
```
monthlyContribution = SSS.monthlyContribution(salary)   // tiered bracket table
perCutoff = monthlyContribution / 2.0
```
**SSS Bracket Table:**
```
≤ 5,250        → 250
5,250–5,749   → 275
5,750–6,249   → 300
...
34,250–34,749 → 1,725
≥ 34,750       → 1,750 (max)
```
**Implementation:** `SSS.monthlyContribution(double salary)` → `PayrollCalculator.computeSSSDeduction()`
**Source:** `src/main/java/com/com253/payrollsystem/Service/Tax/SSS.java`

---

### 3.2 PhilHealth
**Formula:**
```
monthlyContribution = monthlyRate × 0.055 (5.5%)

if monthlyContribution < 500: monthlyContribution = 500
if monthlyContribution > 2,750: monthlyContribution = 2,750

perCutoff = monthlyContribution / 4.0    // employee share (half) + split (half)
```
**Implementation:** `PhilHealth.monthlyContribution(double monthlyRate)` → `PayrollCalculator.computePhilHealthDeduction()`
**Source:** `src/main/java/com/com253/payrollsystem/Service/Tax/PhilHealth.java`

---

### 3.3 Pag-IBIG
**Formula:**
```
if monthlyRate < 1,500: monthlyContribution = monthlyRate × 0.01  (1%)
else:                     monthlyContribution = monthlyRate × 0.02 (2%)

if monthlyContribution > 100: monthlyContribution = 100

perCutoff = monthlyContribution / 2.0
```
**Implementation:** `Pagibig.monthlyContribution(double monthlyRate)` → `PayrollCalculator.computePagibigDeduction()`
**Source:** `src/main/java/com/com253/payrollsystem/Service/Tax/Pagibig.java`

---

### 3.4 Withholding Tax (BIR — TRAIN Law)
**Formula:**
```
annualIncome = taxableIncome × 24.0

if annualIncome ≤ 250,000:     annualTax = 0
else if ≤ 400,000:             annualTax = (annualIncome - 250,000) × 0.15
else if ≤ 800,000:             annualTax = 22,500 + (annualIncome - 400,000) × 0.20
else if ≤ 2,000,000:           annualTax = 102,500 + (annualIncome - 800,000) × 0.25
else if ≤ 8,000,000:          annualTax = 402,500 + (annualIncome - 2,000,000) × 0.30
else:                          annualTax = 2,202,500 + (annualIncome - 8,000,000) × 0.35

perCutoff = annualTax / 24.0
```
**Taxable Income:** `grossPay - SSS - PhilHealth - Pag-IBIG`
**Implementation:** `WithholdingTax.annualTax(double annualIncome)` → `PayrollCalculator.computeWithholdingTax()`
**Source:** `src/main/java/com/com253/payrollsystem/Service/Tax/WithholdingTax.java`

---

## 4. PENALTIES

### 4.1 Undertime Penalty
**Formula:**
```
undertimePenalty = undertimeHours × hourlyRate
```
**Implementation:** `computeUndertimePenalty(double undertimeHours, double hourlyRate)`

---

### 4.2 Absence Penalty
**Formula:**
```
leaveCredits = employee.isHasLeave() ? employee.leaveBalance.getTotal() : 0
chargeableDays = MAX(0, absentDays - leaveCredits)
absencePenalty = chargeableDays × dailyRate
```
**Notes:**
- PartTimer: `leaveCredits = 0`, penalty applies from day 1.
- Regular/Probationary: uses actual leave balance (sick + vacation + emergency).
- Contractual: `hasLeave = false`, no leave credits.
- Absence penalty = 0 if `chargeableDays = 0`.

**Implementation:** `computeAbsencePenalty(Employee employee, int absentDays, PayrollSettings settings)`

---

### 4.3 Loan Deduction
Handled via the `LoanBalance` immutable value object:

```java
actual = loanBalance.deduct(requestedAmount)  // can't deduct more than balance
newBalance = loanBalance.apply(actual)
```

Records each deduction in `loan_transactions` ledger table.

---

### 4.4 Leave Deduction
Handled via the `LeaveBalance` immutable value object:

```java
LeaveBalance.DeductionResult result = leaveBalance.deduct(days);
LeaveBalance newBalance = leaveBalance.apply(result);
```

Deduction is rounded: `Math.round(sub.getLeaveDays())` — 0.5 rounds up, not truncates.

---

## 5. NET PAY

**Formula:**
```
netPay = grossPay
         - undertimePenalty
         - absencePenalty
         - sss
         - philhealth
         - pagibig
         - withholdingTax
         - loanDeduction
```

**Implementation:** `PayrollCalculator.computeNetPay(PayrollEntry entry)`
**Note:** `loanDeduction` in `PayrollEntry` is the approved amount passed at submission time (deducted in `buildPayrollEntry` before net pay, not via `computeNetPay`).

---

## 6. GROSS PAY COMPONENTS SUMMARY

```
grossPay = basicPay + overtimePay + holidayPay + nsd
             │          │              │          │
             │          │              │          └── Night shift differential (10% premium)
             │          │              └── Regular holiday / rest day premium
             │          └── OT hours × rate × (multiplier - 1)
             └── Monthly/2 OR hours × hourlyRate
```

---

## 7. EMPLOYEE TYPE DIFFERENCES

| Feature | Regular | Probationary | Contractual | PartTimer |
|---|---|---|---|---|
| Short Shift Rule (<1 hr) | 0 hrs paid | 0 hrs paid | 0 hrs paid | 0 hrs paid |
| Basic Pay | monthly/2 | monthly/2 | monthly/2 | hours × rate |
| Has Leave | yes | yes | no | no |
| OT Multiplier | 1.25× | 1.25× | 1.25× | 1.25× |
| Deduction Basis | monthlyRate | monthlyRate | monthlyRate | grossPay × 2 |
| Absence Penalty | covered by leave balance | covered by leave balance | days × rate | days × rate |
| NSD | yes | yes | yes | yes |
| Holiday Pay | yes | yes | yes | yes |

---

## 8. METHOD REFERENCE

| Method | Class | Location |
|---|---|---|
| `computeHoursWorked()` | PayrollCalculator | Line 43 |
| `computeTotalHours()` | PayrollCalculator | Line 80 |
| `computeOvertimeHours()` (total) | PayrollCalculator | Line 95 |
| `computeUndertimeHours()` | PayrollCalculator | Line 112 |
| `computeAbsentDays()` | PayrollCalculator | Line 141 |
| `computeGrossPay()` | PayrollCalculator | Line 149 |
| `computeBasicPay()` | PayrollCalculator | Line 164 |
| `computeOvertimePay()` | PayrollCalculator | Line 182 |
| `computeHolidayPay()` | PayrollCalculator | Line 272 |
| `computeNSD()` | PayrollCalculator | Line 302 |
| `buildPayrollEntry()` | PayrollCalculator | Line 451 |
| `computeSSSDeduction()` | PayrollCalculator | Line 340 |
| `computePhilHealthDeduction()` | PayrollCalculator | Line 351 |
| `computePagibigDeduction()` | PayrollCalculator | Line 366 |
| `computeWithholdingTax()` | PayrollCalculator | Line 378 |
| `computeUndertimePenalty()` | PayrollCalculator | Line 394 |
| `computeAbsencePenalty()` | PayrollCalculator | Line 406 |
| `computeNetPay()` | PayrollCalculator | Line 430 |
| `SSS.monthlyContribution()` | Tax/SSS | — |
| `PhilHealth.monthlyContribution()` | Tax/PhilHealth | — |
| `Pagibig.monthlyContribution()` | Tax/Pagibig | — |
| `WithholdingTax.annualTax()` | Tax/WithholdingTax | — |