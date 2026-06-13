package com.com253.payrollsystem.payroll;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRunRepository extends JpaRepository<PayrollRunEntity, Long> {

    List<PayrollRunEntity> findAllByOrderByCreatedAtDesc();

    boolean existsByCutoffPeriodAndStatus(String cutoffPeriod, PayrollRunStatus status);

    long countByStatus(PayrollRunStatus status);

    Optional<PayrollRunEntity> findFirstByOrderByCreatedAtDesc();
}
