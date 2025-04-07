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

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

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

        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage storage = new Storage("./data/test-events.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        Priority.clearPriorities();
        String simulatedInput = "HIGH\nMEDIUM\nLOW\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        Event low =  new Event("Low Priority",
                LocalDateTime.of(2025, 4, 1, 10, 0),
                LocalDateTime.of(2025, 4, 1, 12, 0),
                "Home", "Chores");
        Event middle = new Event("Medium Priority",
                LocalDateTime.of(2025, 4, 2, 14, 0),
                LocalDateTime.of(2025, 4, 2, 16, 0),
                "Office", "Meeting");
         Event high = new Event("High Priority",
                 LocalDateTime.of(2025, 4, 3, 9, 0),
                 LocalDateTime.of(2025, 4, 3, 10, 0),
                 "Remote", "Deadline");

        eventManager.addEvent(low);
        eventManager.addEvent(middle);
        eventManager.addEvent(high);
    }

    @Test
    void testFilterLowToMedium() throws SyncException {
        FilterCommand cmd = new FilterCommand(1, 2);
        cmd.execute(eventManager, ui, participantManager);

        String out = outputStreamCaptor.toString();
        assertTrue(out.contains("Low Priority"));
        assertTrue(out.contains("Medium Priority"));
    }

    @Test
    void testFilterHighOnly() throws SyncException {
        FilterCommand cmd = new FilterCommand(3, 3);
        cmd.execute(eventManager, ui, participantManager);

        String out = outputStreamCaptor.toString();
        assertTrue(out.contains("High Priority"));
    }
}
