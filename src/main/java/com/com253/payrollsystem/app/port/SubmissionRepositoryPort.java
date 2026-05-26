package com.com253.payrollsystem.app.port;

import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollReportEntry;
import com.com253.payrollsystem.domain.model.Submission;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepositoryPort {
    boolean save(Submission submission) throws SQLException;
    Optional<Submission> findByEmployeeId(String employeeId) throws SQLException;
    Optional<Submission> findById(int id) throws SQLException;
    List<Submission> findAllPending() throws SQLException;
    void updateStatus(int submissionId, Submission.Status status) throws SQLException;
    void deleteByEmployeeId(String employeeId) throws SQLException;
    void savePayrollEntry(PayrollEntry entry) throws SQLException;
    List<PayrollReportEntry> findByPeriod(String cutOffPeriod) throws SQLException;
    List<PayrollReportEntry> findAllReports() throws SQLException;
}
