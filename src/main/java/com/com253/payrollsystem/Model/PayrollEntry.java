package com.com253.payrollsystem.Model;

public record PayrollEntry(
    Employee employee,
    String cutOffPeriod,
    double totalHoursWorked,
    double overtimeHours,
    double undertimeHours,
    int absentDays,
    double basicPay,
    double overtimePay,
    double holidayPay,
    double nightShiftDifferential,
    double grossPay,
    double sssDeduction,
    double philhealthDeduction,
    double pagibigDeduction,
    double taxDeduction,
    double loanDeduction,
    double undertimePenalty,
    double absencePenalty,
    double netPay
) {}