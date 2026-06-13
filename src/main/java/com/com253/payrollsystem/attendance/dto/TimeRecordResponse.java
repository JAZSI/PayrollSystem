package com.com253.payrollsystem.attendance.dto;

import com.com253.payrollsystem.shared.domain.HolidayType;

/** One day's stored attendance returned to clients. */
public record TimeRecordResponse(
        Long id,
        int dayNumber,
        int timeIn,
        int timeOut,
        boolean absent,
        HolidayType holidayType) {
}
