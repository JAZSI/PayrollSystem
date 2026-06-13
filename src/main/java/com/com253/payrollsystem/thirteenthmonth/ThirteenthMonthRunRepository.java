package com.com253.payrollsystem.thirteenthmonth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThirteenthMonthRunRepository extends JpaRepository<ThirteenthMonthRun, Long> {

    List<ThirteenthMonthRun> findAllByOrderByCreatedAtDesc();
}
