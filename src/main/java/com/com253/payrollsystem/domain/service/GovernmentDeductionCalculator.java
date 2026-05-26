package com.com253.payrollsystem.domain.service;

import com.com253.payrollsystem.domain.service.tax.Pagibig;
import com.com253.payrollsystem.domain.service.tax.PhilHealth;
import com.com253.payrollsystem.domain.service.tax.SSS;
import com.com253.payrollsystem.domain.service.tax.WithholdingTax;

public final class GovernmentDeductionCalculator {

    private GovernmentDeductionCalculator() {}

    public static double computeSSSDeduction(double salary) {
        return SSS.monthlyContribution(salary) / 2.0;
    }

    public static double computePhilHealthDeduction(double monthlyRate) {
        return PhilHealth.monthlyContribution(monthlyRate) / 4.0;
    }

    public static double computePagibigDeduction(double monthlyRate) {
        return Pagibig.monthlyContribution(monthlyRate) / 2.0;
    }

    public static double computeWithholdingTax(double taxableIncome) {
        double annualIncome = taxableIncome * 24.0;
        double annualTax = WithholdingTax.annualTax(annualIncome);
        return annualTax / 24.0;
    }
}
