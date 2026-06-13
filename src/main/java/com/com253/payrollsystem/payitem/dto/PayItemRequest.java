package com.com253.payrollsystem.payitem.dto;

import com.com253.payrollsystem.payitem.PayItemKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Create/update a pay item. On update, employeeId is ignored. */
public record PayItemRequest(

        @NotBlank String employeeId,

        @NotNull PayItemKind kind,

        @NotBlank String name,

        @Positive(message = "Amount must be greater than zero")
        double amount,

        boolean taxable,

        boolean recurring) {
}
