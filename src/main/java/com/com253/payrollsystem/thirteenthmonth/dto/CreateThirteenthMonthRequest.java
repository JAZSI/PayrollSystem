package com.com253.payrollsystem.thirteenthmonth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Compute a 13th-month run for a calendar year. */
public record CreateThirteenthMonthRequest(

        @Min(value = 2000, message = "Year must be 2000 or later")
        @Max(value = 2100, message = "Year must be 2100 or earlier")
        int year) {
}
