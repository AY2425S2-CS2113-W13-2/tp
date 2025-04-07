package exception;

import java.time.LocalDateTime;

import participant.ParticipantManager;

/**
 * This class defines custom exceptions used to handle synchronization and validation errors
 * related to commands, event details, and participant availability in the system.
 * It provides various static methods to generate specific error messages for different types
 * of synchronization issues encountered during the operation of the event management system.
 */
public class SyncException extends Exception {

    /**
     * Constructs a new SyncException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     */
    public SyncException(String message) {
        super(message);
    }

    /**
     * Returns an error message for invalid commands entered by the user.
     *
     * @param command The invalid command entered by the user.
     * @param participantManager The ParticipantManager instance to check for login status.
     * @return A string message with the error details.
     */
    public static String invalidCommandErrorMessage(String command, ParticipantManager participantManager) {
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
     * Returns an error message for an invalid event index input.
     *
     * @return A string message with the error details.
     */
    public static String invalidEventIndexErrorMessage() {
        return "   (╯°□°)╯︵ Invalid event index! Enter your command word to try again.\n" +
                "   Please provide a valid event index.\n";
    }

    /**
     * Returns an error message for invalid event details entered by the user.
     *
     * @return A string message with the error details.
     */
    public static String invalidEventDetailsErrorMessage() {
        return "   (╯°□°)╯︵ OOPS!!! Invalid event details! Enter your command word to try again.\n" +
                "   Please provide valid event details in the format: \n" +
                "   `add Event Name | Start Date | End Date | Location | Description`.\n" +
                "   Example: `add Meeting | 2025-05-10 14:00 | 2025-05-10 15:00 | Room 101 | Discuss project`.";
    }

    /**
     * Returns an error message for invalid filter input for event filtering.
     *
     * @return A string message with the error details.
     */
    public static String invalidFilterInputErrorMessage() {
        return "   (╯°□°)╯︵ Invalid filter input! Enter 'filter' to try again.\n" +
                "   Please provide valid filter inputs in the format: \n" +
                "   `filter {lower priority bound} {upper priority bound}`.\n";
    }

    /**
     * Returns an error message for invalid bound values used during event filtering.
     *
     * @return A string message with the error details.
     */
    public static String invalidBoundErrorMessage() {
        return "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please follow this format: filter {LOWER} {HIGHER}.\n" +
                "   Be careful that the lower priority cannot be higher than upper priority.\n" +
                "   Example: filter LOW MEDIUM";
    }

    /**
     * Returns an error message for invalid priority range used during event filtering.
     *
     * @return A string message with the error details.
     */
    public static String invalidPriorityFilterErrorMessage() {
        return "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please provide either one or two priority levels for filtering!\n" +
                "   If you provide two priority levels, they will serve as the lower bound and upper bound." +
                "   Be careful that the lower priority cannot be higher than upper priority.\n" +
                "   Example: filter MEDIUM" +
                "   Example: filter LOW MEDIUM";
    }

    /**
     * Returns an error message for an invalid date or time format entered by the user.
     *
     * @param type The type of time input (either start time or end time).
     * @return A string message with the error details.
     */
    public static String invalidDateTimeFormatMessage(String type) {
        return "❌ Invalid " + type + " time format! Please enter in YYYY-MM-DD HH:MM format. " +
                "\"Please enter any command word and try again.";
    }

    /**
     * Returns an error message if the event start time is after the end time.
     *
     * @return A string message with the error details.
     */
    public static String startTimeAfterEndTimeMessage() {
        return "❌ Start time cannot be after current end time.";
    }

    /**
     * Returns an error message if the event end time is before the start time.
     *
     * @return A string message with the error details.
     */
    public static String endTimeBeforeStartTimeMessage() {
        return "❌ End time cannot be before current start time.";
    }

    /**
     * Returns an error message if a participant is unavailable during the specified time range.
     *
     * @param name The name of the participant.
     * @param start The start time of the event.
     * @param end The end time of the event.
     * @return A string message with the error details.
     */
    public static String participantUnavailableDuringEditError(String name, LocalDateTime start, LocalDateTime end) {
        return "❌ " + name + " is not available from " + start + " to " + end;
    }

    /**
     * Returns an error message if one or more participants are unavailable for the new event time.
     *
     * @return A string message with the error details.
     */
    public static String participantConflictMessage() {
        return "❌ One or more participants are unavailable for the new time.";
    }
}
