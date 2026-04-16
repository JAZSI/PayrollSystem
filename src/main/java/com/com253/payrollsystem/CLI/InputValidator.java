package com.com253.payrollsystem.CLI;

import java.util.Scanner;

public final class InputValidator {

    /**
     * Blocks object creation because this is a utility class.
     */
    private InputValidator() {}

    /**
     * Reads an integer and makes sure it is within the given range.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @param min smallest allowed value
     * @param max largest allowed value
     * @return a valid integer within the range
     */
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a decimal number and checks that it is at least the minimum.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @param min minimum allowed value
     * @return a valid number equal to or greater than min
     */
    public static double readDoubleMin(Scanner scanner, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min) {
                    return value;
                }
                System.out.println("  Value must be at least " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads text input and requires a non-empty value.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @return trimmed non-empty text
     */
    public static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  Input cannot be empty.");
        }
    }

    /**
     * Reads an employee ID that may contain digits and hyphens only.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @return validated employee ID
     */
    public static String readEmployeeId(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);
            if (value.matches("[0-9-]+")) {
                return value;
            }
            System.out.println("  Invalid ID. Use digits and hyphens only.");
        }
    }

    /**
     * Reads a person name and allows letters, spaces, apostrophes, and hyphens.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @return validated person name
     */
    public static String readPersonName(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);
            if (value.matches("[A-Za-z]+(?:[ .'-](?:[A-Za-z]+|[A-Za-z]\\.))*")) {
                return value;
            }
            System.out.println("  Invalid name. Use letters, spaces, periods, apostrophes, or hyphens only.");
        }
    }

    /**
     * Reads and validates the employee type code.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @return one of R, P, C, or T
     */
    public static String readEmployeeType(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R") || input.equals("P") || input.equals("C") || input.equals("T")) {
                return input;
            }
            System.out.println("  Invalid input. Please enter R, P, C, or T.");
        }
    }

    /**
     * Reads a yes or no answer.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @return true for Y, false for N
     */
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            }
            if (input.equals("N")) {
                return false;
            }
            System.out.println("  Invalid input. Enter Y or N.");
        }
    }

    /**
     * Reads military time in HHMM format and checks minute validity.
     *
     * @param scanner scanner used to read input
     * @param prompt text shown to the user
     * @return a valid HHMM value
     */
    public static int readHHMM(Scanner scanner, String prompt) {
        while (true) {
            int hhmm = readIntInRange(scanner, prompt, 0, 2359);
            if (isValidHHMM(hhmm)) {
                return hhmm;
            }
            System.out.println("    Invalid time. Minutes must be 00-59.");
        }
    }

    /**
     * Checks if the HHMM value has valid minutes from 00 to 59.
     *
     * @param hhmm time in HHMM format
     * @return true if minutes are valid, else false
     */
    public static boolean isValidHHMM(int hhmm) {
        int minutes = hhmm % 100;
        return minutes >= 0 && minutes <= 59;
    }
}