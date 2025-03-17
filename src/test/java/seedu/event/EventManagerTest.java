package seedu.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.UI;
import exception.SyncException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class EventManagerTest {
    private EventManager eventManager;
    private UI mockUI;
    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        mockUI = new UI();
        eventManager = new EventManager(new ArrayList<>(), mockUI);
        formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    }

    @Test
    void testAddEvent() throws SyncException {
        Event event = new Event("Team Meeting", LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter), "Conference Room", "Team Meeting");
        eventManager.addEvent(event);

        assertEquals(1, eventManager.size());
        assertEquals(event, eventManager.getEvent(0));
    }

    @Test
    void testGetEventValidIndex() throws SyncException {
        Event event = new Event("Team Meeting", LocalDateTime.parse("2025/05/10 14:00", formatter),
            LocalDateTime.parse("2025/05/10 16:00", formatter), "Conference Room", "Team Meeting");
        eventManager.addEvent(event);

        Event fetchedEvent = eventManager.getEvent(0);
        assertEquals(event, fetchedEvent);
    }

    @Test
    void testGetEventInvalidIndex() {
        assertThrows(SyncException.class, () -> {
            eventManager.getEvent(100);
        });
    }

    @Test
    void testSize() {
        Event event1 = new Event("Team Meeting", LocalDateTime.parse("2025/05/10 14:00", formatter),
            LocalDateTime.parse("2025/05/10 16:00", formatter), "Conference Room", "Team Meeting");
        Event event2 = new Event("Team Meeting2", LocalDateTime.parse("2025/05/11 14:00", formatter),
            LocalDateTime.parse("2025/05/11 16:00", formatter), "Conference Room", "Team Meeting");
        eventManager.addEvent(event1);
        eventManager.addEvent(event2);

        assertEquals(2, eventManager.size());
    }
}
