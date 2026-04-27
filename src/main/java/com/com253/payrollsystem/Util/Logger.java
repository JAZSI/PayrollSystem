package com.com253.payrollsystem.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger utility that prints formatted, timestamped, ANSI-colored console output.
 */
public final class Logger {

    private static final String RESET = "\u001B[0m";
    private static final String BRIGHT = "\u001B[1m";
    private static final String DIM = "\u001B[2m";

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String GRAY = "\u001B[90m";

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

    private Logger() {
        // Utility class
    }

    /**
     * Apply ANSI color to a string.
     *
     * @param color ANSI color code
     * @param text text to colorize
     * @return colorized text
     */
    public static String colorize(String color, String text) {
        if (color == null || color.isBlank()) {
            return text;
        }
        return color + text + RESET;
    }

    /**
     * Get formatted timestamp.
     *
     * @return current timestamp in MM/dd/yyyy hh:mm:ss a
     */
    public static String getTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    /**
     * Print a formatted log line.
     *
     * @param level log level label
     * @param color ANSI color code for the level
     * @param message log message
     */
    public static void log(String level, String color, String message) {
        String timestamp = getTimestamp();
        String safeMessage = message == null ? "" : message;

        System.out.println(
                DIM + "[" + timestamp + "]" + RESET + " "
                + color + "[" + level + "]\t" + RESET + " "
                + safeMessage
        );
    }

    public static void info(String message) {
        log("INFO", BLUE, message);
    }

    public static void success(String message) {
        log("SUCCESS", GREEN, message);
    }

    public static void warn(String message) {
        log("WARNING", YELLOW, message);
    }

    public static void error(String message) {
        log("ERROR", RED, message);
    }

    public static void debug(String message) {
        log("DEBUG", MAGENTA, message);
    }

    /**
     * Expose common ANSI color codes for callers that need custom output.
     */
    public static final class Colors {
        public static final String RESET = Logger.RESET;
        public static final String BRIGHT = Logger.BRIGHT;
        public static final String DIM = Logger.DIM;
        public static final String RED = Logger.RED;
        public static final String GREEN = Logger.GREEN;
        public static final String YELLOW = Logger.YELLOW;
        public static final String BLUE = Logger.BLUE;
        public static final String MAGENTA = Logger.MAGENTA;
        public static final String CYAN = Logger.CYAN;
        public static final String WHITE = Logger.WHITE;
        public static final String GRAY = Logger.GRAY;

        private Colors() {
            // Constants holder
        }
    }
}
