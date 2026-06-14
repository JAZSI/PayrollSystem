package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.shared.domain.EmployeeType;
import com.com253.payrollsystem.shared.domain.HolidayType;
import com.com253.payrollsystem.shared.domain.PayrollSettings;
import com.com253.payrollsystem.employee.EmployeeEntity;
import com.com253.payrollsystem.settings.SettingsEntity;
import com.com253.payrollsystem.attendance.TimeRecordEntity;
import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.attendance.TimeRecordRepository;
import com.com253.payrollsystem.leave.LeaveService;
import com.com253.payrollsystem.loan.LoanService;
import com.com253.payrollsystem.payitem.PayItemService;
import com.com253.payrollsystem.payitem.PayItemTotals;
import com.com253.payrollsystem.settings.SettingsService;
import com.com253.payrollsystem.statutory.ContributionTableProvider;
import com.com253.payrollsystem.shared.domain.tax.ContributionTables;
import com.com253.payrollsystem.payroll.dto.PayslipResponse;
import com.com253.payrollsystem.payroll.dto.RunPayrollRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Acceptance test: the payroll REST/service layer must reproduce the domain engine
 * exactly. Mirrors {@code PayrollCalculatorCharacterizationTest.regularBaseline}
 * (Regular @ PHP 30,000, 1st-15th, all present) -> net 13,281.875.
 */
@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    private static final double EPS = 1e-9;

    @Mock EmployeeRepository employeeRepository;
    @Mock TimeRecordRepository timeRecordRepository;
    @Mock PayrollRepository payrollRepository;
    @Mock SettingsService settingsService;
    @Mock PeriodLockGuard periodLockGuard;
    @Mock LoanService loanService;
    @Mock LeaveService leaveService;
    @Mock PayItemService payItemService;
    @Mock ContributionTableProvider contributionTables;

    @InjectMocks PayrollService payrollService;

    @Test
    void runPayrollReproducesEngineNetPay() {
        EmployeeEntity emp = new EmployeeEntity(
                "1234-5678-90", "John Christian R. Senoto", EmployeeType.REGULAR, 30000.0, 0.0, true);
        SettingsEntity settingsEntity = new SettingsEntity(26, 8.0, 17.0, 11.0, 5, 5, 0, 0);

        when(employeeRepository.findById("1234-5678-90")).thenReturn(Optional.of(emp));
        when(settingsService.getOrCreateDefault()).thenReturn(settingsEntity);
        when(settingsService.toDomain(settingsEntity))
                .thenReturn(new PayrollSettings(26, 8.0, 17.0, 11.0, 5, 5, 0, 0));
        when(timeRecordRepository
                .findByEmployeeIdAndCutoffPeriodOrderByDayNumber("1234-5678-90", "1st-15th"))
                .thenReturn(fifteenStandardDays());
        when(loanService.activeCutoffTotal("1234-5678-90")).thenReturn(0.0);
        when(leaveService.coveredDaysFor("1234-5678-90", "1st-15th")).thenReturn(0);
        when(payItemService.totalsFor("1234-5678-90")).thenReturn(PayItemTotals.empty());
        when(contributionTables.tablesFor(any())).thenReturn(ContributionTables.HARDCODED);
        when(payrollRepository.save(any(PayslipEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PayslipResponse slip = payrollService.runPayroll(
                new RunPayrollRequest("1234-5678-90", "1st-15th"));

        assertThat(slip.totalHours()).isEqualTo(120.0, within(EPS));
        assertThat(slip.basicPay()).isEqualTo(15000.0, within(EPS));
        assertThat(slip.grossPay()).isEqualTo(15000.0, within(EPS));
        assertThat(slip.sss()).isEqualTo(750.0, within(EPS));
        assertThat(slip.philhealth()).isEqualTo(412.5, within(EPS));
        assertThat(slip.pagibig()).isEqualTo(50.0, within(EPS));
        assertThat(slip.tax()).isEqualTo(505.63, within(EPS));
        assertThat(slip.netPay()).isEqualTo(13281.87, within(EPS));
        assertThat(slip.employeeName()).isEqualTo("John Christian R. Senoto");
        assertThat(slip.employeeTypeLabel()).isEqualTo("Regular");
    }

    @Test
    void loanIsDeductedFromNetPay() {
        EmployeeEntity emp = new EmployeeEntity(
                "1234-5678-90", "John Christian R. Senoto", EmployeeType.REGULAR, 30000.0, 0.0, true);
        SettingsEntity settingsEntity = new SettingsEntity(26, 8.0, 17.0, 11.0, 5, 5, 0, 0);

        when(employeeRepository.findById("1234-5678-90")).thenReturn(Optional.of(emp));
        when(settingsService.getOrCreateDefault()).thenReturn(settingsEntity);
        when(settingsService.toDomain(settingsEntity))
                .thenReturn(new PayrollSettings(26, 8.0, 17.0, 11.0, 5, 5, 0, 0));
        when(timeRecordRepository
                .findByEmployeeIdAndCutoffPeriodOrderByDayNumber(eq("1234-5678-90"), any()))
                .thenReturn(fifteenStandardDays());
        when(loanService.activeCutoffTotal("1234-5678-90")).thenReturn(1000.0);
        when(leaveService.coveredDaysFor("1234-5678-90", "1st-15th")).thenReturn(0);
        when(payItemService.totalsFor("1234-5678-90")).thenReturn(PayItemTotals.empty());
        when(contributionTables.tablesFor(any())).thenReturn(ContributionTables.HARDCODED);
        when(payrollRepository.save(any(PayslipEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PayslipResponse slip = payrollService.runPayroll(
                new RunPayrollRequest("1234-5678-90", "1st-15th"));

        assertThat(slip.loan()).isEqualTo(1000.0, within(EPS));
        assertThat(slip.netPay()).isEqualTo(12281.87, within(EPS));
    }

    private static List<TimeRecordEntity> fifteenStandardDays() {
        List<TimeRecordEntity> records = new ArrayList<>();
        for (int day = 1; day <= 15; day++) {
            records.add(new TimeRecordEntity(
                    "1234-5678-90", "1st-15th", day, 800, 1700, false, HolidayType.NONE));
        }
        return records;
    }
}
