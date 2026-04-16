# Data & Class Design

This part of the program holds the employee information, attendance records, and payroll results. It uses separate classes so each one has a clear job.

## 1. Employee

This is the main parent class for all employee types. It stores common employee information like ID, name, type, rate, and leave status.

### What it does
- Holds shared employee details
- Acts as the base class for all specific employee types
- Forces each employee type to define its own daily rate

### Fields
- employeeId
- name
- employeeType
- monthlyRate
- hourlyRate
- hasLeave

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| Employee(...) | Creates a new employee with the given details | employeeId, name, employeeType, monthlyRate, hourlyRate, hasLeave | none |
| getEmployeeId() | Returns the employee ID | none | String |
| getName() | Returns the employee name | none | String |
| getEmployeeType() | Returns the employee type | none | String |
| getMonthlyRate() | Returns the monthly salary rate | none | double |
| getHourlyRate() | Returns the hourly rate | none | double |
| isHasLeave() | Checks if the employee has leave benefits | none | boolean |
| computeDailyRate() | Computes the employee's daily rate | none | double |

## 2. Regular

A regular employee is a monthly-paid worker with leave benefits.

### What it does
- Uses monthly rate as its salary basis
- Has leave benefits
- Computes daily pay from monthly rate

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| Regular(...) | Creates a regular employee | employeeId, name, monthlyRate | none |
| computeDailyRate() | Returns daily rate using monthly rate divided by 26 | none | double |

## 3. Probationary

A probationary employee is also monthly-paid and has leave benefits.

### What it does
- Works like a regular employee in rate computation
- Has leave benefits
- Uses monthly rate divided by 26 for daily rate

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| Probationary(...) | Creates a probationary employee | employeeId, name, monthlyRate | none |
| computeDailyRate() | Returns daily rate using monthly rate divided by 26 | none | double |

## 4. Contractual

A contractual employee is monthly-paid but does not have leave benefits.

### What it does
- Uses monthly rate as salary basis
- Does not have leave benefits
- Uses monthly rate divided by 26 for daily rate

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| Contractual(...) | Creates a contractual employee | employeeId, name, monthlyRate | none |
| computeDailyRate() | Returns daily rate using monthly rate divided by 26 | none | double |

## 5. PartTimer

A part-timer is paid by the hour instead of by the month.

### What it does
- Stores hourly rate
- Does not have leave benefits
- Computes daily rate by multiplying hourly rate by 8

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| PartTimer(...) | Creates a part-time employee | employeeId, name, hourlyRate | none |
| computeDailyRate() | Returns an 8-hour daily rate based on hourly rate | none | double |

## 6. TimeRecord

This class stores one day of attendance information.

### What it does
- Records the day number
- Stores time in and time out
- Marks whether the employee was absent
- Stores the holiday type for that day

### Fields
- dayNumber
- timeIn
- timeOut
- isAbsent
- holidayType

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| TimeRecord(...) | Creates a time record for one day | dayNumber, timeIn, timeOut, isAbsent, holidayType | none |
| getDayNumber() | Returns the day number | none | int |
| getTimeIn() | Returns the time in value | none | int |
| getTimeOut() | Returns the time out value | none | int |
| isAbsent() | Checks if the employee was absent that day | none | boolean |
| isHoliday() | Checks if the day is any holiday type | none | boolean |
| isRegularHoliday() | Checks if the day is a regular holiday | none | boolean |
| isRestDayHoliday() | Checks if the day is a special holiday or rest day | none | boolean |
| getHolidayType() | Returns the stored holiday code | none | String |

## 7. PayrollEntry

This class stores the final payroll result for one employee and one cut-off period.

### What it does
- Keeps attendance totals
- Keeps salary and deduction values
- Stores the final net pay

### Fields
- employee
- cutOffPeriod
- totalHoursWorked
- overtimeHours
- undertimeHours
- absentDays
- basicPay
- overtimePay
- grossPay
- sssDeduction
- philhealthDeduction
- pagibigDeduction
- taxDeduction
- loanDeduction
- undertimePenalty
- absencePenalty
- netPay

### Methods

| Method | Explanation | Parameters | Returns |
|---|---|---|---|
| PayrollEntry(...) | Creates a payroll entry with default values | employee, cutOffPeriod | none |
| getEmployee() | Returns the employee in this payroll entry | none | Employee |
| getCutOffPeriod() | Returns the cut-off period label | none | String |
| getTotalHoursWorked() | Returns total hours worked | none | double |
| getOvertimeHours() | Returns total overtime hours | none | double |
| getUndertimeHours() | Returns total undertime hours | none | double |
| getAbsentDays() | Returns number of absent days | none | int |
| getBasicPay() | Returns basic salary amount | none | double |
| getOvertimePay() | Returns overtime pay amount | none | double |
| getGrossPay() | Returns gross pay | none | double |
| getSssDeduction() | Returns SSS deduction | none | double |
| getPhilhealthDeduction() | Returns PhilHealth deduction | none | double |
| getPagibigDeduction() | Returns Pag-IBIG deduction | none | double |
| getTaxDeduction() | Returns withholding tax | none | double |
| getLoanDeduction() | Returns loan deduction | none | double |
| getUndertimePenalty() | Returns undertime penalty | none | double |
| getAbsencePenalty() | Returns absence penalty | none | double |
| getNetPay() | Returns final net pay | none | double |
| setTotalHoursWorked(...) | Stores total hours worked | totalHoursWorked | none |
| setOvertimeHours(...) | Stores overtime hours | overtimeHours | none |
| setUndertimeHours(...) | Stores undertime hours | undertimeHours | none |
| setAbsentDays(...) | Stores absent day count | absentDays | none |
| setBasicPay(...) | Stores basic pay | basicPay | none |
| setOvertimePay(...) | Stores overtime pay | overtimePay | none |
| setGrossPay(...) | Stores gross pay | grossPay | none |
| setSssDeduction(...) | Stores SSS deduction | sssDeduction | none |
| setPhilhealthDeduction(...) | Stores PhilHealth deduction | philhealthDeduction | none |
| setPagibigDeduction(...) | Stores Pag-IBIG deduction | pagibigDeduction | none |
| setTaxDeduction(...) | Stores tax deduction | taxDeduction | none |
| setLoanDeduction(...) | Stores loan deduction | loanDeduction | none |
| setUndertimePenalty(...) | Stores undertime penalty | undertimePenalty | none |
| setAbsencePenalty(...) | Stores absence penalty | absencePenalty | none |
| setNetPay(...) | Stores final net pay | netPay | none |