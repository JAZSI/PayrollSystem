package com.com253.payrollsystem.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeRecordRepository extends JpaRepository<TimeRecordEntity, Long> {

    List<TimeRecordEntity> findByEmployeeIdAndCutoffPeriodOrderByDayNumber(
            String employeeId, String cutoffPeriod);

    Optional<TimeRecordEntity> findByEmployeeIdAndCutoffPeriodAndDayNumber(
            String employeeId, String cutoffPeriod, int dayNumber);

    void deleteByEmployeeIdAndCutoffPeriod(String employeeId, String cutoffPeriod);
}
