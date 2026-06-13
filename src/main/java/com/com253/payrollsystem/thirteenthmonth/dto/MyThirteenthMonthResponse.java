package com.com253.payrollsystem.thirteenthmonth.dto;

/** Employee self-view: 13th-month amount for a year (locked runs only). */
public record MyThirteenthMonthResponse(
        int year,
        double amount) {
}
