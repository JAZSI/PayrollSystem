# CCOBJPGL - Object Oriented Programming

## 1. Develop a Java Payroll program to compute and display the salary of an employee based on employee type.

### Type of Employee:

- Regular
- Probationary
- Contractual
- Part-timer

### Cut-off period:

- 1st-15th
- 16th-30th

(Monday-Friday work week, 8 hrs per work day, 8am to 5pm, 1 hour break)

### Input data:

- Employee Number
- Employee Name
- Time Keeping (per work week - should consider leaves for Regular employees)

### Compute for:

- Hours work (including overtime, absences and undertime)
- Gross pay
- Deductions:
  - Tax
  - SSS
  - Philhealth
  - Loans
  - Undertime

- Netpay

---

## Employee type:

a. Regular - has leave benefits, monthly rate
b. Probationary - has leave benefits, monthly rate
c. Contractual - no leave benefits, monthly rate
d. Part-time - no work, no pay, hourly rate

### Note:

Research on the following to integrate in the payroll computation:

- holiday rates for overtime
- Tax table
- SSS table
- PAG-IBIG and PHILhealth contribution

---

# SAMPLE Run:

ABC Company
Employee Payroll System

Employee ID: 1234-5678-90
Employee Name: Juan Dela Cruz

Employee Type:

- [R]egular
- [P]robationary
- [C]ontractual
- [P]art-time

Basic Salary: Monthly / Hourly Rate

Cut-off Period:

- 1st-15th of the month
- 16th-30th of the month

---

## Timekeeping:

| Day | Time In        | Time Out         |
| --- | -------------- | ---------------- |
| 1   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 2   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 3   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 4   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 5   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 6   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 7   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 8   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 9   | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 10  | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 11  | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 12  | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 13  | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 14  | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |
| 15  | \_\_\_\_\_\_\_\_ | \_\_\_\_\_\_\_\_ |

---

### Computations:

- Total Hours Worked (computed)
- Absent / Undertime (computed)
- Overtime (computed)

Loans: \_\_\_\_\_\_\_\_

---

### Salary Details:

Basic Salary

Additional:

- Overtime

Deductions:

- Undertime / Late
- Absences
- SSS
- W/Tax
- Pagibig
- Philhealth
- Loans

Netpay

---

# PROJECT Checklist:

## Program Requirements

- [ ] Program is console-based
- [ ] No file I/O used
- [ ] Menu-driven interface implemented
- [ ] Uses objects or ArrayList (not static variables only)

## OOP Structure

- [ ] At least 3 user-defined classes
- [ ] Attributes declared as private
- [ ] Constructors implemented
- [ ] Getters and setters used
- [ ] Main class controls program flow only

## Payroll Functionality

- [ ] Regular employee logic implemented correctly
- [ ] Probationary employee logic implemented correctly
- [ ] Contractual employee logic implemented correctly
- [ ] Part-time (hourly, no work-no pay) logic implemented correctly
- [ ] Correct computation of gross pay
- [ ] Correct computation of deductions
- [ ] Correct computation of net pay

---

# Output & Usability

- [ ] Output follows the sample run format
- [ ] Clear labels for payroll components
- [ ] Readable prompts and layout

---

# Code Quality

- [ ] Proper indentation and formatting
- [ ] Meaningful variable, class, and method names
- [ ] Comments explaining major logic

---

# Java Payroll System – Grading Rubric

## OOP Design & Program Structure (30 pts)

- 30 – Excellent: Proper class separation, encapsulation, clear OOP usage
- 22 – Satisfactory: Classes exist but responsibilities overlap
- 15 – Developing: Weak class design, limited OOP
- 5 – Needs Improvement: Program mostly inside main()

## Payroll Computation Accuracy (25 pts)

- 25 – Excellent: All computations correct
- 18 – Satisfactory: Minor computation errors
- 12 – Developing: Multiple incorrect computations
- 5 – Needs Improvement: Incorrect payroll logic

## Employee Type Handling (15 pts)

- 15 – Excellent: All employee types handled correctly
- 11 – Satisfactory: One type handled incorrectly
- 7 – Developing: Multiple types incorrect
- 3 – Needs Improvement: No differentiation

## Input Validation & Stability (5 pts)

- 5 – Excellent: Handles invalid input gracefully
- 3 – Satisfactory: Minimal validation
- 1 – Needs Improvement: Program crashes

## User Interaction & Output (15 pts)

- 15 – Excellent: Clear menu and formatted output
- 11 – Satisfactory: Output complete but poorly formatted
- 7 – Developing: Confusing interface
- 3 – Needs Improvement: Incomplete output

## Code Quality & Documentation (10 pts)

- 10 – Excellent: Clean code, good naming, helpful comments
- 7 – Satisfactory: Minor readability issues
- 4 – Developing: Poor naming, few comments
- 1 – Needs Improvement: Unreadable code
