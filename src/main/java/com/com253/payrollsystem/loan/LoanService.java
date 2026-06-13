package com.com253.payrollsystem.loan;

import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.loan.dto.LoanRequest;
import com.com253.payrollsystem.loan.dto.LoanResponse;
import com.com253.payrollsystem.shared.Money;
import com.com253.payrollsystem.shared.error.ConflictException;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/** Loan accounts: CRUD, per-cut-off deduction total, and posting on run lock. */
@Service
@Transactional
public class LoanService {

    private final LoanRepository loans;
    private final LoanPaymentRepository payments;
    private final EmployeeRepository employees;

    public LoanService(LoanRepository loans, LoanPaymentRepository payments,
                       EmployeeRepository employees) {
        this.loans = loans;
        this.payments = payments;
        this.employees = employees;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findByEmployee(String employeeId) {
        return loans.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream().map(LoanService::toResponse).toList();
    }

    public LoanResponse create(LoanRequest req) {
        if (!employees.existsById(req.employeeId())) {
            throw new NotFoundException("Employee not found: " + req.employeeId());
        }
        LoanEntity loan = new LoanEntity(req.employeeId(), req.type(),
                Money.round2(req.principal()), Money.round2(req.perCutoffAmount()), req.startPeriod());
        return toResponse(loans.save(loan));
    }

    public LoanResponse update(Long id, LoanRequest req) {
        LoanEntity loan = getOrThrow(id);
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new ConflictException("Only an active loan can be edited");
        }
        loan.setType(req.type());
        loan.setPerCutoffAmount(Money.round2(req.perCutoffAmount()));
        loan.setStartPeriod(req.startPeriod());
        return toResponse(loans.save(loan));
    }

    public LoanResponse cancel(Long id) {
        LoanEntity loan = getOrThrow(id);
        loan.setStatus(LoanStatus.CANCELLED);
        return toResponse(loans.save(loan));
    }

    // --------------------------- Payroll integration ---------------------------

    /** Total deduction for a cut-off: each active loan's per-cut-off amount, capped at balance. */
    @Transactional(readOnly = true)
    public double activeCutoffTotal(String employeeId) {
        double total = loans.findByEmployeeIdAndStatus(employeeId, LoanStatus.ACTIVE).stream()
                .mapToDouble(l -> Math.min(l.getPerCutoffAmount(), l.getBalance()))
                .sum();
        return Money.round2(total);
    }

    /** On lock: post a payment per active loan and decrement balances; mark PAID at zero. */
    public void postForRun(String period, Map<String, Long> payslipIdByEmployee) {
        payslipIdByEmployee.forEach((employeeId, payslipId) -> {
            for (LoanEntity loan : loans.findByEmployeeIdAndStatus(employeeId, LoanStatus.ACTIVE)) {
                double amount = Money.round2(Math.min(loan.getPerCutoffAmount(), loan.getBalance()));
                if (amount <= 0) {
                    continue;
                }
                loan.setBalance(Money.round2(loan.getBalance() - amount));
                if (loan.getBalance() <= 0) {
                    loan.setBalance(0);
                    loan.setStatus(LoanStatus.PAID);
                }
                loans.save(loan);
                payments.save(new LoanPaymentEntity(loan.getId(), payslipId, amount, period));
            }
        });
    }

    // ------------------------------- helpers -------------------------------

    private LoanEntity getOrThrow(Long id) {
        return loans.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found: " + id));
    }

    private static LoanResponse toResponse(LoanEntity l) {
        return new LoanResponse(
                l.getId(), l.getEmployeeId(), l.getType(), l.getType().label(),
                l.getPrincipal(), l.getPerCutoffAmount(), l.getBalance(),
                l.getStatus(), l.getStartPeriod(),
                l.getCreatedAt() == null ? null : l.getCreatedAt().toString());
    }
}
