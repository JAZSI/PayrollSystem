package com.com253.payrollsystem.Service;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Model.Submission;
import com.com253.payrollsystem.Repository.AccountRepository;
import com.com253.payrollsystem.Repository.EmployeeRepository;
import com.com253.payrollsystem.Repository.SubmissionRepository;
import java.sql.SQLException;
import java.util.List;

/**
 * Central service layer for all payroll operations.
 * Acts as the single point of contact between the UI and the repositories.
 */
public class PayrollService {
    
    private final AccountRepository accountRepository = new AccountRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final SubmissionRepository submissionRepository = new SubmissionRepository();
    
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
    public void registerEmployee(Employee employee) throws SQLException {
        employeeRepository.save(employee);
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
     * Approves or rejects a submission by ID.
     * On approval, applies leave and loan deductions to the employee record.
     *
     * @param submissionId the submission to update
     * @param status       APPROVED or REJECTED
     */    
    public void updateSubmissionStatus(int submissionId, Submission.Status status) throws SQLException {
        Submission sub = submissionRepository.findByEmployeeId(
                submissionRepository.findAllPending().stream()
                .filter(s -> s.getId() == submissionId)
                .findFirst()
                .map(Submission::getEmployeeId)
                .orElse(null));
        
        submissionRepository.updateStatus(submissionId, status);
        
        if (status == Submission.Status.APPROVED && sub != null) {
            Employee emp = employeeRepository.findById(sub.getEmployeeId());
            if (emp != null) {
                applyDeductions(emp, (int) sub.getLeaveDays(), sub.getLoanDeduction());
            }
        }
    }
}