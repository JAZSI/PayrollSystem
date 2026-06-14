package com.com253.payrollsystem.report;

import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.payroll.PayrollRepository;
import com.com253.payrollsystem.payroll.PayslipEntity;
import com.com253.payrollsystem.report.dto.RegisterReport;
import com.com253.payrollsystem.report.dto.RemittanceReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

/** Register/remittance totals must equal the sum of the period's payslips. */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private static final double EPS = 1e-6;

    @Mock PayrollRepository payslips;
    @Mock EmployeeRepository employees;

    @InjectMocks ReportService service;

    @Test
    void registerTotalsSumThePayslips() {
        when(payslips.findByCutoffPeriodOrderByEmployeeName("1st-15th"))
                .thenReturn(List.of(slip(15000.0, 13281.87), slip(12000.0, 10929.5)));

        RegisterReport r = service.register("1st-15th");

        assertThat(r.rows()).hasSize(2);
        assertThat(r.totalGross()).isEqualTo(27000.0, within(EPS));
        assertThat(r.totalNet()).isEqualTo(24211.37, within(EPS));
        assertThat(r.totalDeductions()).isEqualTo(2788.63, within(EPS));
    }

    @Test
    void remittanceSumsEmployeeAndEmployerShares() {
        PayslipEntity a = slip(15000.0, 13000.0);
        a.setSss(750.0);
        a.setEmployerSss(1500.0);
        a.setEmployerEc(15.0);
        a.setPhilhealth(412.5);
        a.setEmployerPhilhealth(412.5);
        a.setTax(500.0);
        when(payslips.findByCutoffPeriodOrderByEmployeeName("1st-15th")).thenReturn(List.of(a));

        RemittanceReport r = service.remittance("1st-15th");

        assertThat(r.sssEmployee()).isEqualTo(750.0, within(EPS));
        assertThat(r.sssEmployer()).isEqualTo(1500.0, within(EPS));
        assertThat(r.sssEc()).isEqualTo(15.0, within(EPS));
        assertThat(r.grandTotal())
                .isEqualTo(750.0 + 1500.0 + 15.0 + 412.5 + 412.5 + 500.0, within(EPS));
    }

    private static PayslipEntity slip(double gross, double net) {
        PayslipEntity s = new PayslipEntity();
        s.setEmployeeId("E1");
        s.setEmployeeName("Worker");
        s.setGrossPay(gross);
        s.setNetPay(net);
        return s;
    }
}
