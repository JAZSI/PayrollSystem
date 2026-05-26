package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;

public class SubmitPayrollCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        System.out.print("Employee ID: ");
        String id = ctx.scanner.nextLine().trim();
        System.out.print("Leave days: ");
        double leave = Double.parseDouble(ctx.scanner.nextLine().trim());
        System.out.print("OT hours: ");
        double ot = Double.parseDouble(ctx.scanner.nextLine().trim());
        System.out.print("Loan deduction: ");
        double loan = Double.parseDouble(ctx.scanner.nextLine().trim());
        boolean ok = ctx.payrollService.submitPayroll(id, leave, ot, loan);
        System.out.println(ok ? "Submission successful" : "Submission failed or duplicate");
    }
}
