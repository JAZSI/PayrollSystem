package com.com253.payrollsystem.app.port;

import com.com253.payrollsystem.domain.model.AttendanceRecord;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryPort {
    void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException;
    void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException;
    java.util.List<AttendanceRecord> getAttendance(String employeeId, LocalDate from, LocalDate to) throws SQLException;
    void updateTimeIn(String employeeId, LocalDate date, double timeIn) throws SQLException;
    void updateTimeOut(String employeeId, LocalDate date, double timeOut) throws SQLException;
    void deleteByEmployeeAndDate(String employeeId, LocalDate date) throws SQLException;
    void upsert(String employeeId, LocalDate date, Double timeIn, Double timeOut) throws SQLException;
}
