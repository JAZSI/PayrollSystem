package com.com253.payrollsystem.dashboard;

import com.com253.payrollsystem.payroll.PayrollRunStatus;
import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.payroll.PayrollRepository;
import com.com253.payrollsystem.payroll.PayrollRunRepository;
import com.com253.payrollsystem.dashboard.dto.DashboardResponse;
import com.com253.payrollsystem.payroll.dto.PayrollRunResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Builds the dashboard KPI summary. */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final PayrollRunRepository runRepository;

    public DashboardService(EmployeeRepository employeeRepository,
                            PayrollRepository payrollRepository,
                            PayrollRunRepository runRepository) {
        this.employeeRepository = employeeRepository;
        this.payrollRepository = payrollRepository;
        this.runRepository = runRepository;
    }

    public DashboardResponse summary() {
        PayrollRunResponse latest = runRepository.findFirstByOrderByCreatedAtDesc()
                .map(r -> new PayrollRunResponse(
                        r.getId(), r.getCutoffPeriod(), r.getStatus(), r.getEmployeeCount(),
                        r.getTotalGross(), r.getTotalDeductions(), r.getTotalNet(),
                        r.getCreatedAt() == null ? null : r.getCreatedAt().toString(), null))
                .orElse(null);

        return new DashboardResponse(
                employeeRepository.countByActiveTrue(),
                employeeRepository.count(),
                payrollRepository.count(),
                runRepository.count(),
                runRepository.countByStatus(PayrollRunStatus.DRAFT),
                runRepository.countByStatus(PayrollRunStatus.APPROVED),
                runRepository.countByStatus(PayrollRunStatus.LOCKED),
                latest);
    }
}
