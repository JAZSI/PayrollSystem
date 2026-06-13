package com.com253.payrollsystem.leave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record LeaveTypeRequest(

        @NotBlank String name,

        boolean paid,

        @PositiveOrZero(message = "Default credits cannot be negative")
        int defaultAnnualCredits) {
}
