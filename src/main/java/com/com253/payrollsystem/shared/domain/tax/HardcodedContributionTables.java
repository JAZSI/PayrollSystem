package com.com253.payrollsystem.shared.domain.tax;

/** Default tables: delegates to the built-in constant classes (no behavior change). */
final class HardcodedContributionTables implements ContributionTables {

    @Override
    public double sssEmployeeMonthly(double salary) {
        return SSS.monthlyContribution(salary);
    }

    @Override
    public double sssEmployerMonthly(double salary) {
        return SSS.employerContribution(salary);
    }

    @Override
    public double sssEcMonthly(double salary) {
        return SSS.employerCompensation(salary);
    }

    @Override
    public double philhealthTotalMonthly(double monthlyRate) {
        return PhilHealth.monthlyContribution(monthlyRate);
    }

    @Override
    public double pagibigEmployeeMonthly(double monthlyRate) {
        return Pagibig.monthlyContribution(monthlyRate);
    }

    @Override
    public double pagibigEmployerMonthly(double monthlyRate) {
        return Pagibig.employerShare(monthlyRate);
    }

    @Override
    public double annualWithholdingTax(double annualTaxable) {
        return WithholdingTax.annualTax(annualTaxable);
    }
}
