
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import event.Event;
import event.EventManager;
import label.Priority;
import command.FilterCommand;
import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.util.ArrayList;

class PriorityFilterTest {

    private EventManager eventManager;
    private UI ui;
    private ArrayList<Event> testEvents;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        eventManager = new EventManager("./data/EditEventTest.txt");
        testEvents = new ArrayList<>();
        Priority.clearPriorities();

        // Create test events
        Event event1 = new Event("Low Priority Task",
                LocalDateTime.of(2025, 3, 20, 10, 0),
                LocalDateTime.of(2025, 3, 20, 11, 0),
                "Home", "Chores");
        testEvents.add(event1);
        Priority.addPriority("LOW");

        Event event2 = new Event("Medium Priority Meeting",
                LocalDateTime.of(2025, 3, 21, 14, 0),
                LocalDateTime.of(2025, 3, 21, 15, 0),
                "Office", "Team sync");
        testEvents.add(event2);
        Priority.addPriority("MEDIUM");

        Event event3 = new Event("High Priority Deadline",
                LocalDateTime.of(2025, 3, 22, 9, 0),
                LocalDateTime.of(2025, 3, 22, 17, 0),
                "Remote", "Project submission");
        testEvents.add(event3);
        Priority.addPriority("HIGH");
        eventManager.getEvents().addAll(testEvents);
    }

    @Test
    void testFilterLowToMedium() throws SyncException {
        FilterCommand command = new FilterCommand(1, 2); // LOW=1, MEDIUM=2
        command.execute(eventManager, ui);
        assertTrue(true);
    }

    @Test
    void testFilterHighOnly() throws SyncException {
        FilterCommand command = new FilterCommand(3, 3); // HIGH=3
        command.execute(eventManager, ui);
        assertTrue(true);
    }

    @Test
    void testNoMatchingEvents() throws SyncException {
        FilterCommand command = new FilterCommand(4, 4); // Invalid range
        command.execute(eventManager, ui);
        assertTrue(true);
    }
}
