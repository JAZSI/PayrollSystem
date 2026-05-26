package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import java.time.LocalDate;

public class ClockOutCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        System.out.print("Employee ID: ");
        String id = ctx.scanner.nextLine().trim();
        System.out.print("Date (YYYY-MM-DD): ");
        String dateStr = ctx.scanner.nextLine().trim();
        System.out.print("Time out (decimal hours, e.g. 17.5): ");
        double timeOut = Double.parseDouble(ctx.scanner.nextLine().trim());
        ctx.payrollService.clockOut(id, LocalDate.parse(dateStr), timeOut);
        System.out.println("Clocked out " + id + " at " + timeOut);
    }
}
