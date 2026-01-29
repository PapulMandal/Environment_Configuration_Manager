package com.environment.manager.util.util;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class for console operations and formatting.
 */
public final class ConsoleUtil {
    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private static final String BG_BLACK = "\u001B[40m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_YELLOW = "\u001B[43m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String BG_PURPLE = "\u001B[45m";
    private static final String BG_CYAN = "\u001B[46m";
    private static final String BG_WHITE = "\u001B[47m";

    private static final String BOLD = "\u001B[1m";
    private static final String UNDERLINE = "\u001B[4m";
    private static final String REVERSED = "\u001B[7m";

    private ConsoleUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Prints a colored message.
     */
    public static void printColor(String message, String color) {
        System.out.println(color + message + RESET);
    }

    /**
     * Prints a success message in green.
     */
    public static void printSuccess(String message) {
        printColor("✅ " + message, GREEN);
    }

    /**
     * Prints an error message in red.
     */
    public static void printError(String message) {
        printColor("❌ " + message, RED);
    }

    /**
     * Prints a warning message in yellow.
     */
    public static void printWarning(String message) {
        printColor("⚠️ " + message, YELLOW);
    }

    /**
     * Prints an info message in blue.
     */
    public static void printInfo(String message) {
        printColor("ℹ️ " + message, BLUE);
    }

    /**
     * Prints a header with formatting.
     */
    public static void printHeader(String title) {
        System.out.println();
        printColor("╔" + "═".repeat(title.length() + 4) + "╗", CYAN);
        printColor("║  " + BOLD + title + RESET + CYAN + "  ║", CYAN);
        printColor("╚" + "═".repeat(title.length() + 4) + "╝", CYAN);
        System.out.println();
    }

    /**
     * Prints a subheader.
     */
    public static void printSubheader(String title) {
        System.out.println();
        printColor("─".repeat(50), CYAN);
        printColor(" " + title, CYAN + BOLD);
        printColor("─".repeat(50), CYAN);
        System.out.println();
    }

    /**
     * Prints a separator line.
     */
    public static void printSeparator() {
        printColor("─".repeat(80), WHITE);
    }

    /**
     * Prints a boxed message.
     */
    public static void printBoxed(String message, String color) {
        String[] lines = message.split("\n");
        int maxLength = 0;
        for (String line : lines) {
            maxLength = Math.max(maxLength, line.length());
        }

        String topBorder = "┌" + "─".repeat(maxLength + 2) + "┐";
        String bottomBorder = "└" + "─".repeat(maxLength + 2) + "┘";

        printColor(topBorder, color);
        for (String line : lines) {
            printColor("│ " + line + " ".repeat(maxLength - line.length()) + " │", color);
        }
        printColor(bottomBorder, color);
    }

    /**
     * Prints a list with numbering.
     */
    public static void printNumberedList(List<String> items, String color) {
        for (int i = 0; i < items.size(); i++) {
            printColor(String.format("%2d. %s", i + 1, items.get(i)), color);
        }
    }

    /**
     * Prints a table.
     */
    public static void printTable(String[] headers, List<String[]> rows) {
        // Calculate column widths
        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
        }

        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i < colWidths.length) {
                    colWidths[i] = Math.max(colWidths[i], row[i] != null ? row[i].length() : 0);
                }
            }
        }

        // Print header
        printColor("┌", CYAN);
        for (int i = 0; i < colWidths.length; i++) {
            printColor("─".repeat(colWidths[i] + 2), CYAN);
            if (i < colWidths.length - 1) {
                printColor("┬", CYAN);
            }
        }
        printColor("┐", CYAN);

        System.out.print(CYAN + "│ ");
        for (int i = 0; i < headers.length; i++) {
            System.out.print(BOLD + headers[i] + RESET + CYAN);
            System.out.print(" ".repeat(colWidths[i] - headers[i].length()) + " │ ");
        }
        System.out.println(RESET);

        // Print separator
        printColor("├", CYAN);
        for (int i = 0; i < colWidths.length; i++) {
            printColor("─".repeat(colWidths[i] + 2), CYAN);
            if (i < colWidths.length - 1) {
                printColor("┼", CYAN);
            }
        }
        printColor("┤", CYAN);

        // Print rows
        for (String[] row : rows) {
            System.out.print(CYAN + "│ ");
            for (int i = 0; i < row.length; i++) {
                String cell = row[i] != null ? row[i] : "";
                System.out.print(cell);
                System.out.print(" ".repeat(colWidths[i] - cell.length()) + " │ ");
            }
            System.out.println(RESET);
        }

        // Print footer
        printColor("└", CYAN);
        for (int i = 0; i < colWidths.length; i++) {
            printColor("─".repeat(colWidths[i] + 2), CYAN);
            if (i < colWidths.length - 1) {
                printColor("┴", CYAN);
            }
        }
        printColor("┘", CYAN);
    }

    /**
     * Clears the console screen.
     */
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just print some newlines
            System.out.println("\n".repeat(50));
        }
    }

    /**
     * Reads an integer with validation.
     */
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                printError("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                printError("Please enter a valid number");
            }
        }
    }

    /**
     * Reads a string with validation.
     */
    public static String readString(Scanner scanner, String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (required && input.isEmpty()) {
                printError("This field is required");
                continue;
            }

            return input;
        }
    }

    /**
     * Reads a yes/no confirmation.
     */
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }

            printError("Please enter 'y' or 'n'");
        }
    }

    /**
     * Shows a loading animation.
     */
    public static void showLoading(String message, int seconds) {
        System.out.print(message + " ");

        Thread loadingThread = new Thread(() -> {
            String[] spinner = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
            long endTime = System.currentTimeMillis() + (seconds * 1000);

            int i = 0;
            while (System.currentTimeMillis() < endTime) {
                System.out.print("\r" + message + " " + spinner[i % spinner.length]);
                i++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        loadingThread.start();
        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\r" + message + " ✅ Done!" + " ".repeat(20));
    }

    /**
     * Prints a progress bar.
     */
    public static void printProgressBar(int current, int total, int width) {
        float percentage = (float) current / total;
        int filled = (int) (width * percentage);
        int empty = width - filled;

        StringBuilder bar = new StringBuilder();
        bar.append("[");
        bar.append(GREEN);
        bar.append("█".repeat(filled));
        bar.append(RESET);
        bar.append("░".repeat(empty));
        bar.append("] ");
        bar.append(String.format("%3.0f%%", percentage * 100));

        System.out.print("\r" + bar.toString());
        if (current == total) {
            System.out.println();
        }
    }

    /**
     * Gets a colored string.
     */
    public static String getColored(String text, String color) {
        return color + text + RESET;
    }

    /**
     * Prints with a specific color without newline.
     */
    public static void printColorNoNewline(String message, String color) {
        System.out.print(color + message + RESET);
    }
}