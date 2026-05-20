package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepository {
    
    public void save(EndUser user) throws SQLException {
        String sql = "INSERT INTO accounts (username, password_hash, role, linked_employee_id) "
                   + "VALUES (?, ?, ?, ?)";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getLinkedEmployeeId());
            
            stmt.executeUpdate();
        }
    }
    
    public EndUser findByUsername(String username) throws SQLException {
        String sql = "SELECT username, password_hash, role, linked_employee_id "
                   + "FROM accounts WHERE username = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EndUser(
                           rs.getString("username"),
                           rs.getString("password_hash"),
                           EndUser.Role.valueOf(rs.getString("role")),
                           rs.getString("linked_employee_id")
                    );
                }
            }
        }
        return null;
    }
    public void deleteByEmployeeId(String employeeId) throws SQLException {
        String sql = "DELETE FROM accounts where linked_employee_id = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        }
    }
}