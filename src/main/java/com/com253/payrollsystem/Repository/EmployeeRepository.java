package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.EmployeeTypes.Contractual;
import com.com253.payrollsystem.Model.EmployeeTypes.PartTimer;
import com.com253.payrollsystem.Model.EmployeeTypes.Probationary;
import com.com253.payrollsystem.Model.EmployeeTypes.Regular;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    
    public void save(Employee employee) throws SQLException {
        Double rate = (employee.getEmployeeType().equals("PartTimer")) 
                ? employee.getHourlyRate() 
                : employee.getMonthlyRate();
        
        String sql = "INSERT INTO employees (id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {   
            
            stmt.setString(1, employee.getEmployeeId());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getEmployeeType());
            stmt.setDouble(4, rate);
            stmt.setInt(5, employee.getSickLeave());
            stmt.setInt(6, employee.getVacationLeave());
            stmt.setInt(7, employee.getEmergencyLeave());
            stmt.setDouble(8, employee.getLoanBalance());
            
            stmt.executeUpdate();
        }
    }
    
    public Employee findById(String id) throws SQLException {
        String sql = "SELECT id, name, type, rate FROM employees WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildEmployee(rs);
                }
            }
        }
        return null;
    }
    
    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT id, name, type, rate FROM employees";
        List<Employee> employees = new ArrayList<>();
        
        try (Connection connection = Database.getConnection(); 
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(buildEmployee(rs));
            }
        }
        return employees;
    }
    
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Employee buildEmployee(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String type = rs.getString("type");
        double rate = rs.getDouble("rate");
        int sickLeave = rs.getInt("sick_leave");
        int vacationLeave = rs.getInt("vacation_leave");
        int emergencyLeave = rs.getInt("emergency_leave");
        double loanBalance = rs.getDouble("loan_balance");
        
        switch (type){
            case "Regular": return new Regular(id, name, rate, sickLeave, vacationLeave, emergencyLeave, loanBalance);
            case "Probationary": return new Probationary(id, name, rate, sickLeave, vacationLeave, emergencyLeave, loanBalance);
            case "Contractual": return new Contractual(id, name, rate, sickLeave, vacationLeave, emergencyLeave, loanBalance);
            case "PartTimer": return new PartTimer(id, name, rate, sickLeave, vacationLeave, emergencyLeave, loanBalance);
            default: throw new IllegalStateException("Unknown employee type in DB: " + type);
        }
    }
}