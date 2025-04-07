package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.SyncException;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage storage = new Storage("./data/test-events.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalSystemIn);
    }

    @Test
    public void testCreateCommandNoUserLoggedInThrowsSyncException() {
        participantManager.setCurrentUser(null);
        factory = new ListParticipantsCommandFactory(ui, eventManager, participantManager);
        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("You are not logged in. Enter 'login' to log in first.", exception.getMessage());
    }

    @Test
    public void testCreateCommandUserLoggedInReturnsListParticipantsCommand() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123",
                Participant.AccessLevel.ADMIN, new ArrayList<>());
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);

        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime, "Test Location",
                "Test Description");
        eventManager.addEvent(event);

        UI mockUi = new UI() {
            @Override
            public String readLine() {
                return "exit";
            }
        };

        factory = new ListParticipantsCommandFactory(mockUi, eventManager, participantManager);

        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("Operation cancelled.", exception.getMessage());
    }

    @Test
    public void testCreateCommandInvalidEventIndexThrowsSyncException() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123",
                Participant.AccessLevel.ADMIN, new ArrayList<>());
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);

        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime, "Test Location",
                "Test Description");
        eventManager.addEvent(event);

        UI mockUi = new UI() {
            private int callCount = 0;

            @Override
            public String readLine() {
                callCount++;
                if (callCount == 1) {
                    return "invalid";
                } else {
                    return "exit";
                }
            }

            @Override
            public void showMessage(String message) {
            }
        };

        factory = new ListParticipantsCommandFactory(mockUi, eventManager, participantManager);

        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("Operation cancelled.", exception.getMessage());
    }

    @Test
    public void testCreateCommandNoEventsAvailableThrowsSyncException() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123",
                Participant.AccessLevel.ADMIN, new ArrayList<>());
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);

        UI mockUi = new UI() {
            @Override
            public void showMessage(String message) {
            }
        };

        factory = new ListParticipantsCommandFactory(mockUi, eventManager, participantManager);

        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("No events available.", exception.getMessage());
    }
}
