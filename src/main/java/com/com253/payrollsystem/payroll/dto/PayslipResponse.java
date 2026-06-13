package com.com253.payrollsystem.payroll.dto;

import com.com253.payrollsystem.shared.domain.EmployeeType;

/** A computed/saved payslip returned to clients (maps 1:1 from the domain PayrollEntry). */
public record PayslipResponse(
        Long id,
        String employeeId,
        String employeeName,
        EmployeeType employeeType,
        String employeeTypeLabel,
        String cutoffPeriod,
        double totalHours,
        double overtimeHours,
        double undertimeHours,
        int absentDays,
        double basicPay,
        double overtimePay,
        double nightDiffPay,
        double allowances,
        double grossPay,
        double sss,
        double philhealth,
        double pagibig,
        double tax,
        double loan,
        double otherDeductions,
        double undertimePenalty,
        double absencePenalty,
        double employerSss,
        double employerPhilhealth,
        double employerPagibig,
        double employerEc,
        double netPay,
        String createdAt) {
}
