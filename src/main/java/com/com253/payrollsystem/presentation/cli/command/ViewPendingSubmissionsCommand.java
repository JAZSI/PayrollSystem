package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import com.com253.payrollsystem.domain.model.Submission;
import com.com253.payrollsystem.presentation.cli.formatter.Formatter;
import java.util.List;

public class ViewPendingSubmissionsCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        List<Submission> subs = ctx.payrollService.getPendingSubmissions();
        Formatter.printSubmissions(subs);
    }
}
