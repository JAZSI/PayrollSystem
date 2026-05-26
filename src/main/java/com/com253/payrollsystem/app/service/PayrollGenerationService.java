package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollSettings;
import com.com253.payrollsystem.domain.model.Submission;
import com.com253.payrollsystem.domain.model.TimeRecord;
import com.com253.payrollsystem.domain.service.PayrollCalculator;
import com.com253.payrollsystem.infrastructure.config.Database;
import com.com253.payrollsystem.infrastructure.config.TransactionManager;
import com.com253.payrollsystem.app.port.AttendanceRepositoryPort;
import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.app.port.SubmissionRepositoryPort;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.AttendanceRepository;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.EmployeeRepository;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.SubmissionRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.com253.payrollsystem.util.TimeUtils;

public class PayrollGenerationService {

    private final AttendanceRepositoryPort attendanceRepository;
    private final EmployeeRepositoryPort employeeRepository;
    private final SubmissionRepositoryPort submissionRepository;

    public PayrollGenerationService() {
        this(new AttendanceRepository(), new EmployeeRepository(), new SubmissionRepository());
    }

    public PayrollGenerationService(AttendanceRepositoryPort attendanceRepository,
                                    EmployeeRepositoryPort employeeRepository,
                                    SubmissionRepositoryPort submissionRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.submissionRepository = submissionRepository;
    }

    public PayrollEntry buildPayrollEntry(String employeeId, String cutOffPeriod) throws SQLException {
        java.sql.Connection conn = Database.getConnection();
        try {
            TransactionManager.begin(conn);

            Optional<Submission> subOpt = submissionRepository.findByEmployeeId(employeeId);
            if (subOpt.isEmpty() || subOpt.get().status() != Submission.Status.APPROVED) {
                return null;
            }
            Submission sub = subOpt.get();

            Optional<Employee> empOpt = employeeRepository.findById(employeeId);
            if (empOpt.isEmpty()) {
                return null;
            }
            Employee emp = empOpt.get();

            PayrollSettings settings = new PayrollSettings(26, 8.0, 17.0, 11.0);
            LocalDate today = LocalDate.now();
            YearMonth ym = YearMonth.of(today.getYear(), today.getMonthValue());
            com.com253.payrollsystem.domain.model.CutoffPeriod cp = com.com253.payrollsystem.domain.model.CutoffPeriod.fromLabel(cutOffPeriod);
            LocalDate from = ym.atDay(cp.startDay());
            LocalDate to = (cp.endDay() == -1) ? ym.atEndOfMonth() : ym.atDay(Math.min(cp.endDay(), ym.lengthOfMonth()));

            List<TimeRecord> records = buildTimeRecords(emp.getEmployeeId(), from, to);
            TimeRecord[] recordArray = records.toArray(new TimeRecord[0]);

            PayrollEntry entry = PayrollCalculator.buildPayrollEntry(emp, recordArray, cutOffPeriod, sub.loanDeduction(), settings);
            submissionRepository.savePayrollEntry(entry);

            TransactionManager.commit(conn);
            return entry;
        } catch (SQLException e) {
            TransactionManager.rollback(conn);
            throw e;
        } finally {
            Database.close(conn);
        }
    }

    private List<TimeRecord> buildTimeRecords(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        List<AttendanceRecord> attendance = attendanceRepository.getAttendance(employeeId, from, to);
        List<TimeRecord> records = new ArrayList<>();
        LocalDate current = from;

        while (!current.isAfter(to)) {
            int dayNum = current.getDayOfMonth();
            AttendanceRecord rec = findRecord(attendance, current);

            if (rec == null || rec.getTimeIn() == null) {
                records.add(new TimeRecord(dayNum, 0, 0, true, TimeRecord.HOLIDAY_NONE));
            } else {
                int timeIn = TimeUtils.toHHMM(rec.getTimeIn());
                int timeOut = (rec.getTimeOut() != null) ? TimeUtils.toHHMM(rec.getTimeOut()) : 1700;
                records.add(new TimeRecord(dayNum, timeIn, timeOut, false, TimeRecord.HOLIDAY_NONE));
            }

            current = current.plusDays(1);
        }
        return records;
    }

    private AttendanceRecord findRecord(List<AttendanceRecord> attendance, LocalDate date) {
        for (AttendanceRecord rec : attendance) {
            if (rec.getRecordDate().equals(date)) {
                return rec;
            }
            if (rec.getRecordDate().isAfter(date)) {
                break;
            }
        }
        return null;
    }
}
