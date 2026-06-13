package com.com253.payrollsystem.leave.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** File a leave application. On self-service, employeeId is taken from the session. */
public record LeaveRequestRequest(

        String employeeId,

        @NotNull Long leaveTypeId,

        @NotNull LocalDate startDate,

        @NotNull LocalDate endDate,

        String reason) {
}
