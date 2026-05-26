package com.com253.payrollsystem.app.port;

import com.com253.payrollsystem.domain.model.LeaveTransaction;
import com.com253.payrollsystem.domain.model.LeaveTransaction.LeaveType;
import java.sql.SQLException;
import java.util.List;

public interface LeaveTransactionRepositoryPort {
    void save(String employeeId, LeaveType leaveType, int days, String cutOffPeriod) throws SQLException;
    List<LeaveTransaction> findByEmployeeId(String employeeId) throws SQLException;
}
