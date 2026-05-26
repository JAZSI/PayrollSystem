package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.app.port.AttendanceRepositoryPort;
import com.com253.payrollsystem.infrastructure.persistence.dao.AttendanceDao;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceService {

    private final AttendanceRepositoryPort attendanceRepository;

    public AttendanceService() {
        this(new AttendanceDao());
    }

    public AttendanceService(AttendanceRepositoryPort attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public void updateTimeIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        attendanceRepository.updateTimeIn(employeeId, date, timeIn);
    }

    public void updateTimeOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        attendanceRepository.updateTimeOut(employeeId, date, timeOut);
    }

    public void deleteAttendance(String employeeId, LocalDate date) throws SQLException {
        attendanceRepository.deleteByEmployeeAndDate(employeeId, date);
    }

    public void upsertAttendance(String employeeId, LocalDate date, Double timeIn, Double timeOut) throws SQLException {
        attendanceRepository.upsert(employeeId, date, timeIn, timeOut);
    }

    public void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        attendanceRepository.clockIn(employeeId, date, timeIn);
    }

    public void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        attendanceRepository.clockOut(employeeId, date, timeOut);
    }

    public List<AttendanceRecord> getAttendanceHistory(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        return attendanceRepository.getAttendance(employeeId, from, to);
    }
}
