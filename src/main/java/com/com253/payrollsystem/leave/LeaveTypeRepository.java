package com.com253.payrollsystem.leave;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    List<LeaveType> findAllByOrderByName();

    boolean existsByName(String name);
}
