package com.com253.payrollsystem.holiday;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {

    Optional<HolidayEntity> findByDate(LocalDate date);

    List<HolidayEntity> findAllByOrderByDate();

    boolean existsByDate(LocalDate date);
}
