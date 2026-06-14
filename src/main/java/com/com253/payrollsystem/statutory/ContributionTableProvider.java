package com.com253.payrollsystem.statutory;

import com.com253.payrollsystem.shared.domain.tax.ContributionTables;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Resolves the contribution tables effective for a date into a {@link ContributionTables}. */
@Service
@Transactional(readOnly = true)
public class ContributionTableProvider {

    private final ContributionTableRepository tables;
    private final ContributionBracketRepository brackets;

    public ContributionTableProvider(ContributionTableRepository tables,
                                     ContributionBracketRepository brackets) {
        this.tables = tables;
        this.brackets = brackets;
    }

    public ContributionTables tablesFor(LocalDate date) {
        return new DbContributionTables(
                bracketsFor(ContributionAgency.SSS, date),
                bracketsFor(ContributionAgency.PHILHEALTH, date),
                bracketsFor(ContributionAgency.PAGIBIG, date),
                bracketsFor(ContributionAgency.BIR, date));
    }

    private List<ContributionBracketEntity> bracketsFor(ContributionAgency agency, LocalDate date) {
        return tables
                .findFirstByAgencyAndActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(agency, date)
                .map(t -> brackets.findByTableIdOrderBySeq(t.getId()))
                .orElse(null);
    }
}
