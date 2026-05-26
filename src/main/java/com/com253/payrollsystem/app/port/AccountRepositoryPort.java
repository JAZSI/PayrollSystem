package com.com253.payrollsystem.app.port;

import com.com253.payrollsystem.domain.model.EndUser;
import java.sql.SQLException;
import java.util.Optional;

public interface AccountRepositoryPort {
    void save(EndUser user) throws SQLException;
    Optional<EndUser> findByUsername(String username) throws SQLException;
    void deleteByEmployeeId(String employeeId) throws SQLException;
}
