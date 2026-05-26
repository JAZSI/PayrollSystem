package com.com253.payrollsystem.presentation.cli.launcher;

import com.com253.payrollsystem.app.service.PayrollService;
import com.com253.payrollsystem.domain.model.EndUser;
import java.util.Scanner;

public class CliContext {
    public final PayrollService payrollService;
    public final Scanner scanner;
    private EndUser currentUser;

    public CliContext(PayrollService payrollService, Scanner scanner) {
        this.payrollService = payrollService;
        this.scanner = scanner;
    }

    public EndUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(EndUser user) {
        this.currentUser = user;
    }
}
