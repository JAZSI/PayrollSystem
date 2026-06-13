package com.com253.payrollsystem.thirteenthmonth.dto;

import com.com253.payrollsystem.payroll.PayrollRunStatus;

import java.util.List;

public record ThirteenthMonthRunResponse(
        Long id,
        int year,
        PayrollRunStatus status,
        int employeeCount,
        double totalAmount,
        String createdAt,
        List<ThirteenthMonthEntryResponse> entries) {
}
