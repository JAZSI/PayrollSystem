package com.com253.payrollsystem.loan.dto;

import com.com253.payrollsystem.loan.LoanStatus;
import com.com253.payrollsystem.loan.LoanType;

public record LoanResponse(
        Long id,
        String employeeId,
        LoanType type,
        String typeLabel,
        double principal,
        double perCutoffAmount,
        double balance,
        LoanStatus status,
        String startPeriod,
        String createdAt) {
}
