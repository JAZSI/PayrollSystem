package com.com253.payrollsystem.payitem.dto;

import com.com253.payrollsystem.payitem.PayItemKind;

public record PayItemResponse(
        Long id,
        String employeeId,
        PayItemKind kind,
        String name,
        double amount,
        boolean taxable,
        boolean recurring,
        boolean active,
        String createdAt) {
}
