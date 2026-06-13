package com.com253.payrollsystem.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPaymentEntity, Long> {

    List<LoanPaymentEntity> findByLoanIdOrderByCreatedAtDesc(Long loanId);
}
