package label;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Priority {
    public static final String HIGH = "HIGH";
    public static final String MEDIUM = "MEDIUM";
    public static final String LOW = "LOW";

    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<String> priorityList = new ArrayList<>();

    public static int getValue(String priority) {
        switch (priority.toUpperCase()) {
        case HIGH: return 3;
        case MEDIUM: return 2;
        case LOW: return 1;
        default: return 0;
        }
    }

    public static boolean isValid(String input) {
        if (input == null){
            return false;
        }
        String allUpper = input.trim().toUpperCase();
        return allUpper.equals(HIGH) || allUpper.equals(MEDIUM) || allUpper.equals(LOW);
    }

    public static String normalize(String input) {
        return input.trim().toUpperCase();
    }

    public static String priorityInput() {
        if (System.console() != null) {
            System.out.print("Enter event priority (LOW, MEDIUM, HIGH): ");
        }

        String input;
        try {
            input = scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return LOW;
        }

        while (!isValid(input)) {
            System.out.println("Invalid priority. Please enter LOW, MEDIUM, or HIGH.");
            System.out.print("Enter event priority (LOW, MEDIUM, HIGH): ");
            input = scanner.nextLine().trim();
        }

        return normalize(input);
    }

    public static void addPriority(String priority) {
        priorityList.add(priority);
    }

    public static void updatePriority(int index, String newPriority) {
        if (index >= 0 && index < priorityList.size()) {
            priorityList.set(index, newPriority);
        }
    }

    public static String getPriority(int index) {
        if (index >= 0 && index < priorityList.size()) {
            return priorityList.get(index);
        }
        return LOW;
    }

    public static void removePriority(int index) {
        if (index >= 0 && index < priorityList.size()) {
            priorityList.remove(index);
        }
    }

    public static ArrayList<String> getAllPriorities() {
        return priorityList;
    }

    public static void clearPriorities() {
        priorityList.clear();
    }

    public static void loadFromStorage(List<String> savedPriorities) {
        priorityList.clear();
        priorityList.addAll(savedPriorities);
    }
}
