package com.com253.payrollsystem.user.dto;

import com.com253.payrollsystem.employee.dto.EmployeeResponse;

import com.com253.payrollsystem.shared.security.Role;

/** The current user's account plus their linked employee record (if any). */
public record MeResponse(
        String username,
        Role role,
        EmployeeResponse employee) {
}
