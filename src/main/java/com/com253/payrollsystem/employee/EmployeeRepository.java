package com.com253.payrollsystem.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    List<EmployeeEntity> findByActiveTrue();

    long countByActiveTrue();
}
