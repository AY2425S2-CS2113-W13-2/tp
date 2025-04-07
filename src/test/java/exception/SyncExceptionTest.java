package exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SyncExceptionTest {

    private ParticipantManager participantManager;

    @BeforeEach
    public void setUp() throws SyncException {
        UI ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
    }

    @Test
    public void testInvalidCommandErrorMessageNoUserLoggedIn() {
        participantManager.setCurrentUser(null);

        String command = "add";
        String expectedMessage = "   (╯°□°)╯︵ OOPS!!! Invalid command: add\n" +
                "   Please enter 'login' to login first.";

        String actualMessage = SyncException.invalidCommandErrorMessage(command, participantManager);

        assertEquals(expectedMessage, actualMessage, "The error message should match when no " +
                "user is logged in.");
    }

    @Test
    public void testInvalidCommandErrorMessageUserLoggedIn() {
        Participant participant = new Participant("John", "password", Participant.AccessLevel.ADMIN);
        participantManager.setCurrentUser(participant);

        String command = "add";
        String expectedMessage = "   (╯°□°)╯︵ OOPS!!! Invalid command: add\n" +
                "   Please enter a valid command or help to see all commands. \n" +
                "   Example: `add` or `list` or `edit`.";

        String actualMessage = SyncException.invalidCommandErrorMessage(command, participantManager);

        assertEquals(expectedMessage, actualMessage, "The error message should match when a user is " +
                "logged in.");
    }

    @Test
    public void testInvalidEventIndexErrorMessage() {
        String expectedMessage = "   (╯°□°)╯︵ Invalid event index! Enter your command word to try again.\n" +
                "   Please provide a valid event index.\n";

        String actualMessage = SyncException.invalidEventIndexErrorMessage();

        assertEquals(expectedMessage, actualMessage, "The event index error message should be as expected.");
    }

    @Test
    public void testInvalidEventDetailsErrorMessage() {
        String expectedMessage = "   (╯°□°)╯︵ OOPS!!! Invalid event details! Enter your command word to try again.\n" +
                "   Please provide valid event details in the format: \n" +
                "   `add Event Name | Start Date | End Date | Location | Description`.\n" +
                "   Example: `add Meeting | 2025-05-10 14:00 | 2025-05-10 15:00 | Room 101 | Discuss project`.";

        String actualMessage = SyncException.invalidEventDetailsErrorMessage();

        assertEquals(expectedMessage, actualMessage, "The event details error message should be as expected.");
    }

    @Test
    public void testInvalidFilterInputErrorMessage() {
        String expectedMessage = "   (╯°□°)╯︵ Invalid filter input! Enter 'filter' to try again.\n" +
                "   Please provide valid filter inputs in the format: \n" +
                "   `filter {lower priority bound} {upper priority bound}`.\n";

        String actualMessage = SyncException.invalidFilterInputErrorMessage();

        assertEquals(expectedMessage, actualMessage, "The filter input error message should be as expected.");
    }

    @Test
    public void testInvalidBoundErrorMessage() {
        String expectedMessage = "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please follow this format: filter {LOWER} {HIGHER}.\n" +
                "   Be careful that the lower priority cannot be higher than upper priority.\n" +
                "   Example: filter LOW MEDIUM";

        String actualMessage = SyncException.invalidBoundErrorMessage();

        assertEquals(expectedMessage, actualMessage, "The bound error message should be as expected.");
    }

    @Test
    public void testInvalidPriorityFilterErrorMessage() {
        String expectedMessage = "   (╯°□°)╯︵ Invalid bound values for filtering!\n" +
                "   Please provide either one or two priority levels for filtering!\n" +
                "   If you provide two priority levels, they will serve as the lower bound and upper bound." +
                "   Be careful that the lower priority cannot be higher than upper priority.\n" +
                "   Example: filter MEDIUM" +
                "   Example: filter LOW MEDIUM";

        String actualMessage = SyncException.invalidPriorityFilterErrorMessage();

        assertEquals(expectedMessage, actualMessage, "The priority filter error " +
                "message should be as expected.");
    }

    @Test
    public void testInvalidDateTimeFormatMessage() {
        String expectedMessage = "❌ Invalid start time format! Please enter in YYYY-MM-DD HH:MM format. " +
                "\"Please enter any command word and try again.";

        String actualMessage = SyncException.invalidDateTimeFormatMessage("start");

        assertEquals(expectedMessage, actualMessage, "The datetime format error " +
                "message should be as expected.");
    }

    @Test
    public void testStartTimeAfterEndTimeMessage() {
        String expectedMessage = "❌ Start time cannot be after current end time.";

        String actualMessage = SyncException.startTimeAfterEndTimeMessage();

        assertEquals(expectedMessage, actualMessage, "The start time after end time error " +
                "message should be as expected.");
    }

    @Test
    public void testEndTimeBeforeStartTimeMessage() {
        String expectedMessage = "❌ End time cannot be before current start time.";

        String actualMessage = SyncException.endTimeBeforeStartTimeMessage();

        assertEquals(expectedMessage, actualMessage, "The end time before start time error " +
                "message should be as expected.");
    }

    @Test
    public void testParticipantUnavailableDuringEditError() {
        String name = "John Doe";
        String expectedMessage = "❌ John Doe is not available from 2025-05-10T10:00 to 2025-05-10T12:00";

        String actualMessage = SyncException.participantUnavailableDuringEditError(name,
                LocalDateTime.of(2025, 5, 10, 10, 0), LocalDateTime.of(2025,
                        5, 10, 12, 0));

        assertEquals(expectedMessage, actualMessage, "The participant unavailable " +
                "during edit error message should be as expected.");
    }

    @Test
    public void testParticipantConflictMessage() {
        String expectedMessage = "❌ One or more participants are unavailable for the new time.";

        String actualMessage = SyncException.participantConflictMessage();

        assertEquals(expectedMessage, actualMessage, "The participant conflict message should be as " +
                "expected.");
    }
}
