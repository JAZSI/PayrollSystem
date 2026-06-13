package com.com253.payrollsystem.settings.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

/** Create/update request for the single payroll settings row. */
public record SettingsRequest(

        @Min(value = 1, message = "Working days must be at least 1")
        @Max(value = 31, message = "Working days cannot exceed 31")
        int workingDays,

        @PositiveOrZero double workdayStartHour,
        @PositiveOrZero double overtimeStartHour,
        @PositiveOrZero double lunchStartHour,

        @PositiveOrZero int leaveRegular,
        @PositiveOrZero int leaveProbationary,
        @PositiveOrZero int leaveContractual,
        @PositiveOrZero int leavePartTimer) {
}
