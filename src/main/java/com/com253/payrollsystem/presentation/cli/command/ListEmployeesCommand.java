package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.presentation.cli.formatter.Formatter;
import java.util.List;

public class ListEmployeesCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        List<Employee> employees = ctx.payrollService.getAllEmployees();
        Formatter.printEmployees(employees);
    }
}
