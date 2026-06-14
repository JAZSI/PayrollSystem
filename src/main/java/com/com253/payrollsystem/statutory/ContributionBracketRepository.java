package com.com253.payrollsystem.statutory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionBracketRepository extends JpaRepository<ContributionBracketEntity, Long> {

    List<ContributionBracketEntity> findByTableIdOrderBySeq(Long tableId);

    void deleteByTableId(Long tableId);
}
