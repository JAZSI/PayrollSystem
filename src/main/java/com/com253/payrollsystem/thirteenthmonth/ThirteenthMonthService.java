package com.com253.payrollsystem.thirteenthmonth;

import com.com253.payrollsystem.payroll.PayrollRepository;
import com.com253.payrollsystem.payroll.PayrollRunStatus;
import com.com253.payrollsystem.payroll.PayslipEntity;
import com.com253.payrollsystem.shared.Money;
import com.com253.payrollsystem.shared.error.NotFoundException;
import com.com253.payrollsystem.thirteenthmonth.dto.MyThirteenthMonthResponse;
import com.com253.payrollsystem.thirteenthmonth.dto.ThirteenthMonthEntryResponse;
import com.com253.payrollsystem.thirteenthmonth.dto.ThirteenthMonthRunResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 13th-month pay: sum of basic pay earned in a year, divided by 12. */
@Service
@Transactional
public class ThirteenthMonthService {

    private static final double MONTHS_PER_YEAR = 12.0;

    private final ThirteenthMonthRunRepository runs;
    private final ThirteenthMonthEntryRepository entries;
    private final PayrollRepository payslips;

    public ThirteenthMonthService(ThirteenthMonthRunRepository runs,
                                  ThirteenthMonthEntryRepository entries,
                                  PayrollRepository payslips) {
        this.runs = runs;
        this.entries = entries;
        this.payslips = payslips;
    }

    /** Computes a DRAFT run from every payslip created in the given year. */
    public ThirteenthMonthRunResponse createRun(int year) {
        Map<String, double[]> basicByEmployee = new LinkedHashMap<>();
        Map<String, String> nameByEmployee = new LinkedHashMap<>();
        for (PayslipEntity slip : payslips.findAllByOrderByCreatedAtDesc()) {
            if (slip.getCreatedAt() == null || slip.getCreatedAt().getYear() != year) {
                continue;
            }
            basicByEmployee.computeIfAbsent(slip.getEmployeeId(), k -> new double[1])[0] += slip.getBasicPay();
            nameByEmployee.putIfAbsent(slip.getEmployeeId(), slip.getEmployeeName());
        }
        if (basicByEmployee.isEmpty()) {
            throw new IllegalArgumentException("No payslips found for " + year);
        }

        ThirteenthMonthRun run = runs.save(new ThirteenthMonthRun(year));
        double total = 0.0;
        for (Map.Entry<String, double[]> e : basicByEmployee.entrySet()) {
            double totalBasic = Money.round2(e.getValue()[0]);
            double amount = Money.round2(totalBasic / MONTHS_PER_YEAR);
            entries.save(new ThirteenthMonthEntry(
                    run.getId(), e.getKey(), nameByEmployee.get(e.getKey()), totalBasic, amount));
            total += amount;
        }
        run.setEmployeeCount(basicByEmployee.size());
        run.setTotalAmount(Money.round2(total));
        return toDetail(runs.save(run));
    }

    @Transactional(readOnly = true)
    public List<ThirteenthMonthRunResponse> list() {
        return runs.findAllByOrderByCreatedAtDesc().stream()
                .map(r -> toResponse(r, null)).toList();
    }

    @Transactional(readOnly = true)
    public ThirteenthMonthRunResponse get(Long id) {
        return toDetail(getOrThrow(id));
    }

    public ThirteenthMonthRunResponse approve(Long id) {
        ThirteenthMonthRun run = getOrThrow(id);
        if (run.getStatus() != PayrollRunStatus.DRAFT) {
            throw new IllegalStateException("Only a DRAFT run can be approved (current: "
                    + run.getStatus() + ")");
        }
        run.setStatus(PayrollRunStatus.APPROVED);
        return toDetail(runs.save(run));
    }

    public ThirteenthMonthRunResponse lock(Long id) {
        ThirteenthMonthRun run = getOrThrow(id);
        if (run.getStatus() != PayrollRunStatus.APPROVED) {
            throw new IllegalStateException("Only an APPROVED run can be locked (current: "
                    + run.getStatus() + ")");
        }
        run.setStatus(PayrollRunStatus.LOCKED);
        return toDetail(runs.save(run));
    }

    @Transactional(readOnly = true)
    public ThirteenthMonthEntry entryOrThrow(Long entryId) {
        return entries.findById(entryId)
                .orElseThrow(() -> new NotFoundException("13th-month entry not found: " + entryId));
    }

    @Transactional(readOnly = true)
    public int yearOfRun(Long runId) {
        return getOrThrow(runId).getYear();
    }

    /** Self-view: the employee's amounts from locked runs, newest first. */
    @Transactional(readOnly = true)
    public List<MyThirteenthMonthResponse> myEntries(String employeeId) {
        Map<Long, ThirteenthMonthRun> runById = new LinkedHashMap<>();
        runs.findAll().forEach(r -> runById.put(r.getId(), r));
        List<MyThirteenthMonthResponse> result = new ArrayList<>();
        for (ThirteenthMonthEntry entry : entries.findByEmployeeIdOrderByIdDesc(employeeId)) {
            ThirteenthMonthRun run = runById.get(entry.getRunId());
            if (run != null && run.getStatus() == PayrollRunStatus.LOCKED) {
                result.add(new MyThirteenthMonthResponse(run.getYear(), entry.getAmount()));
            }
        }
        return result;
    }

    // ------------------------------- helpers -------------------------------

    private ThirteenthMonthRun getOrThrow(Long id) {
        return runs.findById(id)
                .orElseThrow(() -> new NotFoundException("13th-month run not found: " + id));
    }

    private ThirteenthMonthRunResponse toDetail(ThirteenthMonthRun run) {
        List<ThirteenthMonthEntryResponse> rows = entries.findByRunIdOrderByEmployeeName(run.getId())
                .stream().map(ThirteenthMonthService::toEntry).toList();
        return toResponse(run, rows);
    }

    private static ThirteenthMonthRunResponse toResponse(ThirteenthMonthRun r,
                                                         List<ThirteenthMonthEntryResponse> rows) {
        return new ThirteenthMonthRunResponse(
                r.getId(), r.getYear(), r.getStatus(), r.getEmployeeCount(), r.getTotalAmount(),
                r.getCreatedAt() == null ? null : r.getCreatedAt().toString(), rows);
    }

    private static ThirteenthMonthEntryResponse toEntry(ThirteenthMonthEntry e) {
        return new ThirteenthMonthEntryResponse(
                e.getId(), e.getEmployeeId(), e.getEmployeeName(), e.getTotalBasic(), e.getAmount());
    }
}
