package com.com253.payrollsystem.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/** Replaces all attendance for an employee in a cut-off period. */
public record SaveAttendanceRequest(

        @NotBlank String employeeId,

        @NotBlank
        @Pattern(regexp = "1st-15th|16th-30th", message = "Period must be '1st-15th' or '16th-30th'")
        String cutoffPeriod,

        @Min(2000) @Max(2100) int year,
        @Min(1) @Max(12) int month,

        @NotNull @Valid List<TimeRecordRequest> records) {
}
