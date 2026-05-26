package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;

public interface Command {
    void execute(CliContext ctx) throws Exception;
}
