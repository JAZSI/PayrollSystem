package com.com253.payrollsystem.payitem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

/** Active pay items split into taxable/non-taxable allowances and other deductions. */
@ExtendWith(MockitoExtension.class)
class PayItemServiceTest {

    @Mock PayItemRepository items;
    @Mock com.com253.payrollsystem.employee.EmployeeRepository employees;

    @InjectMocks PayItemService service;

    @Test
    void totalsSplitByKindAndTaxableFlag() {
        when(items.findByEmployeeIdAndActiveTrue("E1")).thenReturn(List.of(
                item(PayItemKind.ALLOWANCE, 1000.0, true),   // taxable allowance
                item(PayItemKind.ALLOWANCE, 500.0, false),   // non-taxable allowance
                item(PayItemKind.DEDUCTION, 250.0, false)));  // other deduction

        PayItemTotals totals = service.totalsFor("E1");

        assertThat(totals.taxableAllowances()).isEqualTo(1000.0, within(1e-9));
        assertThat(totals.nonTaxableAllowances()).isEqualTo(500.0, within(1e-9));
        assertThat(totals.otherDeductions()).isEqualTo(250.0, within(1e-9));
    }

    private static PayItemEntity item(PayItemKind kind, double amount, boolean taxable) {
        return new PayItemEntity("E1", kind, kind.name(), amount, taxable, true);
    }
}
