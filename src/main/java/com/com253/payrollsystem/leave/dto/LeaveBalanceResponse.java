package com.com253.payrollsystem.leave.dto;

public record LeaveBalanceResponse(
        Long leaveTypeId,
        String leaveTypeName,
        boolean paid,
        int year,
        int credits,
        int used,
        int remaining) {
}
