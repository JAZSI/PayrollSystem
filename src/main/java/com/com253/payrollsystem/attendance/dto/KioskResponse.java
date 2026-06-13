package com.com253.payrollsystem.attendance.dto;

/** Result of a kiosk clock punch (action = CLOCK_IN/CLOCK_OUT/ALREADY_COMPLETE). */
public record KioskResponse(
        String action,
        String employeeId,
        String employeeName,
        String time,
        String date,
        String message) {
}
