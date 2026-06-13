package com.com253.payrollsystem.thirteenthmonth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThirteenthMonthEntryRepository extends JpaRepository<ThirteenthMonthEntry, Long> {

    List<ThirteenthMonthEntry> findByRunIdOrderByEmployeeName(Long runId);

    List<ThirteenthMonthEntry> findByEmployeeIdOrderByIdDesc(String employeeId);

    void deleteByRunId(Long runId);
}
