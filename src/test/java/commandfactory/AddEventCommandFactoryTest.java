package commandfactory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.AddEventCommand;
import command.Command;
import event.Event;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import parser.CommandParser;

class AddEventCommandFactoryTest {

    private ParticipantManager participantManager;
    private UI ui;
    private AddEventCommandFactory addEventCommandFactory;

    @BeforeEach
    void setUp() {
        ui = new UI();
        participantManager = new ParticipantManager(new ArrayList<>(), ui, null); // Simplified ParticipantManager
        addEventCommandFactory = new AddEventCommandFactory(participantManager, ui);
    }

    @Test
    void testCreateCommandWhenNotLoggedIn() {
        participantManager.setCurrentUser(null);

        SyncException thrown = assertThrows(SyncException.class, () -> {
            addEventCommandFactory.createCommand();
        });
        assertEquals("You are not logged in. Please enter 'login' to login.", thrown.getMessage());
    }

    @Test
    void testCreateCommandWhenUserIsNotAdmin() {
        participantManager.setCurrentUser(new participant.Participant("john_doe", "password123",
                participant.Participant.AccessLevel.MEMBER, new ArrayList<>()));

        SyncException thrown = assertThrows(SyncException.class, () -> {
            addEventCommandFactory.createCommand();
        });
        assertEquals("Only admin can create events!", thrown.getMessage());
    }

    @Test
    void testCreateCommandWithValidAdminUser() throws SyncException {
        participantManager.setCurrentUser(new participant.Participant("admin", "adminpass", participant.Participant.AccessLevel.ADMIN, new ArrayList<>()));

        String validInput = "Test Event|2025-05-01 10:00|2025-05-01 12:00|Room A|Test Description" +
                "\n";
        InputStream in = new ByteArrayInputStream(validInput.getBytes());
        System.setIn(in);

        String[] parts = CommandParser.splitAddCommandInput(validInput); // Split input into parts

        LocalDateTime startTime = CommandParser.parseDateTime(parts[1]); // Parse the start time
        LocalDateTime endTime = CommandParser.parseDateTime(parts[2]); // Parse the end time

        assertEquals("Test Event", parts[0].trim());
        assertEquals(LocalDateTime.of(2025, 5, 1, 10, 0), startTime);
        assertEquals(LocalDateTime.of(2025, 5, 1, 12, 0), endTime);
        assertEquals("Room A", parts[3].trim());
        assertEquals("Test Description", parts[4].trim());
        Command command = addEventCommandFactory.createCommand();

        assertNotNull(command);
        assertTrue(command instanceof AddEventCommand);

        Event newEvent = ((AddEventCommand) command).getEvent();
        assertEquals("Test Event", newEvent.getName());
        assertEquals(LocalDateTime.of(2025, 5, 1, 10, 0), newEvent.getStartTime());
        assertEquals(LocalDateTime.of(2025, 5, 1, 12, 0), newEvent.getEndTime());
        assertEquals("Room A", newEvent.getLocation());
        assertEquals("Test Description", newEvent.getDescription());
    }
}
