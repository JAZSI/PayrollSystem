package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AccountRepository extends BaseRepository {

    public void save(EndUser user) throws SQLException {
        String sql = "INSERT INTO accounts (username, password_hash, role, linked_employee_id) "
                   + "VALUES (?, ?, ?, ?)";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole().name());
                stmt.setString(4, user.getLinkedEmployeeId());
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public Optional<EndUser> findByUsername(String username) throws SQLException {
        String sql = "SELECT username, password_hash, role, linked_employee_id "
                   + "FROM accounts WHERE username = ?";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        return Optional.of(new EndUser(
                               rs.getString("username"),
                               rs.getString("password_hash"),
                               EndUser.Role.valueOf(rs.getString("role")),
                               rs.getString("linked_employee_id")
                        ));
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
        return Optional.empty();
    }

    public void deleteByEmployeeId(String employeeId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE linked_employee_id = ?";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }
}