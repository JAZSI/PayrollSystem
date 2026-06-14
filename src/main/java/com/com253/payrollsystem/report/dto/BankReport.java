package com.com253.payrollsystem.report.dto;

import java.util.List;

public record BankReport(
        String period,
        List<BankRow> rows,
        double totalNet) {
}
