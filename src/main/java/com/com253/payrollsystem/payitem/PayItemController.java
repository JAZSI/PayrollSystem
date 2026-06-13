package com.com253.payrollsystem.payitem;

import com.com253.payrollsystem.payitem.dto.PayItemRequest;
import com.com253.payrollsystem.payitem.dto.PayItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Allowances and other deductions per employee (ADMIN/HR). */
@RestController
public class PayItemController {

    private final PayItemService service;

    public PayItemController(PayItemService service) {
        this.service = service;
    }

    @GetMapping("/api/employees/{id}/pay-items")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<PayItemResponse> byEmployee(@PathVariable String id) {
        return service.findByEmployee(id);
    }

    @PostMapping("/api/pay-items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public PayItemResponse create(@Valid @RequestBody PayItemRequest request) {
        return service.create(request);
    }

    @PutMapping("/api/pay-items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public PayItemResponse update(@PathVariable Long id, @Valid @RequestBody PayItemRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/api/pay-items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
