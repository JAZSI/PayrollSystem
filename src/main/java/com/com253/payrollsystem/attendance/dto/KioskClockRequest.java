package com.com253.payrollsystem.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** A kiosk clock punch: the employee enters their id. */
public record KioskClockRequest(
        @NotBlank
        @Pattern(regexp = "[0-9-]+", message = "Use digits and hyphens only")
        String employeeId) {
}
