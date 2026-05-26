package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import com.com253.payrollsystem.domain.model.Employee;

public class RegisterEmployeeCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        System.out.print("New employee ID: ");
        String id = ctx.scanner.nextLine().trim();
        System.out.print("First name: ");
        String first = ctx.scanner.nextLine().trim();
        System.out.print("Last name: ");
        String last = ctx.scanner.nextLine().trim();
        System.out.print("Employee type (REGULAR, PROBATIONARY, CONTRACTUAL, PARTTIMER) [REGULAR]: ");
        String typeStr = ctx.scanner.nextLine().trim();
        System.out.print("Monthly rate (0 for none): ");
        double monthly = Double.parseDouble(ctx.scanner.nextLine().trim());
        System.out.print("Hourly rate (0 for none): ");
        double hourly = Double.parseDouble(ctx.scanner.nextLine().trim());
        System.out.print("Has leave? (true/false) [true]: ");
        String hasLeaveStr = ctx.scanner.nextLine().trim();
        com.com253.payrollsystem.domain.model.Employee.EmployeeType type = com.com253.payrollsystem.domain.model.Employee.EmployeeType.REGULAR;
        if (!typeStr.isEmpty()) {
            try { type = com.com253.payrollsystem.domain.model.Employee.EmployeeType.valueOf(typeStr); } catch (Exception ex) { /* keep default */ }
        }
        boolean hasLeave = true;
        if (!hasLeaveStr.isEmpty()) { hasLeave = Boolean.parseBoolean(hasLeaveStr); }

        String name = first + " " + last;
        com.com253.payrollsystem.domain.model.LeaveBalance lb = new com.com253.payrollsystem.domain.model.LeaveBalance(0,0,0);
        com.com253.payrollsystem.domain.model.LoanBalance loan = new com.com253.payrollsystem.domain.model.LoanBalance(0.0);
        com.com253.payrollsystem.domain.model.Employee e = new com.com253.payrollsystem.domain.model.Employee(id, name, type, monthly, hourly, hasLeave, lb, loan);

        System.out.print("Set username for employee: ");
        String username = ctx.scanner.nextLine().trim();
        System.out.print("Set password for employee: ");
        String password = ctx.scanner.nextLine().trim();
        ctx.payrollService.registerEmployee(e, username, password);
        System.out.println("Registered employee " + id + " (" + name + ")");
    }
}
