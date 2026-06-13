package com.com253.payrollsystem.leave;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);

    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(LeaveStatus status);

    List<LeaveRequest> findAllByOrderByCreatedAtDesc();

    List<LeaveRequest> findByEmployeeIdAndStatus(String employeeId, LeaveStatus status);
}
