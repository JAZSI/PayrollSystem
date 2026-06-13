package com.com253.payrollsystem.leave;

import com.com253.payrollsystem.leave.dto.LeaveTypeRequest;
import com.com253.payrollsystem.leave.dto.LeaveTypeResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Leave-type catalog: anyone authenticated reads; ADMIN edits. */
@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveService service;

    public LeaveTypeController(LeaveService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeaveTypeResponse> list() {
        return service.findAllTypes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public LeaveTypeResponse create(@Valid @RequestBody LeaveTypeRequest request) {
        return service.createType(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LeaveTypeResponse update(@PathVariable Long id, @Valid @RequestBody LeaveTypeRequest request) {
        return service.updateType(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}
