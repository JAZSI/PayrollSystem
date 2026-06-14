package com.com253.payrollsystem.statutory;

import com.com253.payrollsystem.statutory.dto.ContributionTableRequest;
import com.com253.payrollsystem.statutory.dto.ContributionTableResponse;
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

/** Admin-managed effective-dated statutory contribution tables. */
@RestController
@RequestMapping("/api/statutory-tables")
@PreAuthorize("hasRole('ADMIN')")
public class ContributionTableController {

    private final ContributionTableService service;

    public ContributionTableController(ContributionTableService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContributionTableResponse> list() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContributionTableResponse create(@Valid @RequestBody ContributionTableRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ContributionTableResponse update(@PathVariable Long id,
                                            @Valid @RequestBody ContributionTableRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
