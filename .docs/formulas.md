# PayrollSystem Formulas Documentation

## Overview
This document contains all mathematical formulas used in the Payroll System for calculating employee compensation, deductions, and net pay.

---

## 1. TIME & ATTENDANCE CALCULATIONS

### 1.1 Hours Worked Per Day
**Formula:**
```
effectiveTimeIn = MAX(timeIn, 0800)
hoursWorked = MAX(0, timeOut - effectiveTimeIn)
if timeOut > 1100:
      hoursWorked = hoursWorked - 1.0
where:
  - timeIn/timeOut are in HHMM format (e.g., 0800 = 8:00 AM)
  - Convert to decimal: hours = (HHMM / 100) + (HHMM % 100) / 60
   - Work hours only start counting at 8:00 AM; earlier time-in is ignored
   - 1.0 hour is subtracted for lunch only when the shift extends past 11:00 AM
  - Returns 0.0 if employee is absent
```

**Implementation:** `computeHoursWorked(TimeRecord record)`

**Example:**
```
Time In: 0800 → 8.0 + 0/60 = 8.0 hours
Time Out: 1700 → 17.0 + 0/60 = 17.0 hours
Hours Worked = (17.0 - 8.0) - 1.0 = 8.0 hours
```

---

### 1.2 Total Hours Worked (Cut-off Period)
**Formula:**
```
totalHours = Σ(hoursWorked for each day)
where: only non-absent days are counted
```

**Implementation:** `computeTotalHours(TimeRecord[] records)`

---

### 1.3 Overtime Hours
**Formula:**
```
overtimeHours = MAX(0, hoursWorked - 8.0) for each day
totalOvertimeHours = Σ(overtimeHours for each day)
```

**Implementation:** `computeOvertimeHours(TimeRecord[] records)`

**Rule:** Overtime is triggered when daily hours exceed 8 hours

---

### 1.4 Undertime Hours
**Formula:**
```
undertimeHours = MAX(0, 8.0 - hoursWorked) for each day
totalUndertimeHours = Σ(undertimeHours for each day)
```

**Implementation:** `computeUndertimeHours(TimeRecord[] records)`

**Rule:** Undertime is recorded when daily hours are less than 8 hours

---

### 1.5 Absent Days
**Formula:**
```
absentDays = COUNT(days where isAbsent = true)
```

**Implementation:** `computeAbsentDays(TimeRecord[] records)`

---

## 2. PAY CALCULATIONS

### 2.1 Daily Rate Computation (By Employee Type)

#### Regular Employee
**Formula:**
```
dailyRate = monthlyRate / 26.0
```
**Justification:** 26 working days per month (standard in Philippines)

#### Probationary Employee
**Formula:**
```
dailyRate = monthlyRate / 26.0
```
Same as Regular employee.

#### Contractual Employee
**Formula:**
```
dailyRate = monthlyRate / 26.0
```
Same as Regular employee.

#### Part-Time Employee
**Formula:**
```
dailyRate = hourlyRate × 8.0
```
**Justification:** Standard 8-hour workday

---

### 2.2 Basic Pay (Without Overtime)

#### Part-Time Employee
**Formula:**
```
basicPay = totalHours × hourlyRate
```

#### Regular, Probationary, Contractual Employees
**Formula:**
```
basicPay = monthlyRate / 2.0
```
**Justification:** Semi-monthly cutoff (two pay periods per month)

**Implementation:** `computeBasicPay(Employee employee, TimeRecord[] records)`

---

### 2.3 Overtime Pay

#### Part-Time Employee
**Formula:**
```
overtimePay = Σ[ MAX(0, hoursWorked - 8.0) × hourlyRate × (overtimeMultiplier - 1.0) ]
for each day
```

#### Regular, Probationary, Contractual Employees
**Formula:**
```
hourlyRate = dailyRate / 8.0 = (monthlyRate / 26.0) / 8.0
overtimePay = Σ[ MAX(0, hoursWorked - 8.0) × hourlyRate × overtimeMultiplier ]
for each day
```

**Overtime Multipliers:**
```
- Regular Day Overtime: 1.25×
- Regular Holiday Overtime: 2.00 × 1.30 = 2.60×
- Rest Day/Special Holiday Overtime: 1.30 × 1.30 = 1.69×
```

**Implementation:** `computeOvertimePay(Employee employee, TimeRecord[] records)`

---

### 2.4 Gross Pay
**Formula:**
```
grossPay = basicPay + overtimePay
```

**Implementation:** `computeGrossPay(Employee employee, TimeRecord[] records)`

---

## 3. DEDUCTION CALCULATIONS

### 3.1 SSS (Social Security System) Deduction

**Formula:**
```
Monthly contribution is determined by tiered table based on monthly salary:

Salary Range                 | Monthly Contribution
─────────────────────────────────────────────────
< 5,250                      | 250
5,250 - 5,749                | 275
5,750 - 6,249                | 300
6,250 - 6,749                | 325
6,750 - 7,249                | 350
...
34,250 - 34,749              | 1,725
35,000+                      | 1,750

Per Cut-off = Monthly Contribution / 2.0
```

**Implementation:** `computeSSSDeduction(double salary)`

**Note:** SSS uses a bracketed contribution table with maximum cap at 1,750

---

### 3.2 PhilHealth Deduction

**Formula:**
```
monthlyContribution = monthlyRate × 0.055  (5.5% total)

Apply Floor & Ceiling:
  IF monthlyContribution < 500.00:
     monthlyContribution = 500.00
  ELSE IF monthlyContribution > 2,750.00:
     monthlyContribution = 2,750.00

Employee Share = monthlyContribution / 2
Per Cut-off = monthlyContribution / 4.0
```

**Implementation:** `computePhilHealthDeduction(double monthlyRate)`

**Rationale:**
- Employer contributes equal share (/ 2.0)
- Employee cut-off is half of employee share (/2.0 again)
- Net calculation: monthly / 4.0

---

### 3.3 Pag-IBIG Deduction

**Formula:**
```
IF monthlyRate < 1,500.00:
   monthlyContribution = monthlyRate × 0.01  (1%)
ELSE:
   monthlyContribution = monthlyRate × 0.02  (2%)

CEILING:
  IF monthlyContribution > 100.00:
     monthlyContribution = 100.00

Per Cut-off = monthlyContribution / 2.0
```

**Implementation:** `computePagibigDeduction(double monthlyRate)`

---

### 3.4 Withholding Tax (BIR - Bureau of Internal Revenue)

**Formula:**
```
Step 1: Annualize the taxable income
   annualIncome = taxableIncome × 24.0
   (24 cut-offs per year)

Step 2: Apply progressive tax brackets
   IF annualIncome ≤ 250,000:
      annualTax = 0.00

   ELSE IF annualIncome ≤ 400,000:
      annualTax = (annualIncome - 250,000) × 0.15

   ELSE IF annualIncome ≤ 800,000:
      annualTax = 22,500 + (annualIncome - 400,000) × 0.20

   ELSE IF annualIncome ≤ 2,000,000:
      annualTax = 102,500 + (annualIncome - 800,000) × 0.25

   ELSE IF annualIncome ≤ 8,000,000:
      annualTax = 402,500 + (annualIncome - 2,000,000) × 0.30

   ELSE:
      annualTax = 2,202,500 + (annualIncome - 8,000,000) × 0.35

Step 3: Convert back to per cut-off
   taxPerCutoff = annualTax / 24.0
```

**Implementation:** `computeWithholdingTax(double taxableIncome)`

**Tax Brackets:**
```
Annual Income Range          | Tax Rate | Base Tax
─────────────────────────────────────────────────────
≤ 250,000                    | 0%       | 0
250,001 - 400,000            | 15%      | 0
400,001 - 800,000            | 20%      | 22,500
800,001 - 2,000,000          | 25%      | 102,500
2,000,001 - 8,000,000        | 30%      | 402,500
8,000,001+                   | 35%      | 2,202,500
```

---

### 3.5 Taxable Income Calculation
**Formula:**
```
taxableIncome = grossPay - SSS - PhilHealth - Pag-IBIG
```

**Implementation:** Calculated in `buildPayrollEntry()` before tax computation

---

## 4. PENALTY CALCULATIONS

### 4.1 Undertime Penalty
**Formula:**
```
undertimePenalty = undertimeHours × hourlyRate

where:
  hourlyRate = dailyRate / 8.0
  undertimeHours = sum of all undertime hours in cut-off
```

**Implementation:** `computeUndertimePenalty(double undertimeHours, double hourlyRate)`

---

### 4.2 Absence Penalty

**Formula:**

#### Part-Time Employees
```
absencePenalty = 0.0
(No penalty; they only pay for hours worked)
```

#### Regular & Probationary Employees (with leave credits)
```
IF absentDays ≤ 5:
   absencePenalty = 0.0
   (Covered by 5 days of leave credits)
ELSE:
   chargeableDays = absentDays - 5
   absencePenalty = chargeableDays × dailyRate
```

#### Contractual Employees (no leave credits)
```
absencePenalty = absentDays × dailyRate
```

**Implementation:** `computeAbsencePenalty(Employee employee, int absentDays)`

**Rules:**
- Part-timers have no leave benefits
- Regular & Probationary get 5 days of leave credits per cut-off
- Contractual employees have no leave credits

---

## 5. NET PAY CALCULATION

### 5.1 Final Net Pay
**Formula:**
```
netPay = grossPay
         - undertimePenalty
         - absencePenalty
         - SSS
         - PhilHealth
         - Pag-IBIG
         - withholding Tax
         - loan Deduction (if any)
```

**In Mathematical Notation:**
$$\text{netPay} = \text{GP} - \text{UTP} - \text{AP} - \text{SSS} - \text{PH} - \text{PI} - \text{TAX} - \text{LOAN}$$

**Implementation:** `computeNetPay(PayrollEntry entry)`

where:
- GP = Gross Pay
- UTP = Undertime Penalty
- AP = Absence Penalty
- SSS = SSS Deduction
- PH = PhilHealth Deduction
- PI = Pag-IBIG Deduction
- TAX = Withholding Tax
- LOAN = Loan Deduction

---

## 6. CONSTANTS & MULTIPLIERS

### 6.1 Time & Attendance Constants
```
- Standard workday: 8 hours
- Lunch break: 1 hour
- Working days per month: 26 days
- Pay periods per year: 24 (semi-monthly)
```

### 6.2 Overtime Multipliers
```
- Regular Day OT: 1.25× hourly rate
- Regular Holiday Base: 2.00×
- Regular Holiday w/ OT: 2.00 × 1.30 = 2.60×
- Rest Day/Special Holiday: 1.30×
- Rest Day/Special Holiday w/ OT: 1.30 × 1.30 = 1.69×
```

### 6.3 Deduction Constants
```
- PhilHealth Rate: 5.5% of monthly salary
  * Minimum monthly: 500.00
  * Maximum monthly: 2,750.00
  * Employee share: 50%

- Pag-IBIG Rate:
  * < 1,500: 1% of monthly salary
  * ≥ 1,500: 2% of monthly salary
  * Maximum monthly: 100.00

- SSS: Tiered contribution table (see section 3.1)
  * Maximum contribution: 1,750/month

- Leave Credits: 5 days per cut-off (Regular & Probationary only)
```

---

## 7. SUMMARY TABLE

| Component | Formula/Rule | Applies To |
|-----------|--------------|-----------|
| **Hours Worked** | (timeOut - timeIn) - 1 hr lunch | All employees |
| **Overtime** | Hours > 8/day | All employees |
| **Undertime** | Hours < 8/day | All employees |
| **Basic Pay** | Monthly/2 OR Hours × Rate | By employment type |
| **Overtime Pay** | OT Hours × Rate × Multiplier | All (different multipliers by day type) |
| **Gross Pay** | Basic + Overtime | All employees |
| **SSS** | Tiered table / 2 | Monthly rate basis |
| **PhilHealth** | (Monthly × 5.5%) / 4 | Monthly rate basis |
| **Pag-IBIG** | (Monthly × 1-2%) / 2 | Capped at 100/month |
| **Tax** | Progressive brackets / 24 | Taxable income in PH |
| **Undertime Penalty** | UT Hours × Hourly Rate | All employees |
| **Absence Penalty** | (Days - 5) × Daily Rate | Based on leave credits |
| **Net Pay** | Gross - Penalties - Deductions | All employees |

---

## 8. IMPLEMENTATION NOTES

### Employee Type Differences

| Feature | Regular | Probationary | Contractual | Part-Timer |
|---------|---------|--------------|-------------|-----------|
| **Basic Pay Basis** | Monthly/2 | Monthly/2 | Monthly/2 | Hours × Rate |
| **Daily Rate** | Monthly/26 | Monthly/26 | Monthly/26 | Hourly × 8 |
| **Leave Credits** | 5 days/cutoff | 5 days/cutoff | None | None |
| **OT Eligible** | Yes | Yes | Yes | Yes |
| **SSS Deduction** | Yes | Yes | Yes | Yes (from GP×2) |
| **PhilHealth** | Yes | Yes | Yes | Yes (from GP×2) |
| **Pag-IBIG** | Yes | Yes | Yes | Yes (from GP×2) |

### Special Rules
- **Part-timers:** No absence penalty, no leave credits, paid only for hours worked
- **Contractual:** No leave credits, higher absence penalties
- **Regular/Probationary:** 5 free leave days per cut-off before penalties apply

---

## 9. FORMULA REFERENCE BY METHOD

| Method Name | Purpose | Key Formula |
|-------------|---------|-------------|
| `computeHoursWorked()` | Daily hours calculation | (timeOut - timeIn) - 1 |
| `computeTotalHours()` | Sum all worked hours | Σ hoursWorked |
| `computeOvertimeHours()` | Sum OT hours | Σ MAX(0, hours - 8) |
| `computeUndertimeHours()` | Sum UT hours | Σ MAX(0, 8 - hours) |
| `computeAbsentDays()` | Count absences | COUNT(absent) |
| `computeDailyRate()` | Employee daily rate | By type (Monthly/26 or Hourly×8) |
| `computeBasicPay()` | Base compensation | By type (Monthly/2 or Hours×Rate) |
| `computeOvertimePay()` | OT compensation | OT Hours × Rate × Multiplier |
| `computeGrossPay()` | Total earnings | Basic + OT |
| `computeSSSDeduction()` | SSS deduction | Tiered table / 2 |
| `computePhilHealthDeduction()` | PhilHealth deduction | (Rate × 5.5%) / 4 |
| `computePagibigDeduction()` | Pag-IBIG deduction | (Rate × 1-2%) / 2 |
| `computeWithholdingTax()` | Income tax | Progressive brackets / 24 |
| `computeUndertimePenalty()` | UT penalty | UT Hours × Hourly Rate |
| `computeAbsencePenalty()` | Absence penalty | (Days - Credits) × Daily Rate |
| `computeNetPay()` | Final pay | Gross - Penalties - Deductions |
