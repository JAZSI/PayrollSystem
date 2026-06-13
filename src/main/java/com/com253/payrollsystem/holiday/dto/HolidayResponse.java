package com.com253.payrollsystem.holiday.dto;

import com.com253.payrollsystem.shared.domain.HolidayType;

/** A holiday returned to clients. {@code date} is ISO "yyyy-MM-dd". */
public record HolidayResponse(
        Long id,
        String date,
        String name,
        HolidayType type) {
}
