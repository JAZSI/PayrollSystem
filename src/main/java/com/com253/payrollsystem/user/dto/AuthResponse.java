package com.com253.payrollsystem.user.dto;

import com.com253.payrollsystem.shared.security.Role;

/** Returned on successful login. */
public record AuthResponse(
        String token,
        String username,
        Role role,
        String employeeId) {
}
