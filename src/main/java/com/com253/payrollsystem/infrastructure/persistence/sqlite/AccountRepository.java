package com.com253.payrollsystem.infrastructure.persistence.sqlite;

import com.com253.payrollsystem.app.port.AccountRepositoryPort;
import com.com253.payrollsystem.domain.model.EndUser;
import java.sql.SQLException;
import java.util.Optional;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;

public class AccountRepository implements AccountRepositoryPort {

    public void save(EndUser user) throws SQLException {
        try {
            String sql = "INSERT INTO accounts (username, password_hash, role, linked_employee_id) "
                       + "VALUES (?, ?, ?, ?)";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole().name());
                stmt.setString(4, user.getLinkedEmployeeId());
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save account: " + user.getUsername(), e);
        }
    }

    public Optional<EndUser> findByUsername(String username) throws SQLException {
        String sql = "SELECT username, password_hash, role, linked_employee_id "
                   + "FROM accounts WHERE username = ?";
        try {
            return JdbcTemplate.queryForObject(sql, stmt -> stmt.setString(1, username), rs -> new EndUser(
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    EndUser.Role.valueOf(rs.getString("role")),
                    rs.getString("linked_employee_id")
            ));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find account by username: " + username, e);
        }
    }

    public void deleteByEmployeeId(String employeeId) throws SQLException {
        try {
            String sql = "DELETE FROM accounts WHERE linked_employee_id = ?";
            JdbcTemplate.update(sql, stmt -> stmt.setString(1, employeeId));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete account for employee: " + employeeId, e);
        }
    }
}