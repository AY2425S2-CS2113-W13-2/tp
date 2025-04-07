package command;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import event.Event;
import event.EventManager;
import logger.EventSyncLogger;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import ui.UI;
import exception.SyncException;
import storage.UserStorage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class LogOutCommandTest {

    private ParticipantManager participantManager;
    private UI ui;
    private ByteArrayOutputStream outputStream;
    private EventManager eventManager;

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));

        Participant testUser = new Participant("john_doe", "password123",
                Participant.AccessLevel.ADMIN, availableTimes);
        participantManager.addNewUser(testUser);
        participantManager.setCurrentUser(testUser);
        EventManager eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        Event event = new Event("Test Event",
                LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0),
                "Lab", "Test Desc");

        outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testLogOutCommand() throws SyncException {
        LogOutCommand logOutCommand = new LogOutCommand();
        logOutCommand.execute(eventManager, ui, participantManager);
        assertTrue(outputStream.toString().contains("has logged out"));

        assertNull(participantManager.getCurrentUser());
    }
}
