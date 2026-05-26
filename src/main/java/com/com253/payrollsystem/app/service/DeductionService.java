package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.LeaveBalance;
import com.com253.payrollsystem.domain.model.LeaveTransaction;
import com.com253.payrollsystem.domain.model.LeaveTransaction.LeaveType;
import com.com253.payrollsystem.domain.model.LoanBalance;
import com.com253.payrollsystem.domain.model.LoanTransaction;
import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.app.port.LeaveTransactionRepositoryPort;
import com.com253.payrollsystem.app.port.LoanTransactionRepositoryPort;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.EmployeeRepository;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.LeaveTransactionRepository;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.LoanTransactionRepository;
import java.sql.SQLException;
import java.util.List;

public class DeductionService {

    private final EmployeeRepositoryPort employeeRepository;
    private final LeaveTransactionRepositoryPort leaveTransactionRepository;
    private final LoanTransactionRepositoryPort loanTransactionRepository;

    public DeductionService() {
        this(new EmployeeRepository(), new LeaveTransactionRepository(), new LoanTransactionRepository());
    }

    public DeductionService(EmployeeRepositoryPort employeeRepository,
                            LeaveTransactionRepositoryPort leaveTransactionRepository,
                            LoanTransactionRepositoryPort loanTransactionRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveTransactionRepository = leaveTransactionRepository;
        this.loanTransactionRepository = loanTransactionRepository;
    }

    public void applyDeductions(Employee employee, int leaveDaysUsed,
                                double loanDeducted, String cutOffPeriod) throws SQLException {
        if (employee.isHasLeave() && leaveDaysUsed > 0) {
            LeaveBalance.DeductionResult result = employee.getLeaveBalance().deduct(leaveDaysUsed);
            LeaveBalance updatedLeave = employee.getLeaveBalance().apply(result);
            employee.setLeaveBalance(updatedLeave);
            employeeRepository.updateLeaveBalance(employee.getEmployeeId(), updatedLeave);

            if (result.getSick() > 0) {
                leaveTransactionRepository.save(employee.getEmployeeId(), LeaveType.SICK, result.getSick(), cutOffPeriod);
            }
            if (result.getVacation() > 0) {
                leaveTransactionRepository.save(employee.getEmployeeId(), LeaveType.VACATION, result.getVacation(), cutOffPeriod);
            }
            if (result.getEmergency() > 0) {
                leaveTransactionRepository.save(employee.getEmployeeId(), LeaveType.EMERGENCY, result.getEmergency(), cutOffPeriod);
            }
        }

        if (loanDeducted > 0) {
            double actualDeducted = employee.getLoanBalance().deduct(loanDeducted);
            if (actualDeducted > 0) {
                LoanBalance updatedLoan = employee.getLoanBalance().apply(actualDeducted);
                employee.setLoanBalance(updatedLoan);
                employeeRepository.updateLoanBalance(employee.getEmployeeId(), updatedLoan);
                loanTransactionRepository.save(employee.getEmployeeId(), actualDeducted, cutOffPeriod);
            }
        }
    }

    public List<LeaveTransaction> getLeaveHistory(String employeeId) throws SQLException {
        return leaveTransactionRepository.findByEmployeeId(employeeId);
    }

    public List<LoanTransaction> getLoanHistory(String employeeId) throws SQLException {
        return loanTransactionRepository.findByEmployeeId(employeeId);
    }
}
