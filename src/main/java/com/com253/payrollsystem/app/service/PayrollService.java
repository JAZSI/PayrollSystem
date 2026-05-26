package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.EndUser;
import com.com253.payrollsystem.domain.model.LeaveTransaction;
import com.com253.payrollsystem.domain.model.LoanTransaction;
import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.Submission;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PayrollService {

    private final AuthService authService = new AuthService();
    private final EmployeeService employeeService = new EmployeeService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final DeductionService deductionService = new DeductionService();
    private final SubmissionService submissionService = new SubmissionService();
    private final PayrollGenerationService payrollGenerationService = new PayrollGenerationService();

    public EndUser authenticate(String username, String password) throws SQLException {
        return authService.authenticate(username, password);
    }

    public void registerEmployee(Employee employee, String username, String password) throws SQLException {
        employeeService.registerEmployee(employee, username, password);
    }

    public Employee findEmployee(String id) throws SQLException {
        return employeeService.findEmployee(id);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeService.getAllEmployees();
    }

    public void deleteEmployee(String id) throws SQLException {
        employeeService.deleteEmployee(id);
    }

    public void deleteSubmission(String employeeId) throws SQLException {
        submissionService.deleteSubmission(employeeId);
    }

    public void updateTimeIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        attendanceService.updateTimeIn(employeeId, date, timeIn);
    }

    public void updateTimeOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        attendanceService.updateTimeOut(employeeId, date, timeOut);
    }

    public void deleteAttendance(String employeeId, LocalDate date) throws SQLException {
        attendanceService.deleteAttendance(employeeId, date);
    }

    public void upsertAttendance(String employeeId, LocalDate date, Double timeIn, Double timeOut) throws SQLException {
        attendanceService.upsertAttendance(employeeId, date, timeIn, timeOut);
    }

    public void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        attendanceService.clockIn(employeeId, date, timeIn);
    }

    public void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        attendanceService.clockOut(employeeId, date, timeOut);
    }

    public List<AttendanceRecord> getAttendanceHistory(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        return attendanceService.getAttendanceHistory(employeeId, from, to);
    }

    public void applyDeductions(Employee employee, int leaveDaysUsed, double loanDeducted, String cutOffPeriod) throws SQLException {
        deductionService.applyDeductions(employee, leaveDaysUsed, loanDeducted, cutOffPeriod);
    }

    public List<LeaveTransaction> getLeaveHistory(String employeeId) throws SQLException {
        return deductionService.getLeaveHistory(employeeId);
    }

    public List<LoanTransaction> getLoanHistory(String employeeId) throws SQLException {
        return deductionService.getLoanHistory(employeeId);
    }

    public boolean submitPayroll(String employeeId, double leaveDays, double otHours, double loanDeduction) throws SQLException {
        return submissionService.submitPayroll(employeeId, leaveDays, otHours, loanDeduction);
    }

    public Submission getSubmission(String employeeId) throws SQLException {
        return submissionService.getSubmission(employeeId);
    }

    public List<Submission> getPendingSubmissions() throws SQLException {
        return submissionService.getPendingSubmissions();
    }

    public PayrollEntry buildPayrollEntry(String employeeId, String cutOffPeriod) throws SQLException {
        return payrollGenerationService.buildPayrollEntry(employeeId, cutOffPeriod);
    }

    public void updateSubmissionStatus(int submissionId, Submission.Status status, String cutOffPeriod) throws SQLException {
        submissionService.updateSubmissionStatus(submissionId, status, cutOffPeriod);
    }
}
