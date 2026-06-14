package com.com253.payrollsystem.report.dto;

public record BankRow(
        String employeeId,
        String employeeName,
        String bankAccount,
        double netPay) {
}
