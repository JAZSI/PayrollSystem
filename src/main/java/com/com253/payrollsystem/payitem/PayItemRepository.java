package com.com253.payrollsystem.payitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayItemRepository extends JpaRepository<PayItemEntity, Long> {

    List<PayItemEntity> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    List<PayItemEntity> findByEmployeeIdAndActiveTrue(String employeeId);
}
