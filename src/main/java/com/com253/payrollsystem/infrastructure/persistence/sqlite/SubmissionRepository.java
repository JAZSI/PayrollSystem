package com.com253.payrollsystem.infrastructure.persistence.sqlite;

import com.com253.payrollsystem.app.port.SubmissionRepositoryPort;

import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollReportEntry;
import com.com253.payrollsystem.domain.model.Submission;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;

public class SubmissionRepository implements SubmissionRepositoryPort {

    private static final String REPORT_SELECT = "SELECT pe.id, pe.employee_id, e.name AS employee_name, pe.cutoff_period, pe.total_hours, pe.overtime_hours, pe.undertime_hours, pe.absent_days, pe.basic_pay, pe.overtime_pay, pe.holiday_pay, pe.night_shift_differential, pe.gross_pay, pe.sss_deduction, pe.philhealth_deduction, pe.pagibig_deduction, pe.tax_deduction, pe.loan_deduction, pe.undertime_penalty, pe.absence_penalty, pe.net_pay, pe.created_at "
                   + "FROM payroll_entries pe JOIN employees e ON pe.employee_id = e.id ";
    public boolean save(Submission submission) throws SQLException {
        try {
            Optional<Submission> existing = findByEmployeeId(submission.employeeId());
            if (existing.isPresent() && existing.get().status() == Submission.Status.APPROVED) {
                return false;
            }

            if (existing.isPresent()) {
                String sql = "UPDATE submissions SET leave_days = ?, ot_hours = ?, loan_deduction = ?, status = 'PENDING', submitted_at = CURRENT_TIMESTAMP WHERE employee_id = ?";
                JdbcTemplate.update(sql, stmt -> {
                    stmt.setDouble(1, submission.leaveDays());
                    stmt.setDouble(2, submission.otHours());
                    stmt.setDouble(3, submission.loanDeduction());
                    stmt.setString(4, submission.employeeId());
                });
            } else {
                String sql = "INSERT INTO submissions (employee_id, leave_days, ot_hours, loan_deduction) VALUES (?, ?, ?, ?)";
                JdbcTemplate.update(sql, stmt -> {
                    stmt.setString(1, submission.employeeId());
                    stmt.setDouble(2, submission.leaveDays());
                    stmt.setDouble(3, submission.otHours());
                    stmt.setDouble(4, submission.loanDeduction());
                });
            }
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save submission for employee: " + submission.employeeId(), e);
        }
    }

    public Optional<Submission> findByEmployeeId(String employeeId) throws SQLException {
        try {
            String sql = "SELECT id, employee_id, leave_days, ot_hours, loan_deduction, status, submitted_at FROM submissions WHERE employee_id = ?";
            return JdbcTemplate.queryForObject(sql, stmt -> stmt.setString(1, employeeId), this::buildSubmission);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find submission by employee id: " + employeeId, e);
        }
    }

    public Optional<Submission> findById(int id) throws SQLException {
        try {
            String sql = "SELECT id, employee_id, leave_days, ot_hours, loan_deduction, status, submitted_at FROM submissions WHERE id = ?";
            return JdbcTemplate.queryForObject(sql, stmt -> stmt.setInt(1, id), this::buildSubmission);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find submission by id: " + id, e);
        }
    }

    public List<Submission> findAllPending() throws SQLException {
        try {
            String sql = "SELECT id, employee_id, leave_days, ot_hours, loan_deduction, status, submitted_at FROM submissions WHERE status = 'PENDING'";
            return JdbcTemplate.query(sql, this::buildSubmission);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find pending submissions", e);
        }
    }

    public void updateStatus(int submissionId, Submission.Status status) throws SQLException {
        try {
            String sql = "UPDATE submissions SET status = ? WHERE id = ?";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, status.name());
                stmt.setInt(2, submissionId);
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update submission status: " + submissionId, e);
        }
    }

    public void deleteByEmployeeId(String employeeId) throws SQLException {
        try {
            String sql = "DELETE FROM submissions WHERE employee_id = ?";
            JdbcTemplate.update(sql, stmt -> stmt.setString(1, employeeId));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete submission for employee: " + employeeId, e);
        }
    }

    public void savePayrollEntry(PayrollEntry entry) throws SQLException {
        try {
            String sql = "INSERT OR IGNORE INTO payroll_entries "
                       + "(employee_id, cutoff_period, total_hours, overtime_hours, undertime_hours, absent_days, "
                       + "basic_pay, overtime_pay, holiday_pay, night_shift_differential, gross_pay, sss_deduction, "
                       + "philhealth_deduction, pagibig_deduction, tax_deduction, loan_deduction, undertime_penalty, "
                       + "absence_penalty, net_pay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, entry.employee().getEmployeeId());
                stmt.setString(2, entry.cutOffPeriod());
                stmt.setDouble(3, entry.totalHoursWorked());
                stmt.setDouble(4, entry.overtimeHours());
                stmt.setDouble(5, entry.undertimeHours());
                stmt.setInt(6, entry.absentDays());
                stmt.setDouble(7, entry.basicPay());
                stmt.setDouble(8, entry.overtimePay());
                stmt.setDouble(9, entry.holidayPay());
                stmt.setDouble(10, entry.nightShiftDifferential());
                stmt.setDouble(11, entry.grossPay());
                stmt.setDouble(12, entry.sssDeduction());
                stmt.setDouble(13, entry.philhealthDeduction());
                stmt.setDouble(14, entry.pagibigDeduction());
                stmt.setDouble(15, entry.taxDeduction());
                stmt.setDouble(16, entry.loanDeduction());
                stmt.setDouble(17, entry.undertimePenalty());
                stmt.setDouble(18, entry.absencePenalty());
                stmt.setDouble(19, entry.netPay());
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save payroll entry for employee: " + entry.employee().getEmployeeId(), e);
        }
    }

    public List<PayrollReportEntry> findByPeriod(String cutOffPeriod) throws SQLException {
        try {
            String sql = REPORT_SELECT + "WHERE pe.cutoff_period = ? ORDER BY e.name ASC";
            return JdbcTemplate.query(sql, stmt -> stmt.setString(1, cutOffPeriod), this::buildReportEntry);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query payroll reports for period: " + cutOffPeriod, e);
        }
    }

    public List<PayrollReportEntry> findAllReports() throws SQLException {
        try {
            String sql = REPORT_SELECT + "ORDER BY pe.cutoff_period, e.name ASC";
            return JdbcTemplate.query(sql, this::buildReportEntry);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query all payroll reports", e);
        }
    }

    private Submission buildSubmission(ResultSet rs) throws SQLException {
        return new Submission(
                rs.getInt("id"),
                rs.getString("employee_id"),
                rs.getDouble("leave_days"),
                rs.getDouble("ot_hours"),
                rs.getDouble("loan_deduction"),
                Submission.Status.valueOf(rs.getString("status")),
                rs.getString("submitted_at"));
    }

    private PayrollReportEntry buildReportEntry(ResultSet rs) throws SQLException {
        return new PayrollReportEntry(
                rs.getInt("id"),
                rs.getString("employee_id"),
                rs.getString("employee_name"),
                rs.getString("cutoff_period"),
                rs.getDouble("total_hours"),
                rs.getDouble("overtime_hours"),
                rs.getDouble("undertime_hours"),
                rs.getInt("absent_days"),
                rs.getDouble("basic_pay"),
                rs.getDouble("overtime_pay"),
                rs.getDouble("holiday_pay"),
                rs.getDouble("night_shift_differential"),
                rs.getDouble("gross_pay"),
                rs.getDouble("sss_deduction"),
                rs.getDouble("philhealth_deduction"),
                rs.getDouble("pagibig_deduction"),
                rs.getDouble("tax_deduction"),
                rs.getDouble("loan_deduction"),
                rs.getDouble("undertime_penalty"),
                rs.getDouble("absence_penalty"),
                rs.getDouble("net_pay"),
                rs.getString("created_at"));
    }
}
