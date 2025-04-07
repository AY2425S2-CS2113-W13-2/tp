package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.SyncException;
import event.Event;
import event.EventManager;
import logger.EventSyncLogger;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

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
    public void testCreateCommand_NoUserLoggedIn_ThrowsSyncException() {
        participantManager.setCurrentUser(null);
        factory = new ListParticipantsCommandFactory(ui, eventManager, participantManager);
        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("You are not logged in. Enter 'login' to log in first.", exception.getMessage());
    }

    @Test
    public void testCreateCommand_NoEventsAvailable_ThrowsSyncException() throws SyncException {
        Participant testUser = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, new ArrayList<>());

        // Try to add the user, ignore if already exists
        try {
            participantManager.addNewUser(testUser);
        } catch (SyncException e) {
            // Ignore if user already exists
        }

        participantManager.setCurrentUser(testUser);

        UI mockUi = new UI() {
            @Override
            public void showMessage(String message) {
                // Suppress message output
            }

            @Override
            public String readLine() {
                return "exit";
            }
        };

        factory = new ListParticipantsCommandFactory(mockUi, eventManager, participantManager);

        SyncException exception = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("No events available.", exception.getMessage());
    }
}
