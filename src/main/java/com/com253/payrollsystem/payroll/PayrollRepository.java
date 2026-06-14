package com.com253.payrollsystem.payroll;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<PayslipEntity, Long> {

    List<PayslipEntity> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    List<PayslipEntity> findAllByOrderByCreatedAtDesc();

    List<PayslipEntity> findByRunIdOrderByEmployeeName(Long runId);

    List<PayslipEntity> findByCutoffPeriodOrderByEmployeeName(String cutoffPeriod);
}
