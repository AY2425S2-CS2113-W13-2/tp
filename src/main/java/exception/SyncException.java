package exception;

public class SyncException extends Exception {
    public SyncException(String message) {
        super(message);
    }

    public static String invalidCommandErrorMessage(String command) {
        return "   (╯°□°)╯︵ OOPS!!! Invalid command: " + command + "\n" +
                "   Please enter a valid command. \n" +
                "   Example: `add` or `list` or `edit`.";
    }

    public static String invalidEventIndexErrorMessage() {
        return "   (╯°□°)╯︵ Invalid event index!\n" +
                "   Please provide a valid event index.\n" +
                "   Example: `delete 2` or `edit 3`.";
    }

    public static String invalidEventDetailsErrorMessage() {
        return "   (╯°□°)╯︵ OOPS!!! Invalid event details!\n" +
                "   Please provide valid event details in the format: \n" +
                "   `add Event Name | Start Date | End Date | Location | Description`.\n" +
                "   Example: `add Meeting | 2025/05/10 14:00 | 2025/05/10 15:00 | Room 101 | Discuss project`.";
    }

    public static String invalidFilterInputErrorMessage() {
        return "   (╯°□°)╯︵ Invalid filter input!\n" +
                "   Please provide valid filter inputs in the format: \n" +
                "   `filter {lower priority bound} {upper priority bound}`.\n" +
                "   Example: `filter 1 2`.";
    }

    public static String eventNotFoundErrorMessage() {
        return "   (╯°□°)╯︵ Event not found!\n" +
                "   The event you are trying to delete or edit does not exist.\n" +
                "   Please ensure you are using the correct event index.";
    }

    public static String invalidDuplicateEventIndexErrorMessage() {
        return "   (╯°□°)╯︵ Invalid event index for duplication!\n" +
                "   Please provide a valid event index to duplicate.\n" +
                "   Example: `duplicate 2 New Event Name`.";
    }

    public static String invalidBoundErrorMessage() {
        return "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please provide integers as priority levels.\n" +
                "   Low = 1; Medium = 2; High = 3.";
    }


}
