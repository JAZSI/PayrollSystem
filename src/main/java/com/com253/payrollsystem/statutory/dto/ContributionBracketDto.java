package com.com253.payrollsystem.statutory.dto;

public record ContributionBracketDto(
        Long id,
        int seq,
        double lowerBound,
        double upperBound,
        double amount,
        double rate) {
}
