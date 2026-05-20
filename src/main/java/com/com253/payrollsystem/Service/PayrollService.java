package com.com253.payrollsystem.Service;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Model.Submission;
import com.com253.payrollsystem.Repository.AccountRepository;
import com.com253.payrollsystem.Repository.AttendanceRepository;
import com.com253.payrollsystem.Repository.EmployeeRepository;
import com.com253.payrollsystem.Repository.SubmissionRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Central service layer for all payroll operations.
 * Acts as the single point of contact between the UI and the repositories.
 */
public class PayrollService {
    
    private final AccountRepository accountRepository = new AccountRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final SubmissionRepository submissionRepository = new SubmissionRepository();
    private final AttendanceRepository attendanceRepository = new AttendanceRepository();
    
    /**
     * Authenticates a user by username and password.
     * Returns the matching EndUser or null if credentials are invalid.
     *
     * @param username entered username
     * @param password entered password
     * @return authenticated EndUser, or null if login fails
     */
    public EndUser authenticate(String username, String password) throws SQLException {
        EndUser user = accountRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!user.getPasswordHash().equals(password)) {
            return null;
        }
        return user;
    }
    
    /**
     * Registers a new employee and creates their login account.
     *
     * @param employee the employee to register
     * @param username login username for the new account
     * @param password login password for the new account
     */ 
    public void registerEmployee(Employee employee, String username, String password) throws SQLException {
        employeeRepository.save(employee);
        EndUser account = new EndUser(
                username, 
                password, 
                EndUser.Role.EMPLOYEE, 
                employee.getEmployeeId());
        accountRepository.save(account);
    }
    
    public Employee findEmployee(String id) throws SQLException {
        return employeeRepository.findById(id);
    }
    
    public List<Employee> getAllEmployees() throws SQLException {
        return employeeRepository.findAll();
    }
    
    /**
     * Deletes an employee and their linked account.
     *
     * @param id employee identifier to delete
     */
    public void deleteEmployee(String id) throws SQLException {
        accountRepository.deleteByEmployeeId(id);
        employeeRepository.delete(id);
    }
    
    /**
     * Applies leave and loan deductions to the employee record after payroll is processed.
     * Deducts the given leave days from the employee's leave balance and the
     * loan amount from their loan balance, then persists both to the database.
     *
     * @param employee      the employee to update
     * @param leaveDaysUsed number of leave days consumed this cutoff
     * @param loanDeducted  loan amount deducted this cutoff
     */   
    public void applyDeductions(Employee employee, int leaveDaysUsed, double loanDeducted) throws SQLException {
        if (employee.isHasLeave() && leaveDaysUsed > 0) {
            employee.getLeaveBalance().deduct(leaveDaysUsed);
            employeeRepository.updateLeaveBalance(employee.getEmployeeId(), employee.getLeaveBalance());
        }
        
        if (loanDeducted > 0) {
            employee.getLoanBalance().deduct(loanDeducted);
            employeeRepository.updateLoanBalance(employee.getEmployeeId(), employee.getLoanBalance());
        }
    }

    /**
     * Files a payroll submission for the given employee.
     * Returns false if an approved submission already exists.
     *
     * @param employeeId    employee filing the submission
     * @param leaveDays     leave days to apply this cutoff
     * @param otHours       overtime hours filed this cutoff
     * @param loanDeduction loan amount to deduct this cutoff
     * @return true if submitted successfully, false if already approved
     */    
    public boolean submitPayroll(String employeeId, double leaveDays, double otHours, double loanDeduction) throws SQLException {
        Submission submission = new Submission(
                0,
                employeeId,
                leaveDays,
                otHours,
                loanDeduction,
                Submission.Status.PENDING,
                null);
        return submissionRepository.save(submission);
    }
    
    /**
     * Returns the current submission for the given employee, or null if none.
     *
     * @param employeeId employee identifier
     * @return submission, or null
     */    
    public Submission getSubmission(String employeeId) throws SQLException {
        return submissionRepository.findByEmployeeId(employeeId);
    }
    
    /**
     * Returns all submissions currently awaiting admin approval.
     *
     * @return list of pending submissions
     */    
    public List<Submission> getPendingSubmissions() throws SQLException {
        return submissionRepository.findAllPending();
    }
    
    /**
     * Builds a payroll entry for an employee using their approved submission.
     * Returns null if no approved submission exists for this employee.
     *
     * @param employeeId    employee to compute payroll for
     * @param cutOffPeriod  current cutoff period label
     * @return payroll entry, or null if submission not approved
     */    
    public PayrollEntry buildPayrollEntry(String employeeId, String cutOffPeriod) throws SQLException {

        Submission sub = submissionRepository.findByEmployeeId(employeeId);
        if (sub == null || sub.getStatus() != Submission.Status.APPROVED) {
            return null;
        }

        Employee emp = employeeRepository.findById(employeeId);
        if (emp == null) return null;

        PayrollSettings settings = new PayrollSettings(26, 8.0, 17.0, 11.0);

        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.of(today.getYear(), today.getMonthValue());

        LocalDate from = cutOffPeriod.equals("1st-15th") ? ym.atDay(1) : ym.atDay(16);
        LocalDate to = cutOffPeriod.equals("1st-15th") ? ym.atDay(15) : ym.atEndOfMonth();

        List<TimeRecord> records = buildTimeRecords(emp.getEmployeeId(), from, to);
        TimeRecord[] recordArray = records.toArray(new TimeRecord[0]);

        return com.com253.payrollsystem.Service.PayrollCalculator.buildPayrollEntry(
                emp, recordArray, cutOffPeriod, sub.getLoanDeduction(), settings);
    }
    
    /**
     * Builds a list of time records for every day in the given date range
     * by matching attendance data with the corresponding dates.
     *
     * @param employeeId employee identifier
     * @param from      start date, inclusive
     * @param to        end date, inclusive
     * @return list of time records for each day in the range
     */    
    private List<TimeRecord> buildTimeRecords(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        
        List<AttendanceRecord> attendance = attendanceRepository.getAttendance(employeeId, from, to);
        List<TimeRecord> records = new ArrayList<>();
        LocalDate current = from;
        
        while (!current.isAfter(to)) {
            int dayNum = current.getDayOfMonth();
            AttendanceRecord rec = findRecord(attendance, current);
            
            if (rec == null || rec.getTimeIn() == null) {
                records.add(new TimeRecord(dayNum, 0, 0, true, TimeRecord.HOLIDAY_NONE));
            } else {
                int timeIn = toHHMM(rec.getTimeIn());
                int timeOut = (rec.getTimeOut() != null) ? toHHMM(rec.getTimeOut()) : 1700;
                records.add(new TimeRecord(dayNum, timeIn, timeOut, false, TimeRecord.HOLIDAY_NONE));
            }
            
            current = current.plusDays(1);
        }
        return records;
    }
    
    /**
     * Converts a decimal hour value to HHMM integer format.
     * Example: 8.5 becomes 850, 17.75 becomes 1745.
     *
     * @param time decimal hours value
     * @return time as integer in HHMM format
     */
    private int toHHMM(double time) {
        int hours = (int) time;
        int minutes = (int) ((time - hours) * 60);
        return hours * 100 + minutes;
    }
    
    /**
     * Searches for an attendance record matching the given date.
     * Records are ordered by date, so search stops when date passes.
     *
     * @param attendance list of attendance records to search
     * @param date       target date to find
     * @return matching attendance record, or null if not found
     */
    private AttendanceRecord findRecord(List<AttendanceRecord> attendance, LocalDate date) {
        for (AttendanceRecord rec : attendance) {
            if (rec.getRecordDate().equals(date)) {
                return rec;
            }
            if (rec.getRecordDate().isAfter(date)) {
                break; // list is ordered by date
            }
        }
        return null;
    }
    
    /**
     * Approves or rejects a submission by ID.
     * On approval, applies leave and loan deductions to the employee record.
     *
     * @param submissionId the submission to update
     * @param status       APPROVED or REJECTED
     */    
    public void updateSubmissionStatus(int submissionId, Submission.Status status) throws SQLException {
        Submission sub = submissionRepository.findById(submissionId);
        if (sub == null) {
            throw new IllegalArgumentException("Submission not found: " + submissionId);
        }
        
        submissionRepository.updateStatus(submissionId, status);
        
        if (status == Submission.Status.APPROVED) {
            Employee emp = employeeRepository.findById(sub.getEmployeeId());
            if (emp != null) {
                applyDeductions(emp, (int) sub.getLeaveDays(), sub.getLoanDeduction());
            }
        }
    }
}