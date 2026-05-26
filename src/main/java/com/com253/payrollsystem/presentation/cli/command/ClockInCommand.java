package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import java.time.LocalDate;

public class ClockInCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        System.out.print("Employee ID: ");
        String id = ctx.scanner.nextLine().trim();
        System.out.print("Date (YYYY-MM-DD): ");
        String dateStr = ctx.scanner.nextLine().trim();
        System.out.print("Time in (decimal hours, e.g. 8.0): ");
        double timeIn = Double.parseDouble(ctx.scanner.nextLine().trim());
        ctx.payrollService.clockIn(id, LocalDate.parse(dateStr), timeIn);
        System.out.println("Clocked in " + id + " at " + timeIn);
    }
}
