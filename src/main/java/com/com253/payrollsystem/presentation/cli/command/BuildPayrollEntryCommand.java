package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import com.com253.payrollsystem.domain.model.PayrollEntry;

public class BuildPayrollEntryCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        System.out.print("Employee ID: ");
        String id = ctx.scanner.nextLine().trim();
        System.out.print("Cutoff period (e.g. 2026-05-01_2026-05-15): ");
        String period = ctx.scanner.nextLine().trim();
        PayrollEntry entry = ctx.payrollService.buildPayrollEntry(id, period);
        if (entry != null) {
            System.out.println("Payroll for " + id + ":\n" + entry);
        } else {
            System.out.println("Could not build payroll entry");
        }
    }
}
