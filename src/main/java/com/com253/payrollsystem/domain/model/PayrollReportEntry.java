package com.com253.payrollsystem.domain.model;

public record PayrollReportEntry(
    int id,
    String employeeId,
    String employeeName,
    String cutOffPeriod,
    double totalHours,
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
    double netPay,
    String createdAt
) {
    public double getTotalDeductions() {
        return sssDeduction + philhealthDeduction + pagibigDeduction
             + taxDeduction + loanDeduction + undertimePenalty + absencePenalty;
    }

    public static PayrollReportEntry fromPayrollEntry(PayrollEntry entry) {
        return new PayrollReportEntry(
            0,
            entry.employee().getEmployeeId(),
            entry.employee().getName(),
            entry.cutOffPeriod(),
            entry.totalHoursWorked(),
            entry.overtimeHours(),
            entry.undertimeHours(),
            entry.absentDays(),
            entry.basicPay(),
            entry.overtimePay(),
            entry.holidayPay(),
            entry.nightShiftDifferential(),
            entry.grossPay(),
            entry.sssDeduction(),
            entry.philhealthDeduction(),
            entry.pagibigDeduction(),
            entry.taxDeduction(),
            entry.loanDeduction(),
            entry.undertimePenalty(),
            entry.absencePenalty(),
            entry.netPay(),
            null);
    }
}