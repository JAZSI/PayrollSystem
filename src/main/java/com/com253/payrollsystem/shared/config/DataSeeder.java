package com.com253.payrollsystem.shared.config;

import com.com253.payrollsystem.shared.domain.HolidayType;
import com.com253.payrollsystem.holiday.HolidayEntity;
import com.com253.payrollsystem.leave.LeaveType;
import com.com253.payrollsystem.leave.LeaveTypeRepository;
import com.com253.payrollsystem.user.UserEntity;
import com.com253.payrollsystem.holiday.HolidayRepository;
import com.com253.payrollsystem.user.UserRepository;
import com.com253.payrollsystem.shared.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/** Seeds default admin + 2026 holidays on first run. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final HolidayRepository holidayRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository, HolidayRepository holidayRepository,
                      LeaveTypeRepository leaveTypeRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.admin.username}") String adminUsername,
                      @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.holidayRepository = holidayRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new UserEntity(
                    adminUsername, passwordEncoder.encode(adminPassword), Role.ADMIN, null));
            log.warn("Seeded default admin user '{}' — change the password after first login.",
                    adminUsername);
        }
        if (holidayRepository.count() == 0) {
            holidayRepository.saveAll(defaultHolidays2026());
            log.info("Seeded {} default holidays (2026).", holidayRepository.count());
        }
        if (leaveTypeRepository.count() == 0) {
            leaveTypeRepository.saveAll(defaultLeaveTypes());
            log.info("Seeded {} default leave types.", leaveTypeRepository.count());
        }
    }

    private static List<LeaveType> defaultLeaveTypes() {
        return List.of(
                new LeaveType("Vacation Leave", true, 5),
                new LeaveType("Sick Leave", true, 5),
                new LeaveType("Emergency Leave", true, 3),
                new LeaveType("Unpaid Leave", false, 0));
    }

    private static List<HolidayEntity> defaultHolidays2026() {
        HolidayType regular = HolidayType.REGULAR_HOLIDAY;
        HolidayType special = HolidayType.SPECIAL_OR_REST_DAY;
        return List.of(
                // Regular holidays
                new HolidayEntity(LocalDate.of(2026, 1, 1), "New Year's Day", regular),
                new HolidayEntity(LocalDate.of(2026, 4, 2), "Maundy Thursday", regular),
                new HolidayEntity(LocalDate.of(2026, 4, 3), "Good Friday", regular),
                new HolidayEntity(LocalDate.of(2026, 4, 9), "Araw ng Kagitingan", regular),
                new HolidayEntity(LocalDate.of(2026, 5, 1), "Labor Day", regular),
                new HolidayEntity(LocalDate.of(2026, 6, 12), "Independence Day", regular),
                new HolidayEntity(LocalDate.of(2026, 8, 31), "National Heroes Day", regular),
                new HolidayEntity(LocalDate.of(2026, 11, 30), "Bonifacio Day", regular),
                new HolidayEntity(LocalDate.of(2026, 12, 25), "Christmas Day", regular),
                new HolidayEntity(LocalDate.of(2026, 12, 30), "Rizal Day", regular),
                // Special (non-working) days
                new HolidayEntity(LocalDate.of(2026, 2, 17), "Chinese New Year", special),
                new HolidayEntity(LocalDate.of(2026, 4, 4), "Black Saturday", special),
                new HolidayEntity(LocalDate.of(2026, 8, 21), "Ninoy Aquino Day", special),
                new HolidayEntity(LocalDate.of(2026, 11, 1), "All Saints' Day", special),
                new HolidayEntity(LocalDate.of(2026, 11, 2), "All Souls' Day", special),
                new HolidayEntity(LocalDate.of(2026, 12, 8), "Immaculate Conception", special),
                new HolidayEntity(LocalDate.of(2026, 12, 24), "Christmas Eve", special),
                new HolidayEntity(LocalDate.of(2026, 12, 31), "Last Day of the Year", special));
    }
}
