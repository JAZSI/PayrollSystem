package com.com253.payrollsystem.app.port;

import com.com253.payrollsystem.domain.model.LoanTransaction;
import java.sql.SQLException;
import java.util.List;

public interface LoanTransactionRepositoryPort {
    void save(String employeeId, double amount, String cutOffPeriod) throws SQLException;
    List<LoanTransaction> findByEmployeeId(String employeeId) throws SQLException;
}
