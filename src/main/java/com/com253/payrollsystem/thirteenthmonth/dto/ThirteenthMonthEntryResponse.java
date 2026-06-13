package com.com253.payrollsystem.thirteenthmonth.dto;

public record ThirteenthMonthEntryResponse(
        Long id,
        String employeeId,
        String employeeName,
        double totalBasic,
        double amount) {
}
