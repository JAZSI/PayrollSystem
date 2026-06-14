package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.audit.AuditService;
import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.attendance.TimeRecordRepository;
import com.com253.payrollsystem.leave.LeaveService;
import com.com253.payrollsystem.loan.LoanService;
import com.com253.payrollsystem.payitem.PayItemService;
import com.com253.payrollsystem.settings.SettingsService;
import com.com253.payrollsystem.statutory.ContributionTableProvider;
import com.com253.payrollsystem.payroll.dto.PayrollRunResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Lifecycle guards for batch payroll runs: DRAFT -> APPROVED -> LOCKED. */
@ExtendWith(MockitoExtension.class)
class PayrollRunServiceTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock TimeRecordRepository timeRecordRepository;
    @Mock PayrollRepository payrollRepository;
    @Mock PayrollRunRepository runRepository;
    @Mock SettingsService settingsService;
    @Mock PeriodLockGuard periodLockGuard;
    @Mock LoanService loanService;
    @Mock LeaveService leaveService;
    @Mock PayItemService payItemService;
    @Mock ContributionTableProvider contributionTables;
    @Mock AuditService auditService;

    @InjectMocks PayrollRunService service;

    @Test
    void approveMovesDraftToApproved() {
        PayrollRunEntity run = new PayrollRunEntity("1st-15th"); // DRAFT
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));
        when(runRepository.save(run)).thenReturn(run);
        when(payrollRepository.findByRunIdOrderByEmployeeName(any())).thenReturn(List.of());

        PayrollRunResponse res = service.approve(1L);

        assertThat(res.status()).isEqualTo(PayrollRunStatus.APPROVED);
    }

    @Test
    void lockMovesApprovedToLocked() {
        PayrollRunEntity run = new PayrollRunEntity("1st-15th");
        run.setStatus(PayrollRunStatus.APPROVED);
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));
        when(runRepository.save(run)).thenReturn(run);
        when(payrollRepository.findByRunIdOrderByEmployeeName(any())).thenReturn(List.of());

        PayrollRunResponse res = service.lock(1L);

        assertThat(res.status()).isEqualTo(PayrollRunStatus.LOCKED);
        verify(auditService).record(eq("LOCK"), eq("PayrollRun"), eq("1"), any());
    }

    @Test
    void cannotApproveAlreadyApprovedRun() {
        PayrollRunEntity run = new PayrollRunEntity("1st-15th");
        run.setStatus(PayrollRunStatus.APPROVED);
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));

        assertThatThrownBy(() -> service.approve(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cannotLockDraftRun() {
        PayrollRunEntity run = new PayrollRunEntity("1st-15th"); // DRAFT
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));

        assertThatThrownBy(() -> service.lock(1L))
                .isInstanceOf(IllegalStateException.class);
    }
}
