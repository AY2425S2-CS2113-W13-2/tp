package label;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import command.LoginCommand;
import ui.UI;

/**
 * This class handles event priorities, including defining valid priorities,
 * normalizing and validating priority inputs, and storing and managing a list of event priorities.
 * It provides methods for retrieving, updating, and removing priorities, as well as interacting
 * with the user to set priorities for events.
 */
public class Priority {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());


    /**
     * Constant representing the HIGH priority level.
     */
    public static final String HIGH = "HIGH";

    /**
     * Constant representing the MEDIUM priority level.
     */
    public static final String MEDIUM = "MEDIUM";

    /**
     * Constant representing the LOW priority level.
     */
    public static final String LOW = "LOW";

    /**
     * A list to store all the priorities assigned to events.
     */
    private static final ArrayList<String> priorityList = new ArrayList<>();

    /**
     * The UI instance used to interact with the user for input and output.
     */
    private static final UI ui = new UI();

    /**
     * Returns the numeric value associated with a given priority.
     *
     * @param priority The priority as a string (e.g., "HIGH", "MEDIUM", "LOW").
     * @return The numeric value corresponding to the priority (3 for HIGH, 2 for MEDIUM, 1 for LOW).
     */
    public static int getValue(String priority) {
        switch (priority.toUpperCase()) {
        case HIGH: return 3;
        case MEDIUM: return 2;
        case LOW: return 1;
        default: return 0;
        }
    }

    /**
     * Checks if the input string is a valid priority.
     *
     * @param input The priority input to validate.
     * @return True if the input is a valid priority (LOW, MEDIUM, or HIGH), otherwise false.
     */
    public static boolean isValid(String input) {
        if (input == null){
            return false;
        }
        String allUpper = input.trim().toUpperCase();
        return allUpper.equals(HIGH) || allUpper.equals(MEDIUM) || allUpper.equals(LOW);
    }

    /**
     * Normalizes the input by trimming and converting it to uppercase.
     *
     * @param input The priority input to normalize.
     * @return The normalized priority in uppercase.
     */
    public static String normalize(String input) {
        return input.trim().toUpperCase();
    }

    /**
     * Prompts the user to input an event priority and validates the input.
     * If the input is invalid, the user is repeatedly prompted until a valid priority is entered.
     *
     * @return The valid priority input entered by the user (LOW, MEDIUM, or HIGH).
     */
    public static String priorityInput() {
        ui.showMessage("Enter event priority (LOW, MEDIUM, HIGH): ");
        String input;
        try {
            input = ui.readLine().trim();
        } catch (NoSuchElementException e) {
            return LOW;
        }

        while (!isValid(input)) {
            ui.showMessage("Invalid priority. Please enter LOW, MEDIUM, or HIGH.");
            ui.showMessage("Enter event priority (LOW, MEDIUM, HIGH): ");
            input = ui.readLine().trim();
        }

        return normalize(input);
    }

    /**
     * Adds a priority to the list of priorities.
     *
     * @param priority The priority to add to the list.
     */
    public static void addPriority(String priority) {
        assert priority != null : "Priority cannot be null";
        LOGGER.info("Attempting adding priority");
        priorityList.add(priority);
    }

    /**
     * Updates the priority at the specified index in the list of priorities.
     *
     * @param index The index of the priority to update.
     * @param newPriority The new priority to assign at the given index.
     */
    public static void updatePriority(int index, String newPriority) {
        if (index >= 0 && index < priorityList.size()) {
            priorityList.set(index, newPriority);
        }
    }

    /**
     * Returns the priority at the specified index in the list.
     *
     * @param index The index of the priority to retrieve.
     * @return The priority at the specified index, or "LOW" if the index is invalid.
     */
    public static String getPriority(int index) {
        if (index >= 0 && index < priorityList.size()) {
            return priorityList.get(index);
        }
        return LOW;
    }

    /**
     * Removes the priority at the specified index from the list.
     *
     * @param index The index of the priority to remove.
     */
    public static void removePriority(int index) {
        if (index >= 0 && index < priorityList.size()) {
            priorityList.remove(index);
        }
    }

    /**
     * Returns a list of all the priorities currently stored.
     *
     * @return A list of all event priorities.
     */
    public static ArrayList<String> getAllPriorities() {
        return priorityList;
    }

    /**
     * Clears all the priorities in the list.
     */
    public static void clearPriorities() {
        priorityList.clear();
    }

    /**
     * Loads the priorities from storage into the list.
     *
     * @param savedPriorities A list of priorities to load into the list.
     */
    public static void loadFromStorage(List<String> savedPriorities) {
        priorityList.clear();
        priorityList.addAll(savedPriorities);
    }
}
