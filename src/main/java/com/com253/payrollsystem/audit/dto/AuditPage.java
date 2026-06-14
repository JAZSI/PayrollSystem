package com.com253.payrollsystem.audit.dto;

import java.util.List;

public record AuditPage(
        List<AuditResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
