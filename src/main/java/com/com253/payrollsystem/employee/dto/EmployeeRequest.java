package com.com253.payrollsystem.employee.dto;

import com.com253.payrollsystem.shared.domain.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Employee create/update; optional password creates a login (username = id). */
public record EmployeeRequest(

        @NotBlank(message = "Employee id is required")
        @Pattern(regexp = "[0-9-]+", message = "Use digits and hyphens only")
        String id,

        @NotBlank(message = "Full name is required")
        @Pattern(regexp = "[A-Za-z]+(?:[ .'-](?:[A-Za-z]+|[A-Za-z]\\.))*",
                message = "Use letters, spaces, periods, apostrophes, or hyphens only")
        String fullName,

        @NotNull(message = "Employee type is required")
        EmployeeType type,

        @PositiveOrZero(message = "Monthly rate cannot be negative")
        double monthlyRate,

        @PositiveOrZero(message = "Hourly rate cannot be negative")
        double hourlyRate,

        @Size(max = 60, message = "Bank account is too long")
        String bankAccount,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password) {
}
