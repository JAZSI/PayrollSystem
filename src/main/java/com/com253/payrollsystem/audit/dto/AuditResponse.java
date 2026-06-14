package com.com253.payrollsystem.audit.dto;

import com.com253.payrollsystem.shared.security.Role;

public record AuditResponse(
        Long id,
        String username,
        Role role,
        String action,
        String entity,
        String entityId,
        String summary,
        String createdAt) {
}
