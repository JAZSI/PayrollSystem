package com.com253.payrollsystem.report.dto;

public record RegisterRow(
        String employeeId,
        String employeeName,
        double grossPay,
        double sss,
        double philhealth,
        double pagibig,
        double tax,
        double loan,
        double otherDeductions,
        double penalties,
        double totalDeductions,
        double netPay) {
}
