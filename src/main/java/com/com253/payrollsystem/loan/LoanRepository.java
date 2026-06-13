package com.com253.payrollsystem.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    List<LoanEntity> findByEmployeeIdAndStatus(String employeeId, LoanStatus status);
}
