package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.ListParticipantsCommand;
import exception.SyncException;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;


public class ListParticipantsCommandFactoryTest {

    private ListParticipantsCommandFactory factory;
    private ParticipantManager participantManager;
    private UI ui;
    private EventManager eventManager;

    private InputStream originalSystemIn;

    @BeforeEach
    void setUp() throws SyncException {
        originalSystemIn = System.in;
        ui = new UI();
        ui.setScanner(new Scanner(System.in));
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage storage = new Storage("./data/test-events.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
        factory = new ListParticipantsCommandFactory(ui, eventManager, participantManager);
    }

    @Test
    public void testCreateCommand_NoUserLoggedIn_ThrowsSyncException() {
        participantManager.setCurrentUser(null);
        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("You are not logged in. Enter 'login' to log in first.", exception.getMessage());
    }

    @Test
    public void testCreateCommand_UserLoggedIn_ReturnsListParticipantsCommand() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, new ArrayList<>());
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);

        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        String simulatedInput = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        Command command = factory.createCommand();
        assertTrue(command instanceof ListParticipantsCommand);
    }

    @Test
    public void testCreateCommand_InvalidEventIndex_ThrowsSyncException() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123",
                Participant.AccessLevel.ADMIN, new ArrayList<>());
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);

        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        String simulatedInput = "invalid\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("Invalid event index. Please enter a number.", exception.getMessage());
    }

    @Test
    public void testCreateCommand_NoEventsAvailable_ShowsMessage() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, new ArrayList<>());
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);
        String simulatedInput = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
        Command command = factory.createCommand();
        assertTrue(command instanceof ListParticipantsCommand);
    }
}
