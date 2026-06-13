package com.com253.payrollsystem.payitem;

import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.payitem.dto.PayItemRequest;
import com.com253.payrollsystem.payitem.dto.PayItemResponse;
import com.com253.payrollsystem.shared.Money;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Allowances and other deductions: CRUD plus the active totals payroll consumes. */
@Service
@Transactional
public class PayItemService {

    private final PayItemRepository items;
    private final EmployeeRepository employees;

    public PayItemService(PayItemRepository items, EmployeeRepository employees) {
        this.items = items;
        this.employees = employees;
    }

    @Transactional(readOnly = true)
    public List<PayItemResponse> findByEmployee(String employeeId) {
        return items.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream().map(PayItemService::toResponse).toList();
    }

    public PayItemResponse create(PayItemRequest req) {
        if (!employees.existsById(req.employeeId())) {
            throw new NotFoundException("Employee not found: " + req.employeeId());
        }
        PayItemEntity item = new PayItemEntity(req.employeeId(), req.kind(), req.name(),
                Money.round2(req.amount()), req.taxable(), req.recurring());
        return toResponse(items.save(item));
    }

    public PayItemResponse update(Long id, PayItemRequest req) {
        PayItemEntity item = getOrThrow(id);
        item.setKind(req.kind());
        item.setName(req.name());
        item.setAmount(Money.round2(req.amount()));
        item.setTaxable(req.taxable());
        item.setRecurring(req.recurring());
        return toResponse(items.save(item));
    }

    public void delete(Long id) {
        if (!items.existsById(id)) {
            throw new NotFoundException("Pay item not found: " + id);
        }
        items.deleteById(id);
    }

    /** Active allowance/deduction totals for the payroll engine. */
    @Transactional(readOnly = true)
    public PayItemTotals totalsFor(String employeeId) {
        double taxable = 0.0;
        double nonTaxable = 0.0;
        double deductions = 0.0;
        for (PayItemEntity item : items.findByEmployeeIdAndActiveTrue(employeeId)) {
            if (item.getKind() == PayItemKind.ALLOWANCE) {
                if (item.isTaxable()) {
                    taxable += item.getAmount();
                } else {
                    nonTaxable += item.getAmount();
                }
            } else {
                deductions += item.getAmount();
            }
        }
        return new PayItemTotals(Money.round2(taxable), Money.round2(nonTaxable), Money.round2(deductions));
    }

    private PayItemEntity getOrThrow(Long id) {
        return items.findById(id)
                .orElseThrow(() -> new NotFoundException("Pay item not found: " + id));
    }

    private static PayItemResponse toResponse(PayItemEntity i) {
        return new PayItemResponse(i.getId(), i.getEmployeeId(), i.getKind(), i.getName(),
                i.getAmount(), i.isTaxable(), i.isRecurring(), i.isActive(),
                i.getCreatedAt() == null ? null : i.getCreatedAt().toString());
    }
}
