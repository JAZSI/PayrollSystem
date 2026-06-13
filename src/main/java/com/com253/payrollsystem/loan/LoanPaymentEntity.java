package com.com253.payrollsystem.loan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** One amortization payment posted against a loan when a payroll run locks. */
@Entity
@Table(name = "loan_payments")
public class LoanPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "payslip_id")
    private Long payslipId;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false, length = 20)
    private String period;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected LoanPaymentEntity() {
        // JPA
    }

    public LoanPaymentEntity(Long loanId, Long payslipId, double amount, String period) {
        this.loanId = loanId;
        this.payslipId = payslipId;
        this.amount = amount;
        this.period = period;
    }

    public Long getId() { return id; }
    public Long getLoanId() { return loanId; }
    public Long getPayslipId() { return payslipId; }
    public double getAmount() { return amount; }
    public String getPeriod() { return period; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
