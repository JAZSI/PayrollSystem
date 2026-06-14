package com.com253.payrollsystem.shared.config;

import com.com253.payrollsystem.shared.domain.HolidayType;
import com.com253.payrollsystem.holiday.HolidayEntity;
import com.com253.payrollsystem.leave.LeaveType;
import com.com253.payrollsystem.leave.LeaveTypeRepository;
import com.com253.payrollsystem.statutory.ContributionAgency;
import com.com253.payrollsystem.statutory.ContributionBracketEntity;
import com.com253.payrollsystem.statutory.ContributionBracketRepository;
import com.com253.payrollsystem.statutory.ContributionTableEntity;
import com.com253.payrollsystem.statutory.ContributionTableRepository;
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
    private final ContributionTableRepository contributionTableRepository;
    private final ContributionBracketRepository contributionBracketRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository, HolidayRepository holidayRepository,
                      LeaveTypeRepository leaveTypeRepository,
                      ContributionTableRepository contributionTableRepository,
                      ContributionBracketRepository contributionBracketRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.admin.username}") String adminUsername,
                      @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.holidayRepository = holidayRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.contributionTableRepository = contributionTableRepository;
        this.contributionBracketRepository = contributionBracketRepository;
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
        if (contributionTableRepository.count() == 0) {
            seedContributionTables();
            log.info("Seeded 2026 statutory contribution tables.");
        }
    }

    private static final double OPEN_ENDED = 1e12;

    /** Seeds the 2026 SSS/PhilHealth/Pag-IBIG/BIR tables to match the built-in constants. */
    private void seedContributionTables() {
        LocalDate effective = LocalDate.of(2026, 1, 1);

        Long sss = newTable(ContributionAgency.SSS, effective, "SSS 2026 (seeded)");
        int seq = 0;
        for (int k = 0; k < 60; k++) {
            double lower = k == 0 ? 0 : 5_250 + 500.0 * (k - 1);
            double upper = 5_250 + 500.0 * k;
            double amount = 250 + 25.0 * k;
            bracket(sss, seq++, lower, upper, amount, 0.0);
        }
        bracket(sss, seq, 34_750, OPEN_ENDED, 1_750, 0.0);

        Long philhealth = newTable(ContributionAgency.PHILHEALTH, effective, "PhilHealth 2026 (seeded)");
        bracket(philhealth, 0, 500, 2_750, 0.0, 0.055); // lower=floor, upper=ceiling, rate

        Long pagibig = newTable(ContributionAgency.PAGIBIG, effective, "Pag-IBIG 2026 (seeded)");
        bracket(pagibig, 0, 0, 1_500, 100, 0.01);          // amount = cap
        bracket(pagibig, 1, 1_500, OPEN_ENDED, 100, 0.02);

        Long bir = newTable(ContributionAgency.BIR, effective, "BIR TRAIN annual (seeded)");
        bracket(bir, 0, 0, 250_000, 0, 0.0);               // amount = base tax at lower bound
        bracket(bir, 1, 250_000, 400_000, 0, 0.15);
        bracket(bir, 2, 400_000, 800_000, 22_500, 0.20);
        bracket(bir, 3, 800_000, 2_000_000, 102_500, 0.25);
        bracket(bir, 4, 2_000_000, 8_000_000, 402_500, 0.30);
        bracket(bir, 5, 8_000_000, OPEN_ENDED, 2_202_500, 0.35);
    }

    private Long newTable(ContributionAgency agency, LocalDate effective, String note) {
        return contributionTableRepository
                .save(new ContributionTableEntity(agency, effective, true, note)).getId();
    }

    private void bracket(Long tableId, int seq, double lower, double upper, double amount, double rate) {
        contributionBracketRepository.save(
                new ContributionBracketEntity(tableId, seq, lower, upper, amount, rate));
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
