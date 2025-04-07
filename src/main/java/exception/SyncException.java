package exception;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import participant.ParticipantManager;

/**
 * SyncException is a custom exception class that provides
 * static error message generators for invalid commands and input formats.
 */
public class SyncException extends Exception {

    private static final Logger logger = Logger.getLogger(SyncException.class.getName());

    /**
     * Constructs a SyncException with the given message.
     *
     * @param message the error message
     */
    public SyncException(String message) {
        super(message);
        assert message != null : "Error message should not be null";
        logger.warning("SyncException created with message: " + message);
    }

    /**
     * Returns an error message for invalid commands, taking into account login state.
     *
     * @param command             the invalid command input
     * @param participantManager  the current participant manager
     * @return                    formatted error message
     */
    public static String invalidCommandErrorMessage(String command, ParticipantManager participantManager) {
        assert command != null : "Command should not be null";
        assert participantManager != null : "ParticipantManager should not be null";

        if (participantManager.getCurrentUser() == null) {
            return "   (╯°□°)╯︵ OOPS!!! Invalid command: " + command + "\n" +
                    "   Please enter 'login' to login first.";
        } else {
            return "   (╯°□°)╯︵ OOPS!!! Invalid command: " + command + "\n" +
                    "   Please enter a valid command or help to see all commands. \n" +
                    "   Example: `add` or `list` or `edit`.";
        }
    }

    /**
     * Returns error message for invalid event index.
     */
    public static String invalidEventIndexErrorMessage() {
        return "   (╯°□°)╯︵ Invalid event index! Enter your command word to try again.\n" +
                "   Please provide a valid event index.\n";
    }

    /**
     * Returns error message for invalid event details.
     */
    public static String invalidEventDetailsErrorMessage() {
        return "   (╯°□°)╯︵ OOPS!!! Invalid event details! Enter your command word to try again.\n" +
                "   Please provide valid event details in the format: \n" +
                "   `add Event Name | Start Date | End Date | Location | Description`.\n" +
                "   Example: `add Meeting | 2025-05-10 14:00 | 2025-05-10 15:00 | Room 101 | Discuss project`.";
    }

    /**
     * Returns error message for invalid filter input.
     */
    public static String invalidFilterInputErrorMessage() {
        return "   (╯°□°)╯︵ Invalid filter input! Enter 'filter' to try again.\n" +
                "   Please provide valid filter inputs in the format: \n" +
                "   `filter {lower priority bound} {upper priority bound}`.\n";
    }

    /**
     * Returns error message for invalid priority bounds.
     */
    public static String invalidBoundErrorMessage() {
        return "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please follow this format: filter {LOWER} {HIGHER}.\n" +
                "   Be careful that the lower priority cannot be higher than upper priority.\n" +
                "   Example: filter LOW MEDIUM";
    }

    /**
     * Returns error message for invalid priority filter values.
     */
    public static String invalidPriorityFilterErrorMessage() {
        return "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please provide either one or two priority levels for filtering!\n" +
                "   If you provide two priority levels, they will serve as the lower bound and upper bound.\n" +
                "   Be careful that the lower priority cannot be higher than upper priority.\n" +
                "   Example: filter MEDIUM\n" +
                "   Example: filter LOW MEDIUM";
    }

    /**
     * Returns error message for invalid datetime format.
     *
     * @param type the type of datetime (start or end)
     */
    public static String invalidDateTimeFormatMessage(String type) {
        assert type != null : "Datetime type should not be null";
        return "❌ Invalid " + type + " time format! Please enter in YYYY-MM-DD HH:MM format. " +
                "Please enter any command word and try again.";
    }

    /**
     * Returns error message when start time is after end time.
     */
    public static String startTimeAfterEndTimeMessage() {
        return "❌ Start time cannot be after current end time.";
    }

    /**
     * Returns error message when end time is before start time.
     */
    public static String endTimeBeforeStartTimeMessage() {
        return "❌ End time cannot be before current start time.";
    }

    /**
     * Returns error message when a participant is unavailable during the edit time range.
     *
     * @param name  name of participant
     * @param start start time
     * @param end   end time
     */
    public static String participantUnavailableDuringEditError(String name, LocalDateTime start, LocalDateTime end) {
        assert name != null : "Participant name should not be null";
        assert start != null && end != null : "Start and end time must not be null";
        return "❌ " + name + " is not available from " + start + " to " + end;
    }

    /**
     * Returns error message when there is a conflict in participant availability.
     */
    public static String participantConflictMessage() {
        return "❌ One or more participants are unavailable for the new time.";
    }
}
