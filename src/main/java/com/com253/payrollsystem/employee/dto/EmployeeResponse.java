package com.com253.payrollsystem.employee.dto;

import com.com253.payrollsystem.shared.domain.EmployeeType;

/** Employee data returned to clients. */
public record EmployeeResponse(
        String id,
        String fullName,
        EmployeeType type,
        String typeLabel,
        double monthlyRate,
        double hourlyRate,
        boolean active,
        String createdAt) {
}
