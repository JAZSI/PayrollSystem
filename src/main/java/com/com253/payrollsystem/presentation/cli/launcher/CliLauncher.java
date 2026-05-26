package com.com253.payrollsystem.presentation.cli.launcher;

import java.util.Scanner;

public class CliLauncher {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new CliRunner().run(scanner);
        }
    }
}
