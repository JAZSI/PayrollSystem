package com.com253.payrollsystem.statutory;

import com.com253.payrollsystem.shared.domain.tax.ContributionTables;

import java.util.List;

/**
 * Evaluates contributions from DB bracket rows, falling back to the built-in tables for any
 * agency without an effective version. See {@link ContributionBracketEntity} for field meaning.
 */
public final class DbContributionTables implements ContributionTables {

    private final List<ContributionBracketEntity> sss;
    private final List<ContributionBracketEntity> philhealth;
    private final List<ContributionBracketEntity> pagibig;
    private final List<ContributionBracketEntity> bir;
    private final ContributionTables fallback = ContributionTables.HARDCODED;

    public DbContributionTables(List<ContributionBracketEntity> sss,
                                List<ContributionBracketEntity> philhealth,
                                List<ContributionBracketEntity> pagibig,
                                List<ContributionBracketEntity> bir) {
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.bir = bir;
    }

    @Override
    public double sssEmployeeMonthly(double salary) {
        if (empty(sss)) {
            return fallback.sssEmployeeMonthly(salary);
        }
        return bracketFor(sss, salary).getAmount();
    }

    @Override
    public double sssEmployerMonthly(double salary) {
        if (empty(sss)) {
            return fallback.sssEmployerMonthly(salary);
        }
        return 2.0 * sssEmployeeMonthly(salary);
    }

    @Override
    public double sssEcMonthly(double salary) {
        // EC rarely changes; kept rule-based.
        return fallback.sssEcMonthly(salary);
    }

    @Override
    public double philhealthTotalMonthly(double monthlyRate) {
        if (empty(philhealth)) {
            return fallback.philhealthTotalMonthly(monthlyRate);
        }
        ContributionBracketEntity row = philhealth.get(0);
        double floor = row.getLowerBound();
        double ceiling = row.getUpperBound();
        return Math.min(Math.max(monthlyRate * row.getRate(), floor), ceiling);
    }

    @Override
    public double pagibigEmployeeMonthly(double monthlyRate) {
        if (empty(pagibig)) {
            return fallback.pagibigEmployeeMonthly(monthlyRate);
        }
        ContributionBracketEntity row = bracketFor(pagibig, monthlyRate);
        return Math.min(monthlyRate * row.getRate(), row.getAmount());
    }

    @Override
    public double pagibigEmployerMonthly(double monthlyRate) {
        // Employer Pag-IBIG is 2% by law; kept rule-based.
        return fallback.pagibigEmployerMonthly(monthlyRate);
    }

    @Override
    public double annualWithholdingTax(double annualTaxable) {
        if (empty(bir)) {
            return fallback.annualWithholdingTax(annualTaxable);
        }
        ContributionBracketEntity row = bracketFor(bir, annualTaxable);
        return row.getAmount() + (annualTaxable - row.getLowerBound()) * row.getRate();
    }

    /** First bracket whose upper bound exceeds the value, else the last (open-ended) bracket. */
    private static ContributionBracketEntity bracketFor(List<ContributionBracketEntity> brackets, double value) {
        for (ContributionBracketEntity b : brackets) {
            if (value < b.getUpperBound()) {
                return b;
            }
        }
        return brackets.get(brackets.size() - 1);
    }

    private static boolean empty(List<ContributionBracketEntity> brackets) {
        return brackets == null || brackets.isEmpty();
    }
}
