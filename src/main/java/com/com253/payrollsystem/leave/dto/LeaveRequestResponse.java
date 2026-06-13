package com.com253.payrollsystem.leave.dto;

import com.com253.payrollsystem.leave.LeaveStatus;

public record LeaveRequestResponse(
        Long id,
        String employeeId,
        Long leaveTypeId,
        String leaveTypeName,
        String startDate,
        String endDate,
        int days,
        LeaveStatus status,
        String reason,
        String decidedBy,
        String createdAt) {
}
