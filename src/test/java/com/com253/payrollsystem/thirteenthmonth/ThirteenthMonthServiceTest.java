package com.com253.payrollsystem.thirteenthmonth;

import com.com253.payrollsystem.payroll.PayrollRepository;
import com.com253.payrollsystem.payroll.PayslipEntity;
import com.com253.payrollsystem.thirteenthmonth.dto.ThirteenthMonthRunResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** 13th-month = sum of a year's basic pay / 12, per employee. */
@ExtendWith(MockitoExtension.class)
class ThirteenthMonthServiceTest {

    @Mock ThirteenthMonthRunRepository runs;
    @Mock ThirteenthMonthEntryRepository entries;
    @Mock PayrollRepository payslips;

    @InjectMocks ThirteenthMonthService service;

    @Test
    void sumsYearBasicPayAndDividesBy12() {
        // E1: 6 x 15,000 basic in 2026 -> 90,000 / 12 = 7,500.
        List<PayslipEntity> slips = List.of(
                slip("E1", "Alice", 15000.0, 2026),
                slip("E1", "Alice", 15000.0, 2026),
                slip("E1", "Alice", 15000.0, 2026),
                slip("E1", "Alice", 15000.0, 2026),
                slip("E1", "Alice", 15000.0, 2026),
                slip("E1", "Alice", 15000.0, 2026),
                slip("E2", "Bob", 99999.0, 2025)); // different year -> excluded

        when(payslips.findAllByOrderByCreatedAtDesc()).thenReturn(slips);
        when(runs.save(any(ThirteenthMonthRun.class))).thenAnswer(inv -> inv.getArgument(0));
        when(entries.save(any(ThirteenthMonthEntry.class))).thenAnswer(inv -> inv.getArgument(0));
        when(entries.findByRunIdOrderByEmployeeName(any())).thenReturn(List.of());

        ThirteenthMonthRunResponse res = service.createRun(2026);

        assertThat(res.employeeCount()).isEqualTo(1);
        assertThat(res.totalAmount()).isEqualTo(7500.0, within(1e-9));
    }

    private static PayslipEntity slip(String empId, String name, double basic, int year) {
        PayslipEntity s = new PayslipEntity();
        s.setEmployeeId(empId);
        s.setEmployeeName(name);
        s.setBasicPay(basic);
        ReflectionTestUtils.setField(s, "createdAt", LocalDateTime.of(year, 6, 15, 9, 0));
        return s;
    }
}
