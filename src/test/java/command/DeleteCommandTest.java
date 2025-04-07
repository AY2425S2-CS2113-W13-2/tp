package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import event.Event;
import event.EventManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logger.EventSyncLogger;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

class DeleteCommandTest {

    private EventManager eventManager;
    private UI ui;
    private ParticipantManager participantManager;
    private UserStorage userStorage;
    private Storage eventStorage;

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    void setUp() throws SyncException {
        InputStream testInput = new ByteArrayInputStream("".getBytes());
        ui = new UI();
        ui.setScanner(new Scanner(testInput));
        userStorage = new UserStorage("./data/test-users.txt");
        eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, new UserStorage("./data/test-users.txt"));
    }

    @Test
    void testDeleteEventSuccessfully() throws SyncException {
        Event event = new Event("Event",
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0),
                "Lab", "na");
        eventManager.addEvent(event);

        simulateInput("yes");

        DeleteCommand command = new DeleteCommand(0);
        command.execute(eventManager, ui, participantManager);

        assertEquals(0, eventManager.getEvents().size(), "Event should be deleted");
    }

    @Test
    void testCancelDeletion() throws SyncException {
        Event event = new Event("Event",
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0),
                "Lab", "na");
        eventManager.addEvent(event);

        simulateInput("no");

        DeleteCommand command = new DeleteCommand(0);
        command.execute(eventManager, ui, participantManager);

        assertEquals(1, eventManager.getEvents().size(), "Event should NOT be deleted");
    }

    @Test
    void testNoEventsToDelete() throws SyncException {
        simulateInput("yes");

        DeleteCommand command = new DeleteCommand(0);
        assertThrows(SyncException.class,
                () -> command.execute(eventManager, ui, participantManager));
    }

    @Test
    void testInvalidNegativeIndex() {
        DeleteCommand command = new DeleteCommand(-1);
        assertThrows(SyncException.class,
                () -> command.execute(eventManager, ui, participantManager));
    }

    @Test
    void testIndexOutOfBounds() throws SyncException {
        Event event = new Event("Event",
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0),
                "Lab", "na");
        eventManager.addEvent(event);

        DeleteCommand command = new DeleteCommand(2); // Only 1 event at index 0
        SyncException e = assertThrows(SyncException.class,
                () -> command.execute(eventManager, ui, participantManager));
        assertEquals("Invalid event index. Please enter a valid index.", e.getMessage());
    }

    private void simulateInput(String input) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
    }
}
