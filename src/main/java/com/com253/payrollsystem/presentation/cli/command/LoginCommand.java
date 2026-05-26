package com.com253.payrollsystem.presentation.cli.command;

import com.com253.payrollsystem.presentation.cli.launcher.CliContext;
import com.com253.payrollsystem.domain.model.EndUser;

public class LoginCommand implements Command {

    @Override
    public void execute(CliContext ctx) throws Exception {
        System.out.print("Username: ");
        String username = ctx.scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = ctx.scanner.nextLine().trim();
        EndUser user = ctx.payrollService.authenticate(username, password);
        if (user != null) {
            ctx.setCurrentUser(user);
            System.out.println("Logged in as " + user.getUsername());
        } else {
            System.out.println("Authentication failed");
        }
    }
}
