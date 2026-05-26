package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollSettings;
import com.com253.payrollsystem.domain.model.Submission;
import com.com253.payrollsystem.domain.model.TimeRecord;
import com.com253.payrollsystem.infrastructure.config.Database;
import com.com253.payrollsystem.infrastructure.config.TransactionManager;
import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.app.port.SubmissionRepositoryPort;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.EmployeeRepository;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.SubmissionRepository;
import java.sql.SQLException;
import java.util.Optional;

public class SubmissionService {

    private final EmployeeRepositoryPort employeeRepository;
    private final SubmissionRepositoryPort submissionRepository;
    private final DeductionService deductionService;

    public SubmissionService() {
        this(new EmployeeRepository(), new SubmissionRepository(), new DeductionService());
    }

    public SubmissionService(EmployeeRepositoryPort employeeRepository,
                             SubmissionRepositoryPort submissionRepository,
                             DeductionService deductionService) {
        this.employeeRepository = employeeRepository;
        this.submissionRepository = submissionRepository;
        this.deductionService = deductionService;
    }

    public void deleteSubmission(String employeeId) throws SQLException {
        submissionRepository.deleteByEmployeeId(employeeId);
    }

    public boolean submitPayroll(String employeeId, double leaveDays, double otHours, double loanDeduction) throws SQLException {
        try {
            SubmissionValidator.validateSubmissionParams(employeeRepository, employeeId, leaveDays, otHours, loanDeduction);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            throw new SQLException("Validation failure", e);
        }

        Submission submission = new Submission(0, employeeId, leaveDays, otHours, loanDeduction, Submission.Status.PENDING, null);
        return submissionRepository.save(submission);
    }

    public Submission getSubmission(String employeeId) throws SQLException {
        return submissionRepository.findByEmployeeId(employeeId).orElse(null);
    }

    public java.util.List<Submission> getPendingSubmissions() throws SQLException {
        return submissionRepository.findAllPending();
    }

    public void updateSubmissionStatus(int submissionId, Submission.Status status, String cutOffPeriod) throws SQLException {
        java.sql.Connection conn = Database.getConnection();
        try {
            TransactionManager.begin(conn);

            Optional<Submission> subOpt = submissionRepository.findById(submissionId);
            if (subOpt.isEmpty()) {
                throw new IllegalArgumentException("Submission not found: " + submissionId);
            }
            Submission sub = subOpt.get();

            submissionRepository.updateStatus(submissionId, status);

            if (status == Submission.Status.APPROVED) {
                Optional<Employee> empOpt = employeeRepository.findById(sub.employeeId());
                if (empOpt.isPresent()) {
                    deductionService.applyDeductions(empOpt.get(), (int) Math.round(sub.leaveDays()), sub.loanDeduction(), cutOffPeriod);
                }
            }

            TransactionManager.commit(conn);
        } catch (SQLException e) {
            TransactionManager.rollback(conn);
            throw e;
        } finally {
            Database.close(conn);
        }
    }

    public PayrollEntry buildPayrollEntry(String employeeId, String cutOffPeriod) throws SQLException {
        return new PayrollGenerationService().buildPayrollEntry(employeeId, cutOffPeriod);
    }
}
