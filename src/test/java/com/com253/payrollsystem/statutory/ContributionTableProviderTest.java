package com.com253.payrollsystem.statutory;

import com.com253.payrollsystem.shared.domain.tax.ContributionTables;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** The seeded DB tables must reproduce the built-in constants exactly (no behavior change). */
@SpringBootTest
@AutoConfigureMockMvc
class ContributionTableProviderTest {

    private static final double EPS = 1e-9;

    @Autowired ContributionTableProvider provider;

    @Test
    void seededTablesReproduceHardcodedContributions() {
        ContributionTables db = provider.tablesFor(LocalDate.of(2026, 6, 1));
        ContributionTables hc = ContributionTables.HARDCODED;

        for (double salary : new double[]{0, 4_000, 8_000, 14_700, 15_000, 30_000, 40_000}) {
            assertThat(db.sssEmployeeMonthly(salary)).isEqualTo(hc.sssEmployeeMonthly(salary), within(EPS));
            assertThat(db.sssEmployerMonthly(salary)).isEqualTo(hc.sssEmployerMonthly(salary), within(EPS));
            assertThat(db.sssEcMonthly(salary)).isEqualTo(hc.sssEcMonthly(salary), within(EPS));
            assertThat(db.philhealthTotalMonthly(salary)).isEqualTo(hc.philhealthTotalMonthly(salary), within(EPS));
            assertThat(db.pagibigEmployeeMonthly(salary)).isEqualTo(hc.pagibigEmployeeMonthly(salary), within(EPS));
            assertThat(db.pagibigEmployerMonthly(salary)).isEqualTo(hc.pagibigEmployerMonthly(salary), within(EPS));
        }

        for (double annual : new double[]{0, 250_000, 300_000, 400_000, 500_000, 1_000_000, 3_000_000, 9_000_000}) {
            assertThat(db.annualWithholdingTax(annual)).isEqualTo(hc.annualWithholdingTax(annual), within(EPS));
        }
    }
}
