package label;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ui.UI;

/**
 * Utility class for managing event priorities.
 * Provides methods for validating, normalizing, and manipulating priority levels.
 */
public class Priority {
    private static final Logger LOGGER = Logger.getLogger(Priority.class.getName());

    // Predefined priority levels
    public static final String HIGH = "HIGH";
    public static final String MEDIUM = "MEDIUM";
    public static final String LOW = "LOW";

    // Internal storage for priorities
    private static final ArrayList<String> priorityList = new ArrayList<>();
    private static final UI ui = new UI();

    /**
     * Gets the numeric value associated with a priority level.
     *
     * @param priority The priority level to convert
     * @return Numeric value of the priority (HIGH: 3, MEDIUM: 2, LOW: 1, Invalid: 0)
     */
    public static int getValue(String priority) {
        assert priority != null : "Priority input cannot be null";

        String normalizedPriority = priority.toUpperCase();
        int value;
        switch (normalizedPriority) {
        case HIGH:
            value = 3;
            break;
        case MEDIUM:
            value = 2;
            break;
        case LOW:
            value = 1;
            break;
        default:
            LOGGER.log(Level.WARNING, "Invalid priority value: {0}", priority);
            value = 0;
        }

        LOGGER.log(Level.FINE, "Priority {0} converted to value: {1}", new Object[]{priority, value});
        return value;
    }

    /**
     * Validates if the input is a valid priority level.
     *
     * @param input The priority level to validate
     * @return true if the input is a valid priority, false otherwise
     */
    public static boolean isValid(String input) {
        if (input == null) {
            LOGGER.log(Level.FINE, "Null input received for priority validation");
            return false;
        }

        String allUpper = input.trim().toUpperCase();
        boolean isValid = allUpper.equals(HIGH) || allUpper.equals(MEDIUM) || allUpper.equals(LOW);

        LOGGER.log(Level.FINE, "Priority validation for input {0}: {1}", new Object[]{input, isValid});
        return isValid;
    }

    /**
     * Normalizes the priority input to uppercase.
     *
     * @param input The priority input to normalize
     * @return Normalized priority in uppercase
     */
    public static String normalize(String input) {
        assert input != null : "Input cannot be null for normalization";
        return input.trim().toUpperCase();
    }

    /**
     * Prompts user to input a valid priority level.
     *
     * @return Normalized priority input
     */
    public static String priorityInput() {
        ui.showMessage("Enter event priority (LOW, MEDIUM, HIGH): ");
        String input;
        try {
            input = ui.readLine().trim();
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.WARNING, "No input received, defaulting to LOW priority");
            return LOW;
        }

        while (!isValid(input)) {
            LOGGER.log(Level.INFO, "Invalid priority input: {0}", input);
            ui.showMessage("Invalid priority. Please enter LOW, MEDIUM, or HIGH.");
            ui.showMessage("Enter event priority (LOW, MEDIUM, HIGH): ");
            input = ui.readLine().trim();
        }

        String normalizedInput = normalize(input);
        LOGGER.log(Level.INFO, "Valid priority input received: {0}", normalizedInput);
        return normalizedInput;
    }

    /**
     * Adds a priority to the internal priority list.
     *
     * @param priority The priority to add
     */
    public static void addPriority(String priority) {
        LOGGER.log(Level.FINE, "Adding priority: {0}", priority);
        priorityList.add(priority);
    }

    /**
     * Updates a priority at the specified index.
     *
     * @param index The index to update
     * @param newPriority The new priority value
     */
    public static void updatePriority(int index, String newPriority) {
        assert isValid(newPriority) : "Cannot update with invalid priority";

        if (index >= 0 && index < priorityList.size()) {
            LOGGER.log(Level.FINE, "Updating priority at index {0} to {1}", new Object[]{index, newPriority});
            priorityList.set(index, newPriority);
        } else {
            LOGGER.log(Level.WARNING, "Invalid index for priority update: {0}", index);
        }
    }

    /**
     * Retrieves a priority at the specified index.
     *
     * @param index The index to retrieve
     * @return The priority at the given index, or LOW if index is invalid
     */
    public static String getPriority(int index) {
        if (index >= 0 && index < priorityList.size()) {
            return priorityList.get(index);
        }
        LOGGER.log(Level.WARNING, "Invalid index for priority retrieval: {0}", index);
        return LOW;
    }

    /**
     * Removes a priority at the specified index.
     *
     * @param index The index of the priority to remove
     */
    public static void removePriority(int index) {
        if (index >= 0 && index < priorityList.size()) {
            LOGGER.log(Level.FINE, "Removing priority at index: {0}", index);
            priorityList.remove(index);
        } else {
            LOGGER.log(Level.WARNING, "Invalid index for priority removal: {0}", index);
        }
    }

    /**
     * Returns a copy of all priorities in the list.
     *
     * @return ArrayList of all priorities
     */
    public static ArrayList<String> getAllPriorities() {
        return new ArrayList<>(priorityList);
    }

    /**
     * Clears all priorities from the list.
     */
    public static void clearPriorities() {
        LOGGER.log(Level.INFO, "Clearing all priorities");
        priorityList.clear();
    }

    /**
     * Loads priorities from storage.
     *
     * @param savedPriorities List of priorities to load
     */
    public static void loadFromStorage(List<String> savedPriorities) {
        assert savedPriorities != null : "Saved priorities list cannot be null";

        LOGGER.log(Level.INFO, "Loading priorities from storage, count: {0}", savedPriorities.size());
        priorityList.clear();
        priorityList.addAll(savedPriorities);
    }

    // Prevent instantiation
    private Priority() {
        throw new AssertionError("Cannot instantiate Priority utility class");
    }
}
