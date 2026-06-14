package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.audit.AuditService;
import com.com253.payrollsystem.leave.LeaveService;
import com.com253.payrollsystem.loan.LoanService;
import com.com253.payrollsystem.payitem.PayItemService;
import com.com253.payrollsystem.payitem.PayItemTotals;
import com.com253.payrollsystem.settings.SettingsService;
import com.com253.payrollsystem.statutory.ContributionTableProvider;
import com.com253.payrollsystem.shared.Money;
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
import com.com253.payrollsystem.payroll.dto.PayrollRunResponse;
import com.com253.payrollsystem.payroll.dto.PayslipResponse;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Batch payroll + run lifecycle (DRAFT/APPROVED/LOCKED). */
@Service
@Transactional
public class PayrollRunService {

    private final EmployeeRepository employeeRepository;
    private final TimeRecordRepository timeRecordRepository;
    private final PayrollRepository payrollRepository;
    private final PayrollRunRepository runRepository;
    private final SettingsService settingsService;
    private final PeriodLockGuard periodLockGuard;
    private final LoanService loanService;
    private final LeaveService leaveService;
    private final PayItemService payItemService;
    private final ContributionTableProvider contributionTables;
    private final AuditService auditService;

    public PayrollRunService(EmployeeRepository employeeRepository,
                             TimeRecordRepository timeRecordRepository,
                             PayrollRepository payrollRepository,
                             PayrollRunRepository runRepository,
                             SettingsService settingsService,
                             PeriodLockGuard periodLockGuard,
                             LoanService loanService,
                             LeaveService leaveService,
                             PayItemService payItemService,
                             ContributionTableProvider contributionTables,
                             AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.timeRecordRepository = timeRecordRepository;
        this.payrollRepository = payrollRepository;
        this.runRepository = runRepository;
        this.settingsService = settingsService;
        this.periodLockGuard = periodLockGuard;
        this.loanService = loanService;
        this.leaveService = leaveService;
        this.payItemService = payItemService;
        this.contributionTables = contributionTables;
        this.auditService = auditService;
    }

    /** Creates a DRAFT run, computing and saving a payslip for every active employee. */
    public PayrollRunResponse createRun(String period) {
        periodLockGuard.ensureNotLocked(period);
        List<EmployeeEntity> active = employeeRepository.findByActiveTrue();
        if (active.isEmpty()) {
            throw new IllegalArgumentException("No active employees to run payroll for");
        }

        PayrollSettings settings = settingsService.toDomain(settingsService.getOrCreateDefault());
        ContributionTables tables = contributionTables.tablesFor(LocalDate.now());
        PayrollRunEntity run = runRepository.save(new PayrollRunEntity(period));

        double totalGross = 0.0;
        double totalNet = 0.0;
        for (EmployeeEntity emp : active) {
            TimeRecord[] records = timeRecordRepository
                    .findByEmployeeIdAndCutoffPeriodOrderByDayNumber(emp.getId(), period)
                    .stream().map(PayrollMapping::toDomainRecord).toArray(TimeRecord[]::new);

            Employee domainEmployee = PayrollMapping.toDomainEmployee(emp);
            double loanTotal = loanService.activeCutoffTotal(emp.getId());
            int coveredLeaveDays = leaveService.coveredDaysFor(emp.getId(), period);
            PayItemTotals items = payItemService.totalsFor(emp.getId());
            PayContext ctx = new PayContext(loanTotal, coveredLeaveDays,
                    items.taxableAllowances(), items.nonTaxableAllowances(), items.otherDeductions());
            PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                    domainEmployee, records, period, settings, ctx, tables);

            PayslipEntity slip = PayrollMapping.toEntity(emp, entry);
            slip.setRunId(run.getId());
            payrollRepository.save(slip);

            totalGross += entry.getGrossPay();
            totalNet += entry.getNetPay();
        }

        run.setEmployeeCount(active.size());
        run.setTotalGross(Money.round2(totalGross));
        run.setTotalNet(Money.round2(totalNet));
        run.setTotalDeductions(Money.round2(totalGross - totalNet));
        runRepository.save(run);

        auditService.record("RUN", "PayrollRun", String.valueOf(run.getId()),
                "Created run for " + period + " (" + active.size() + " employees)");
        return toDetail(run);
    }

    @Transactional(readOnly = true)
    public List<PayrollRunResponse> list() {
        return runRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(r -> toResponse(r, null)).toList();
    }

    @Transactional(readOnly = true)
    public PayrollRunResponse get(Long id) {
        return toDetail(getOrThrow(id));
    }

    public PayrollRunResponse approve(Long id) {
        PayrollRunEntity run = getOrThrow(id);
        if (run.getStatus() != PayrollRunStatus.DRAFT) {
            throw new IllegalStateException("Only a DRAFT run can be approved (current: "
                    + run.getStatus() + ")");
        }
        run.setStatus(PayrollRunStatus.APPROVED);
        PayrollRunResponse approved = toDetail(runRepository.save(run));
        auditService.record("APPROVE", "PayrollRun", String.valueOf(id),
                "Approved run for " + run.getCutoffPeriod());
        return approved;
    }

    public PayrollRunResponse lock(Long id) {
        PayrollRunEntity run = getOrThrow(id);
        if (run.getStatus() != PayrollRunStatus.APPROVED) {
            throw new IllegalStateException("Only an APPROVED run can be locked (current: "
                    + run.getStatus() + ")");
        }
        run.setStatus(PayrollRunStatus.LOCKED);
        PayrollRunEntity locked = runRepository.save(run);

        Map<String, Long> payslipByEmployee = payrollRepository
                .findByRunIdOrderByEmployeeName(locked.getId()).stream()
                .collect(Collectors.toMap(PayslipEntity::getEmployeeId, PayslipEntity::getId, (a, b) -> a));
        loanService.postForRun(locked.getCutoffPeriod(), payslipByEmployee);

        auditService.record("LOCK", "PayrollRun", String.valueOf(id),
                "Locked run for " + locked.getCutoffPeriod());
        return toDetail(locked);
    }


    private PayrollRunEntity getOrThrow(Long id) {
        return runRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payroll run not found: " + id));
    }

    private PayrollRunResponse toDetail(PayrollRunEntity run) {
        List<PayslipResponse> slips = payrollRepository
                .findByRunIdOrderByEmployeeName(run.getId())
                .stream().map(PayrollMapping::toResponse).toList();
        return toResponse(run, slips);
    }

    private static PayrollRunResponse toResponse(PayrollRunEntity r, List<PayslipResponse> slips) {
        return new PayrollRunResponse(
                r.getId(), r.getCutoffPeriod(), r.getStatus(), r.getEmployeeCount(),
                r.getTotalGross(), r.getTotalDeductions(), r.getTotalNet(),
                r.getCreatedAt() == null ? null : r.getCreatedAt().toString(),
                slips);
    }
}
