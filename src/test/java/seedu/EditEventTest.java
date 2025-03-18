package seedu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import event.Event;
import event.EventManager;
import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class EditEventTest {

    private EventManager eventManager;
    private UI ui;
    private Event event;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();  // Real UI object (no mocking)
        eventManager = new EventManager();
        event = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                LocalDateTime.parse("2025/05/10 16:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                "Conference Room", "Discuss project updates");
        eventManager.addEvent(event);
    }

    @Test
    void testEditEventName() throws SyncException {
        Event updatedEvent = new Event("Updated Meeting",
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getDescription());
        eventManager.updateEvent(0, updatedEvent);
        assertEquals("Updated Meeting", eventManager.getEvent(0).getName());
    }

    @Test
    void testEditEventStartTime() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                LocalDateTime.parse("2025-05-10 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getEndTime(),
                event.getLocation(),
                event.getDescription());
        eventManager.updateEvent(0, updatedEvent);
        assertEquals(LocalDateTime.parse("2025-05-10 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                eventManager.getEvent(0).getStartTime());
    }

    @Test
    void testEditEventEndTime() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                LocalDateTime.parse("2025-05-10 18:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getLocation(),
                event.getDescription());
        eventManager.updateEvent(0, updatedEvent);
        assertEquals(LocalDateTime.parse("2025-05-10 18:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                eventManager.getEvent(0).getEndTime());
    }

    @Test
    void testEditEventLocation() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                "New Location",
                event.getDescription());
        eventManager.updateEvent(0, updatedEvent);
        assertEquals("New Location", eventManager.getEvent(0).getLocation());
    }

    @Test
    void testEditEventDescription() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                "Updated description");
        eventManager.updateEvent(0, updatedEvent);
        assertEquals("Updated description", eventManager.getEvent(0).getDescription());
    }

    @Test
    void testInvalidEventIndex() {
        Event updatedEvent = new Event(
                "Invalid Event",
                event.getStartTime(),
                event.getEndTime(),
                "Invalid Location",
                "Invalid description");
        assertThrows(SyncException.class, () -> {
            eventManager.updateEvent(100, updatedEvent);  // index 100 is out of bounds
        });
    }
}
