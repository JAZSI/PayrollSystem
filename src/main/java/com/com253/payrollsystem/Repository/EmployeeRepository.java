package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LeaveTransaction;
import com.com253.payrollsystem.Model.LeaveTransaction.LeaveType;
import com.com253.payrollsystem.Model.LoanBalance;
import com.com253.payrollsystem.Model.LoanTransaction;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    public void save(Employee employee) throws SQLException {
        Double rate = (employee.getEmployeeType() == EmployeeType.PARTTIMER)
                ? employee.getHourlyRate()
                : employee.getMonthlyRate();

        String sql = "INSERT INTO employees (id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmployeeId());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getTypeName());
            stmt.setDouble(4, rate);
            stmt.setInt(5, employee.getLeaveBalance().getSick());
            stmt.setInt(6, employee.getLeaveBalance().getVacation());
            stmt.setInt(7, employee.getLeaveBalance().getEmergency());
            stmt.setDouble(8, employee.getLoanBalance().getBalance());

            stmt.executeUpdate();
        }
    }

    public Employee findById(String id) throws SQLException {
        String sql = "SELECT id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance FROM employees WHERE id = ?";

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
        String sql = "SELECT id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance FROM employees";
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

    /**
     * Updates the leave balance for the given employee in the database.
     *
     * @param employeeId    employee identifier
     * @param leaveBalance  updated leave balance to persist
     */
    public void updateLeaveBalance(String employeeId, LeaveBalance leaveBalance) throws SQLException {
        String sql = "Update employees SET sick_leave = ?, vacation_leave = ?, "
                   + "emergency_leave = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, leaveBalance.getSick());
            stmt.setInt(2, leaveBalance.getVacation());
            stmt.setInt(3, leaveBalance.getEmergency());
            stmt.setString(4, employeeId);
            stmt.executeUpdate();
        }
    }
    /**
     * Updates the loan balance for the given employee in the database.
     *
     * @param employeeId  employee identifier
     * @param loanBalance updated loan balance to persist
     */
    public void updateLoanBalance(String employeeId, LoanBalance loanBalance) throws SQLException {
        String sql = "UPDATE employees SET loan_balance = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setDouble(1, loanBalance.getBalance());
            stmt.setString(2, employeeId);
            stmt.executeUpdate();
        }
    }

    public void saveLeaveTransaction(String employeeId, LeaveType leaveType, int days, String cutOffPeriod)
            throws SQLException {
        String sql = "INSERT INTO leave_transactions (employee_id, leave_type, days, cutoff_period) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setString(2, leaveType.name());
            stmt.setInt(3, days);
            stmt.setString(4, cutOffPeriod);
            stmt.executeUpdate();
        }
    }

    public List<LeaveTransaction> findLeaveHistory(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, leave_type, days, cutoff_period, created_at "
                   + "FROM leave_transactions WHERE employee_id = ? ORDER BY created_at DESC";
        List<LeaveTransaction> list = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new LeaveTransaction(
                        rs.getInt("id"),
                        rs.getString("employee_id"),
                        LeaveType.valueOf(rs.getString("leave_type")),
                        rs.getInt("days"),
                        rs.getString("cutoff_period"),
                        rs.getString("created_at")));
                }
            }
        }
        return list;
    }

    public void saveLoanTransaction(String employeeId, double amount, String cutOffPeriod) throws SQLException {
        String sql = "INSERT INTO loan_transactions (employee_id, amount, cutoff_period) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDouble(2, amount);
            stmt.setString(3, cutOffPeriod);
            stmt.executeUpdate();
        }
    }

    public List<LoanTransaction> findLoanHistory(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, amount, cutoff_period, created_at "
                   + "FROM loan_transactions WHERE employee_id = ? ORDER BY created_at DESC";
        List<LoanTransaction> list = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new LoanTransaction(
                        rs.getInt("id"),
                        rs.getString("employee_id"),
                        rs.getDouble("amount"),
                        rs.getString("cutoff_period"),
                        rs.getString("created_at")));
                }
            }
        }
        return list;
    }
}