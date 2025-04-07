package parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.SyncException;
import participant.Participant;
import ui.UI;

/**
 * Parses user commands and handles input validation and formatting.
 */
public final class CommandParser {
    private static UI ui = new UI();
    private static final Logger logger = Logger.getLogger(CommandParser.class.getName());

    /**
     * Injects a UI instance for input/output operations.
     *
     * @param ui The UI instance to use.
     */
    public void setUi(UI ui) {
        assert ui != null : "UI cannot be null";
        CommandParser.ui = ui;
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Parses a date-time string into a {@link LocalDateTime} object.
     *
     * @param dateStr The input date-time string.
     * @return The parsed LocalDateTime.
     * @throws SyncException If the format is invalid.
     */
    public static LocalDateTime parseDateTime(String dateStr) throws SyncException {
        logger.info("Parsing date-time: " + dateStr);
        assert dateStr != null && !dateStr.isBlank() : "Date string cannot be null or blank";
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeException e) {
            logger.log(Level.WARNING, "Invalid date-time format: " + dateStr, e);
            throw new SyncException("Invalid date-time format. Use yyyy-MM-dd HH:mm. " +
                    "Enter any command word to continue.");
        }
    }

    /**
     * Splits input for 'add' command using '|' as delimiter.
     *
     * @param input The raw input string.
     * @return An array containing the five components of the add command.
     * @throws SyncException If the input does not have exactly five parts.
     */
    public static String[] splitAddCommandInput(String input) throws SyncException {
        logger.info("Splitting add command input: " + input);
        assert input != null && !input.isBlank() : "Add command input cannot be null or blank";
        String[] parts = input.split("\\|");
        if (parts.length != 5) {
            logger.warning("Invalid add command input. Found " + parts.length + " parts.");
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
        return parts;
    }

    /**
     * Asks the user to input an access level and returns the corresponding enum.
     *
     * @return The parsed access level.
     * @throws SyncException If the input is invalid or user cancels.
     */
    public static Participant.AccessLevel askAccessLevel() throws SyncException {
        ui.showMessage("Enter participant's access level (1 for Admin, 2 for Member) (or type 'exit' to cancel): ");
        String input = ui.readLine().trim();
        ui.checkForExit(input);
        logger.info("Access level input: " + input);
        assert input != null : "User input for access level should not be null";
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
            logger.warning("Invalid access level input: " + input);
            throw new SyncException("You can only enter 1 or 2. Enter 'create' to try again.");
        }
    }
}
