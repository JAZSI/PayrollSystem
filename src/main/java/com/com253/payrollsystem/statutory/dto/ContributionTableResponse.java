package com.com253.payrollsystem.statutory.dto;

import com.com253.payrollsystem.statutory.ContributionAgency;

import java.util.List;

public record ContributionTableResponse(
        Long id,
        ContributionAgency agency,
        String effectiveFrom,
        boolean active,
        String note,
        List<ContributionBracketDto> brackets) {
}
