package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LoanBalance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository extends BaseRepository {

    public void save(Employee employee) throws SQLException {
        Double rate = (employee.getEmployeeType() == EmployeeType.PARTTIMER)
                ? employee.getHourlyRate()
                : employee.getMonthlyRate();

        String sql = "INSERT INTO employees (id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employee.getEmployeeId());
                stmt.setString(2, employee.getName());
                stmt.setString(3, employee.getTypeName());
                stmt.setDouble(4, rate);
                stmt.setInt(5, employee.getLeaveBalance().getSick());
                stmt.setInt(6, employee.getLeaveBalance().getVacation());
                stmt.setInt(7, employee.getLeaveBalance().getEmergency());
                stmt.setDouble(8, employee.getLoanBalance().getBalance());
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public Optional<Employee> findById(String id) throws SQLException {
        String sql = "SELECT id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance FROM employees WHERE id = ?";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) return Optional.of(buildEmployee(rs));
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

    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance FROM employees";
        List<Employee> employees = new ArrayList<>();

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                ResultSet rs = stmt.executeQuery();
                try {
                    while (rs.next()) employees.add(buildEmployee(rs));
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
        return employees;
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, id);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public void updateLeaveBalance(String employeeId, LeaveBalance leaveBalance) throws SQLException {
        String sql = "UPDATE employees SET sick_leave = ?, vacation_leave = ?, emergency_leave = ? WHERE id = ?";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setInt(1, leaveBalance.getSick());
                stmt.setInt(2, leaveBalance.getVacation());
                stmt.setInt(3, leaveBalance.getEmergency());
                stmt.setString(4, employeeId);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public void updateLoanBalance(String employeeId, LoanBalance loanBalance) throws SQLException {
        String sql = "UPDATE employees SET loan_balance = ? WHERE id = ?";

        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setDouble(1, loanBalance.getBalance());
                stmt.setString(2, employeeId);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    private Employee buildEmployee(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String typeStr = rs.getString("type");
        double rate = rs.getDouble("rate");
        LeaveBalance leave = new LeaveBalance(
                rs.getInt("sick_leave"),
                rs.getInt("vacation_leave"),
                rs.getInt("emergency_leave"));
        LoanBalance loan = new LoanBalance(rs.getDouble("loan_balance"));

        EmployeeType type = EmployeeType.valueOf(typeStr.toUpperCase());
        boolean hasLeave = (type != EmployeeType.PARTTIMER && type != EmployeeType.CONTRACTUAL);

        double monthlyRate = (type == EmployeeType.PARTTIMER) ? 0.0 : rate;
        double hourlyRate = (type == EmployeeType.PARTTIMER) ? rate : 0.0;

        return new Employee(id, name, type, monthlyRate, hourlyRate, hasLeave, leave, loan);
    }
}