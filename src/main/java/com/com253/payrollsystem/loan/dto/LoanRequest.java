package com.com253.payrollsystem.loan.dto;

import com.com253.payrollsystem.loan.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Create/update a loan. On update, employeeId is ignored. */
public record LoanRequest(

        @NotBlank String employeeId,

        @NotNull LoanType type,

        @Positive(message = "Principal must be greater than zero")
        double principal,

        @Positive(message = "Per cut-off amount must be greater than zero")
        double perCutoffAmount,

        String startPeriod) {
}
