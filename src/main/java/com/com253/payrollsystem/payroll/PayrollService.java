package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.leave.LeaveService;
import com.com253.payrollsystem.loan.LoanService;
import com.com253.payrollsystem.payitem.PayItemService;
import com.com253.payrollsystem.payitem.PayItemTotals;
import com.com253.payrollsystem.settings.SettingsService;
import com.com253.payrollsystem.statutory.ContributionTableProvider;
import com.com253.payrollsystem.shared.mapping.PayrollMapping;

import com.com253.payrollsystem.shared.domain.Employee;
import com.com253.payrollsystem.shared.domain.PayContext;
import com.com253.payrollsystem.shared.domain.PayrollEntry;
import com.com253.payrollsystem.shared.domain.PayrollSettings;
import com.com253.payrollsystem.shared.domain.TimeRecord;
import com.com253.payrollsystem.shared.domain.tax.ContributionTables;

import java.time.LocalDate;
import com.com253.payrollsystem.employee.EmployeeEntity;
import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.attendance.TimeRecordRepository;
import com.com253.payrollsystem.shared.domain.PayrollCalculator;
import com.com253.payrollsystem.payroll.dto.PayslipResponse;
import com.com253.payrollsystem.payroll.dto.RunPayrollRequest;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Runs payroll for one employee and saves the payslip. */
@Service
@Transactional
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final TimeRecordRepository timeRecordRepository;
    private final PayrollRepository payrollRepository;
    private final SettingsService settingsService;
    private final PeriodLockGuard periodLockGuard;
    private final LoanService loanService;
    private final LeaveService leaveService;
    private final PayItemService payItemService;
    private final ContributionTableProvider contributionTables;

    public PayrollService(EmployeeRepository employeeRepository,
                          TimeRecordRepository timeRecordRepository,
                          PayrollRepository payrollRepository,
                          SettingsService settingsService,
                          PeriodLockGuard periodLockGuard,
                          LoanService loanService,
                          LeaveService leaveService,
                          PayItemService payItemService,
                          ContributionTableProvider contributionTables) {
        this.employeeRepository = employeeRepository;
        this.timeRecordRepository = timeRecordRepository;
        this.payrollRepository = payrollRepository;
        this.settingsService = settingsService;
        this.periodLockGuard = periodLockGuard;
        this.loanService = loanService;
        this.leaveService = leaveService;
        this.payItemService = payItemService;
        this.contributionTables = contributionTables;
    }

    /** Computes the payslip for an employee + cut-off using current settings + attendance, then saves it. */
    public PayslipResponse runPayroll(RunPayrollRequest req) {
        periodLockGuard.ensureNotLocked(req.period());
        EmployeeEntity emp = employeeRepository.findById(req.employeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found: " + req.employeeId()));

        PayrollSettings settings = settingsService.toDomain(settingsService.getOrCreateDefault());

        TimeRecord[] records = timeRecordRepository
                .findByEmployeeIdAndCutoffPeriodOrderByDayNumber(req.employeeId(), req.period())
                .stream()
                .map(PayrollMapping::toDomainRecord)
                .toArray(TimeRecord[]::new);

        Employee domainEmployee = PayrollMapping.toDomainEmployee(emp);
        double loanTotal = loanService.activeCutoffTotal(req.employeeId());
        int coveredLeaveDays = leaveService.coveredDaysFor(req.employeeId(), req.period());
        PayItemTotals items = payItemService.totalsFor(req.employeeId());
        PayContext ctx = new PayContext(loanTotal, coveredLeaveDays,
                items.taxableAllowances(), items.nonTaxableAllowances(), items.otherDeductions());
        ContributionTables tables = contributionTables.tablesFor(LocalDate.now());

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                domainEmployee, records, req.period(), settings, ctx, tables);

        PayslipEntity saved = payrollRepository.save(PayrollMapping.toEntity(emp, entry));
        return PayrollMapping.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponse> history(String employeeId) {
        List<PayslipEntity> slips = (employeeId == null || employeeId.isBlank())
                ? payrollRepository.findAllByOrderByCreatedAtDesc()
                : payrollRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
        return slips.stream().map(PayrollMapping::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PayslipResponse get(Long id) {
        return payrollRepository.findById(id)
                .map(PayrollMapping::toResponse)
                .orElseThrow(() -> new NotFoundException("Payslip not found: " + id));
    }
}
