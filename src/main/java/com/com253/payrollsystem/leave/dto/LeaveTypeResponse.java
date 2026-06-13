package com.com253.payrollsystem.leave.dto;

public record LeaveTypeResponse(
        Long id,
        String name,
        boolean paid,
        int defaultAnnualCredits) {
}
