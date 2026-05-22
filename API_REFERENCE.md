# Payroll System - JavaFX API Reference

## Initialization

```java
// One-time database setup (call at app startup)
Database.initialize();
```

## PayrollService (main service layer)

```java
PayrollService service = new PayrollService();
```

### Authentication

```java
EndUser user = service.authenticate(String username, String password)
// Returns EndUser or null. Check user.getRole() → EndUser.Role.ADMIN or EMPLOYEE
// For EMPLOYEE: use user.getLinkedEmployeeId() to get their employee ID
```

### Employee Management

```java
// Register new employee + create login account
service.registerEmployee(Employee emp, String username, String password)

// Get employee by ID
Employee emp = service.findEmployee(String employeeId)

// Get all employees
List<Employee> employees = service.getAllEmployees()

// Delete employee + account
service.deleteEmployee(String employeeId)
```

### Submission Workflow

```java
// Employee: file a payroll submission
// Returns false if an APPROVED submission already exists
boolean success = service.submitPayroll(String employeeId,
    double leaveDays, double otHours, double loanDeduction)
// leaveDays is rounded to nearest int when applied

// Get current submission for an employee
Submission sub = service.getSubmission(String employeeId)
// Returns null if no submission

// Admin: approve/reject
service.updateSubmissionStatus(int submissionId,
    Submission.Status status,  // Submission.Status.APPROVED or REJECTED
    String cutOffPeriod)       // "1st-15th" or "16th-30th"

// Get all pending submissions
List<Submission> pending = service.getPendingSubmissions()
```

### Build Payslip

```java
// Returns PayrollEntry with full breakdown, or null if no approved submission
PayrollEntry entry = service.buildPayrollEntry(String employeeId, String cutOffPeriod)
```

### Deduction History

```java
List<LeaveTransaction> leaveHistory = service.getLeaveHistory(String employeeId)
List<LoanTransaction> loanHistory = service.getLoanHistory(String employeeId)
```

### Attendance Management (timekeeping)

```java
// View attendance records for a date range
List<AttendanceRecord> records = service.getAttendanceHistory(String employeeId,
    LocalDate from, LocalDate to)

// Edit existing record
service.updateTimeIn(String employeeId, LocalDate date, double timeIn)      // decimal hours
service.updateTimeOut(String employeeId, LocalDate date, double timeOut)    // decimal hours

// Delete a record
service.deleteAttendance(String employeeId, LocalDate date)

// Add or overwrite (insert or update)
service.upsertAttendance(String employeeId, LocalDate date, Double timeIn, Double timeOut)
```

---

## PayrollReportService (reporting)

```java
PayrollReportService reportService = new PayrollReportService();
```

### Queries

```java
// Get all payroll entries for a period
List<PayrollReportEntry> entries = reportService.getReportByPeriod(String cutOffPeriod)
    // "1st-15th" or "16th-30th"

// Get all entries across all periods
List<PayrollReportEntry> allEntries = reportService.getAllReports()

// Compute period totals: [totalGross, totalDeductions, totalNet]
double[] totals = reportService.computePeriodTotals(List<PayrollReportEntry> entries)
```

### CSV Export

```java
// Export to CSV file
reportService.exportToCsv(List<PayrollReportEntry> entries, String filePath)
```

---

## Model Classes

### Employee

```java
// Abstract class. Use subclasses:
Regular emp    = new Regular(employeeId, name, monthlyRate, LeaveBalance, LoanBalance)
Probationary emp = new Probationary(employeeId, name, monthlyRate, LeaveBalance, LoanBalance)
Contractual emp = new Contractual(employeeId, name, monthlyRate, LeaveBalance, LoanBalance)
PartTimer emp   = new PartTimer(employeeId, name, hourlyRate, LeaveBalance, LoanBalance)

// Properties
emp.getEmployeeId()
emp.getName()
emp.getEmployeeType()        // Returns class name: "Regular", "Probationary", etc.
emp.getMonthlyRate()          // Returns 0 for PartTimer (use hourlyRate instead)
emp.getHourlyRate()           // Returns hourly rate
emp.getLeaveBalance()         // LeaveBalance object
emp.getLoanBalance()          // LoanBalance object
emp.isHasLeave()              // Boolean
```

### LeaveBalance (immutable)

```java
LeaveBalance lb = new LeaveBalance(int sick, int vacation, int emergency);
lb.getSick()
lb.getVacation()
lb.getEmergency()
lb.getTotal()                                          // sum of all
LeaveBalance.DeductionResult dr = lb.deduct(int days) // compute deduction breakdown
LeaveBalance newLb = lb.apply(DeductionResult dr)     // get new balance (immutable)
dr.getSick() + dr.getVacation() + dr.getEmergency()  // days deducted per type
```

### LoanBalance (immutable)

```java
LoanBalance lb = new LoanBalance(double balance);
lb.getBalance()
double actual = lb.deduct(double amount)            // can't deduct more than balance
LoanBalance newLb = lb.apply(double amount)         // get new balance
```

### PayrollEntry (computed payroll result)

```java
entry.getEmployee()
entry.getCutOffPeriod()

// Hours
entry.getTotalHoursWorked()
entry.getOvertimeHours()
entry.getUndertimeHours()
entry.getAbsentDays()

// Earnings
entry.getBasicPay()
entry.getOvertimePay()
entry.getHolidayPay()
entry.getNightShiftDifferential()
entry.getGrossPay()

// Deductions
entry.getSssDeduction()
entry.getPhilhealthDeduction()
entry.getPagibigDeduction()
entry.getTaxDeduction()
entry.getLoanDeduction()

// Penalties
entry.getUndertimePenalty()
entry.getAbsencePenalty()

// Final
entry.getNetPay()
```

### PayrollReportEntry (saved payroll from DB)

```java
entry.getId()
entry.getEmployeeId()
entry.getEmployeeName()
entry.getCutOffPeriod()
entry.getTotalHours()
entry.getOvertimeHours()
entry.getUndertimeHours()
entry.getAbsentDays()
entry.getBasicPay()
entry.getOvertimePay()
entry.getHolidayPay()
entry.getNightShiftDifferential()
entry.getGrossPay()
entry.getSssDeduction()
entry.getPhilhealthDeduction()
entry.getPagibigDeduction()
entry.getTaxDeduction()
entry.getLoanDeduction()
entry.getUndertimePenalty()
entry.getAbsencePenalty()
entry.getNetPay()
entry.getCreatedAt()
```

### AttendanceRecord

```java
rec.getEmployeeId()
rec.getRecordDate()           // LocalDate
rec.getTimeIn()               // Double (decimal hours)
rec.getTimeOut()              // Double (decimal hours)
```

### Submission

```java
sub.getId()
sub.getEmployeeId()
sub.getLeaveDays()
sub.getOtHours()
sub.getLoanDeduction()
sub.getStatus()               // Submission.Status.PENDING / APPROVED / REJECTED
sub.getSubmittedAt()
```

### EndUser

```java
user.getUsername()
user.getPasswordHash()
user.getRole()                // EndUser.Role.ADMIN or EMPLOYEE
user.getLinkedEmployeeId()    // null for ADMIN, employee ID for EMPLOYEE
```

### LeaveTransaction / LoanTransaction

```java
tx.getId()
tx.getEmployeeId()
// tx.getCreatedAt()
tx.getCutOffPeriod()
// LeaveTransaction:
tx.getLeaveType()             // LeaveTransaction.LeaveType.SICK / VACATION / EMERGENCY
tx.getDays()
// LoanTransaction:
tx.getAmount()
```

---

## PayrollSettings

Default configuration:
```java
PayrollSettings settings = new PayrollSettings(
    int workingDaysPerMonth,  // 26
    double workdayStartHour,  // 8.0
    double overtimeStartHour, // 17.0
    double lunchBreakHour     // 11.0
);
```

Use `PayrollCalculator.buildPayrollEntry(employee, records, period, loanDeduction, settings)` if you need custom settings.

---

## Error Handling

All `PayrollService` and `PayrollReportService` methods throw `SQLException`.

```java
try {
    service.registerEmployee(emp, username, password);
} catch (SQLException e) {
    e.printStackTrace();  // or show alert in JavaFX
}
```

---

## Time Values

All time values are **decimal hours**:
- `8.0` = 8:00 AM
- `8.5` = 8:30 AM
- `13.5` = 1:30 PM
- `17.75` = 5:45 PM
- `22.0` = 10:00 PM

When displaying to user, convert:
```java
int hours = (int) time;
int minutes = (int) ((time - hours) * 60);
String formatted = String.format("%02d:%02d", hours, minutes);
```