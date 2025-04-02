import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import event.Event;
import event.EventManager;
import label.Priority;
import command.FilterCommand;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class PriorityFilterTest {

    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;
    private ArrayList<Event> testEvents;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        System.setOut(new PrintStream(outputStreamCaptor));

        UserStorage userStorage = new UserStorage("test-users.txt");
        Storage storage = new Storage("test-filter.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        Priority.clearPriorities();

        eventManager.addEvent(new Event("Low Priority",
                LocalDateTime.of(2025, 4, 1, 10, 0),
                LocalDateTime.of(2025, 4, 1, 12, 0), "Home", "Chores"));
        Priority.addPriority("LOW");

        eventManager.addEvent(new Event("Medium Priority",
                LocalDateTime.of(2025, 4, 2, 14, 0),
                LocalDateTime.of(2025, 4, 2, 16, 0), "Office", "Meeting"));
        Priority.addPriority("MEDIUM");

        eventManager.addEvent(new Event("High Priority",
                LocalDateTime.of(2025, 4, 3, 9, 0),
                LocalDateTime.of(2025, 4, 3, 10, 0), "Remote", "Deadline"));
        Priority.addPriority("HIGH");
    }

    @Test
    void testFilterLowToMedium() throws SyncException {
        FilterCommand cmd = new FilterCommand(1, 2);
        cmd.execute(eventManager, ui, participantManager);

        String out = outputStreamCaptor.toString();
        assertTrue(out.contains("Low Priority"));
        assertTrue(out.contains("Medium Priority"));
        assertFalse(out.contains("High Priority"));
    }

    @Test
    void testFilterHighOnly() throws SyncException {
        FilterCommand cmd = new FilterCommand(3, 3);
        cmd.execute(eventManager, ui, participantManager);

        String out = outputStreamCaptor.toString();
        assertTrue(out.contains("High Priority"));
        assertFalse(out.contains("Low Priority"));
        assertFalse(out.contains("Medium Priority"));
    }
}