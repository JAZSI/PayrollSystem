package com.com253.payrollsystem.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Request to compute and save a payslip for an employee + cut-off. Loans are derived. */
public record RunPayrollRequest(

        @NotBlank String employeeId,

        @NotBlank
        @Pattern(regexp = "1st-15th|16th-30th", message = "Period must be '1st-15th' or '16th-30th'")
        String period) {
}
