package com.com253.payrollsystem.holiday.dto;

import com.com253.payrollsystem.shared.domain.HolidayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Create/update request for a holiday. */
public record HolidayRequest(
        @NotNull(message = "Date is required") LocalDate date,
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Type is required") HolidayType type) {
}
