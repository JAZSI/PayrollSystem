package com.com253.payrollsystem.presentation.cli.formatter;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Submission;
import java.util.List;

public class Formatter {

    public static void printEmployees(List<Employee> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        System.out.println("Employees:");
        for (Employee e : list) {
            System.out.printf("- %s: %s (%s) type=%s monthly=%.2f hourly=%.2f\n",
                e.getEmployeeId(), e.getName(), e.getTypeName(), e.getEmployeeType(), e.getMonthlyRate(), e.getHourlyRate());
        }
    }

    public static void printSubmissions(List<Submission> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No pending submissions.");
            return;
        }
        System.out.println("Pending submissions:");
        for (Submission s : list) {
            System.out.printf("- id=%d employee=%s status=%s submittedAt=%s\n", s.id(), s.employeeId(), s.status(), s.submittedAt());
        }
    }
}
