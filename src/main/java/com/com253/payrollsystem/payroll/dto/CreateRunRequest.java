package com.com253.payrollsystem.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Request to create a batch payroll run for a cut-off period. */
public record CreateRunRequest(
        @NotBlank
        @Pattern(regexp = "1st-15th|16th-30th", message = "Period must be '1st-15th' or '16th-30th'")
        String period) {
}
