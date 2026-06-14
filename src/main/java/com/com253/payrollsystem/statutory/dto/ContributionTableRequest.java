package com.com253.payrollsystem.statutory.dto;

import com.com253.payrollsystem.statutory.ContributionAgency;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/** Create or replace a contribution table version (brackets are replaced wholesale). */
public record ContributionTableRequest(

        @NotNull ContributionAgency agency,

        @NotNull LocalDate effectiveFrom,

        boolean active,

        String note,

        @NotNull List<ContributionBracketDto> brackets) {
}
