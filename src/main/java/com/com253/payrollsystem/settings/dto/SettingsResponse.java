package com.com253.payrollsystem.settings.dto;

/** Payroll settings returned to clients. */
public record SettingsResponse(
        int workingDays,
        double workdayStartHour,
        double overtimeStartHour,
        double lunchStartHour,
        int leaveRegular,
        int leaveProbationary,
        int leaveContractual,
        int leavePartTimer) {
}
