package com.com253.payrollsystem.leave;

import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.holiday.HolidayRepository;
import com.com253.payrollsystem.leave.dto.LeaveRequestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/** Balance decrement on approval and paid-leave coverage counting for payroll. */
@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock LeaveTypeRepository types;
    @Mock LeaveBalanceRepository balances;
    @Mock LeaveRequestRepository requests;
    @Mock EmployeeRepository employees;
    @Mock HolidayRepository holidays;

    @InjectMocks LeaveService service;

    private static LeaveType paidType() {
        LeaveType type = new LeaveType("Vacation Leave", true, 5);
        ReflectionTestUtils.setField(type, "id", 1L);
        return type;
    }

    @Test
    void approveDecrementsPaidBalance() {
        LeaveType type = paidType();
        LeaveBalance balance = new LeaveBalance("E1", 1L, 2026, 5);
        LeaveRequest request = new LeaveRequest(
                "E1", 1L, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 2), 2, "trip");

        when(requests.findById(10L)).thenReturn(Optional.of(request));
        when(types.findById(1L)).thenReturn(Optional.of(type));
        when(types.findAll()).thenReturn(List.of(type));
        when(balances.findByEmployeeIdAndLeaveTypeIdAndYear(eq("E1"), eq(1L), anyInt()))
                .thenReturn(Optional.of(balance));
        when(requests.save(request)).thenReturn(request);

        LeaveRequestResponse res = service.approve(10L, "admin");

        assertThat(res.status()).isEqualTo(LeaveStatus.APPROVED);
        assertThat(balance.getUsed()).isEqualTo(2);
        assertThat(balance.getRemaining()).isEqualTo(3);
    }

    @Test
    void coveredDaysCountsApprovedPaidLeaveInPeriod() {
        LeaveType type = paidType();
        // Mon-Wed, all in the 1st-15th bucket.
        LeaveRequest request = new LeaveRequest(
                "E1", 1L, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3), 3, null);
        request.setStatus(LeaveStatus.APPROVED);

        when(types.findAll()).thenReturn(List.of(type));
        when(requests.findByEmployeeIdAndStatus("E1", LeaveStatus.APPROVED))
                .thenReturn(List.of(request));
        when(holidays.existsByDate(any())).thenReturn(false);

        assertThat(service.coveredDaysFor("E1", "1st-15th")).isEqualTo(3);
        assertThat(service.coveredDaysFor("E1", "16th-30th")).isEqualTo(0);
    }
}
