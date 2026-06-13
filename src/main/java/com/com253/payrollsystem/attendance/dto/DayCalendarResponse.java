package com.com253.payrollsystem.attendance.dto;

import com.com253.payrollsystem.shared.domain.HolidayType;

/** A working day with its automatically-determined holiday classification. */
public record DayCalendarResponse(
        int dayNumber,
        HolidayType holidayType,
        String holidayName) {
}
