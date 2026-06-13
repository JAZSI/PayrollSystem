package com.com253.payrollsystem.attendance.dto;

import com.com253.payrollsystem.shared.domain.HolidayType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** One day's attendance in a save-attendance request. Time-order is validated in the service. */
public record TimeRecordRequest(

        @Min(1) @Max(31) int dayNumber,

        @Min(0) @Max(2359) int timeIn,
        @Min(0) @Max(2359) int timeOut,

        boolean absent,

        @NotNull HolidayType holidayType) {
}
