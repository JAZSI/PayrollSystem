package com.com253.payrollsystem.statutory;

import com.com253.payrollsystem.shared.error.NotFoundException;
import com.com253.payrollsystem.statutory.dto.ContributionBracketDto;
import com.com253.payrollsystem.statutory.dto.ContributionTableRequest;
import com.com253.payrollsystem.statutory.dto.ContributionTableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Admin CRUD for effective-dated contribution tables. */
@Service
@Transactional
public class ContributionTableService {

    private final ContributionTableRepository tables;
    private final ContributionBracketRepository brackets;

    public ContributionTableService(ContributionTableRepository tables,
                                    ContributionBracketRepository brackets) {
        this.tables = tables;
        this.brackets = brackets;
    }

    @Transactional(readOnly = true)
    public List<ContributionTableResponse> findAll() {
        return tables.findAllByOrderByAgencyAscEffectiveFromDesc().stream()
                .map(this::toResponse).toList();
    }

    public ContributionTableResponse create(ContributionTableRequest req) {
        ContributionTableEntity table = tables.save(new ContributionTableEntity(
                req.agency(), req.effectiveFrom(), req.active(), req.note()));
        saveBrackets(table.getId(), req.brackets());
        return toResponse(table);
    }

    public ContributionTableResponse update(Long id, ContributionTableRequest req) {
        ContributionTableEntity table = tables.findById(id)
                .orElseThrow(() -> new NotFoundException("Contribution table not found: " + id));
        table.setAgency(req.agency());
        table.setEffectiveFrom(req.effectiveFrom());
        table.setActive(req.active());
        table.setNote(req.note());
        tables.save(table);
        brackets.deleteByTableId(id);
        saveBrackets(id, req.brackets());
        return toResponse(table);
    }

    public void delete(Long id) {
        if (!tables.existsById(id)) {
            throw new NotFoundException("Contribution table not found: " + id);
        }
        brackets.deleteByTableId(id);
        tables.deleteById(id);
    }

    private void saveBrackets(Long tableId, List<ContributionBracketDto> rows) {
        int seq = 0;
        for (ContributionBracketDto r : rows) {
            brackets.save(new ContributionBracketEntity(
                    tableId, seq++, r.lowerBound(), r.upperBound(), r.amount(), r.rate()));
        }
    }

    private ContributionTableResponse toResponse(ContributionTableEntity t) {
        List<ContributionBracketDto> rows = brackets.findByTableIdOrderBySeq(t.getId()).stream()
                .map(b -> new ContributionBracketDto(b.getId(), b.getSeq(), b.getLowerBound(),
                        b.getUpperBound(), b.getAmount(), b.getRate()))
                .toList();
        return new ContributionTableResponse(t.getId(), t.getAgency(),
                t.getEffectiveFrom().toString(), t.isActive(), t.getNote(), rows);
    }
}
