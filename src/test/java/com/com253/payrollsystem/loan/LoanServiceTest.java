package com.com253.payrollsystem.loan;

import com.com253.payrollsystem.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Loan deduction total, balance decrement, and cap-at-balance on posting. */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    private static final double EPS = 1e-9;

    @Mock LoanRepository loans;
    @Mock LoanPaymentRepository payments;
    @Mock EmployeeRepository employees;

    @InjectMocks LoanService service;

    @Test
    void cutoffTotalSumsActiveLoansCappedAtBalance() {
        LoanEntity a = loan(500.0, 5000.0);   // full per-cut-off
        LoanEntity b = loan(800.0, 300.0);    // capped at 300 balance
        when(loans.findByEmployeeIdAndStatus("E1", LoanStatus.ACTIVE)).thenReturn(List.of(a, b));

        assertThat(service.activeCutoffTotal("E1")).isEqualTo(800.0, within());
    }

    @Test
    void postingDecrementsBalanceAndKeepsActive() {
        LoanEntity loan = loan(500.0, 5000.0);
        when(loans.findByEmployeeIdAndStatus("E1", LoanStatus.ACTIVE)).thenReturn(List.of(loan));
        when(loans.save(any(LoanEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        service.postForRun("1st-15th", Map.of("E1", 99L));

        assertThat(loan.getBalance()).isEqualTo(4500.0, within());
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    void postingCapsAtBalanceAndMarksPaid() {
        LoanEntity loan = loan(500.0, 300.0);
        when(loans.findByEmployeeIdAndStatus("E1", LoanStatus.ACTIVE)).thenReturn(List.of(loan));
        when(loans.save(any(LoanEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        service.postForRun("1st-15th", Map.of("E1", 99L));

        assertThat(loan.getBalance()).isEqualTo(0.0, within());
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.PAID);
    }

    private static LoanEntity loan(double perCutoff, double balance) {
        LoanEntity l = new LoanEntity("E1", LoanType.COMPANY, balance, perCutoff, "1st-15th");
        l.setBalance(balance);
        return l;
    }

    private static org.assertj.core.data.Offset<Double> within() {
        return org.assertj.core.data.Offset.offset(EPS);
    }
}
