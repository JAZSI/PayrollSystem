package com.com253.payrollsystem.shared.domain;

import com.com253.payrollsystem.shared.domain.employeetypes.Regular;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** Allowances flow into gross; only taxable ones raise tax; other deductions cut net. */
class PayrollCalculatorPayContextTest {

    private static final double EPS = 1e-9;

    private static final PayrollSettings SETTINGS =
            new PayrollSettings(26, 8.0, 17.0, 11.0, 5, 5, 0, 0);

    private static TimeRecord[] fifteenStandardDays() {
        TimeRecord[] r = new TimeRecord[15];
        for (int i = 0; i < 15; i++) {
            r[i] = new TimeRecord(i + 1, 800, 1700, false, HolidayType.NONE);
        }
        return r;
    }

    private static PayrollEntry run(PayContext ctx) {
        return PayrollCalculator.buildPayrollEntry(
                new Regular("1234-5678-90", "John", 30000.00),
                fifteenStandardDays(), "1st-15th", SETTINGS, ctx);
    }

    @Test
    void nonTaxableAllowanceAddsFullAmountToNet() {
        // Baseline net is 13,281.87; a 1,000 non-taxable allowance adds exactly 1,000.
        PayrollEntry e = run(new PayContext(0, 0, 0.0, 1000.0, 0.0));

        assertThat(e.getAllowances()).isEqualTo(1000.0, within(EPS));
        assertThat(e.getGrossPay()).isEqualTo(16000.0, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(14281.87, within(EPS));
    }

    @Test
    void taxableAllowanceIsTaxedSoNetIsLower() {
        PayrollEntry taxable = run(new PayContext(0, 0, 1000.0, 0.0, 0.0));
        PayrollEntry nonTaxable = run(new PayContext(0, 0, 0.0, 1000.0, 0.0));

        assertThat(taxable.getGrossPay()).isEqualTo(nonTaxable.getGrossPay(), within(EPS));
        assertThat(taxable.getTaxDeduction()).isGreaterThan(nonTaxable.getTaxDeduction());
        assertThat(taxable.getNetPay()).isLessThan(nonTaxable.getNetPay());
    }

    @Test
    void otherDeductionsReduceNet() {
        PayrollEntry e = run(new PayContext(0, 0, 0.0, 0.0, 200.0));

        assertThat(e.getOtherDeductions()).isEqualTo(200.0, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(13081.87, within(EPS));
    }
}
