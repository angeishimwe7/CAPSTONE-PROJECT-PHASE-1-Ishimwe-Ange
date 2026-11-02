package Lab3.console;

import java.util.Scanner;

/** Safe input helpers: keep asking until the user types something valid. */
public class ConsoleIO {
    private final Scanner in;

    public ConsoleIO(Scanner in) { this.in = in; }

    public String readOptional(String prompt) {
        System.out.print(prompt + ": ");
        return in.nextLine().trim();
    }

    public String readRequired(String prompt) {
        while (true) {
            String v = readOptional(prompt);
            if (!v.isEmpty()) return v;
            System.out.println("  This field cannot be empty.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String v = readRequired(prompt);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    /** Returns null when the user just presses Enter. */
    public Integer readOptionalInt(String prompt) {
        while (true) {
            String v = readOptional(prompt);
            if (v.isEmpty()) return null;
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number, or press Enter to skip.");
            }
        }
    }

    public double readGrade(String prompt) {
        while (true) {
            String v = readRequired(prompt);
            try {
                double g = Double.parseDouble(v);
                if (g >= 0.0 && g <= 4.0) return g;
                System.out.println("  Grade must be between 0.0 and 4.0.");
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number like 3.5.");
            }
        }
    }

    public boolean confirm(String prompt) {
        while (true) {
            String v = readRequired(prompt + " (y/n)").toLowerCase();
            if (v.equals("y") || v.equals("yes")) return true;
            if (v.equals("n") || v.equals("no")) return false;
            System.out.println("  Please answer y or n.");
        }
    }
}
