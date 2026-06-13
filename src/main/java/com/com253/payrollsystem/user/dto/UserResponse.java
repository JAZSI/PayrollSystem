package com.com253.payrollsystem.user.dto;

import com.com253.payrollsystem.shared.security.Role;

/** A user account summary (no password). */
public record UserResponse(
        String username,
        Role role,
        String employeeId) {
}
