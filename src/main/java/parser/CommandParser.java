package parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import exception.SyncException;
import participant.Participant;
import ui.UI;

/**
 * This class is responsible for parsing and processing user input commands related to event synchronization.
 * It handles parsing of date-time strings, splitting commands, and prompting for participant access levels.
 * It also throws custom exceptions in case of invalid input.
 */
public final class CommandParser {

    /**
     * The UI instance used for interacting with the user.
     */
    private static UI ui = new UI();

    /**
     * Sets the UI instance used for user interactions.
     *
     * @param ui The UI instance to be set.
     */
    public void setUi(UI ui) {
        this.ui = ui;
    }

    /**
     * The date-time formatter used for parsing date-time strings in the format "yyyy-MM-dd HH:mm".
     */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Parses a date-time string and converts it into a LocalDateTime object.
     *
     * @param dateStr The date-time string to be parsed.
     * @return The corresponding LocalDateTime object.
     * @throws SyncException If the input date-time format is invalid.
     */
    public static LocalDateTime parseDateTime(String dateStr) throws SyncException {
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeException e) {
            throw new SyncException("Invalid date-time format. Use yyyy-MM-dd HH:mm. " +
                    "Enter any command word to continue.");
        }
    }

    /**
     * Splits the user input for adding an event into its components.
     *
     * @param input The input string to be split.
     * @return An array of strings representing the event components.
     * @throws SyncException If the input does not have exactly 5 parts.
     */
    public static String[] splitAddCommandInput(String input) throws SyncException {
        String[] parts = input.split("\\|");
        if (parts.length != 5) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
        return parts;
    }

    /**
     * Prompts the user to input the access level for a participant.
     *
     * @return The access level chosen by the user (either ADMIN or MEMBER).
     * @throws SyncException If the input is invalid or the user does not enter a valid number.
     */
    public static Participant.AccessLevel askAccessLevel() throws SyncException {
        ui.showMessage("Enter participant's access level (1 for Admin, 2 for Member) (or type 'exit' to cancel): ");
        String input = ui.readLine().trim();
        ui.checkForExit(input);
        try {
            int choice = Integer.parseInt(input);
            if (choice == 1) {
                return Participant.AccessLevel.ADMIN;
            } else if (choice == 2) {
                return Participant.AccessLevel.MEMBER;
            } else {
                ui.showMessage("Invalid choice. Defaulting to MEMBER.");
                return Participant.AccessLevel.MEMBER;
            }
        } catch (NumberFormatException e) {
            throw new SyncException("You can only enter 1 or 2. Enter 'create' to try again.");
        }
    }
}
