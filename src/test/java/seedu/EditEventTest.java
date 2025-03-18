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
        // Simulate editing the event name
        Event updatedEvent = new Event("Updated Meeting",
            event.getStartTime(),
            event.getEndTime(),
            event.getLocation(),
            event.getDescription());

        // Update the event using the updateEvent method
        eventManager.updateEvent(0, updatedEvent);

        // Verify that the event's name was updated
        assertEquals("Updated Meeting", eventManager.getEvent(0).getName());
    }

    @Test
    void testEditEventStartTime() throws SyncException {
        // Simulate editing the start time of the event
        Event updatedEvent = new Event(
            event.getName(),
            LocalDateTime.parse("2025-05-10 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            event.getEndTime(),
            event.getLocation(),
            event.getDescription());

        // Update the event using the updateEvent method
        eventManager.updateEvent(0, updatedEvent);

        // Verify that the event's start time was updated
        assertEquals(LocalDateTime.parse("2025-05-10 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            eventManager.getEvent(0).getStartTime());
    }

    @Test
    void testEditEventEndTime() throws SyncException {
        // Simulate editing the end time of the event
        Event updatedEvent = new Event(
            event.getName(),
            event.getStartTime(),
            LocalDateTime.parse("2025-05-10 18:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            event.getLocation(),
            event.getDescription());

        // Update the event using the updateEvent method
        eventManager.updateEvent(0, updatedEvent);

        // Verify that the event's end time was updated
        assertEquals(LocalDateTime.parse("2025-05-10 18:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            eventManager.getEvent(0).getEndTime());
    }

    @Test
    void testEditEventLocation() throws SyncException {
        // Simulate editing the location of the event
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                "New Location",
                event.getDescription());

        // Update the event using the updateEvent method
        eventManager.updateEvent(0, updatedEvent);

        // Verify that the event's location was updated
        assertEquals("New Location", eventManager.getEvent(0).getLocation());
    }

    @Test
    void testEditEventDescription() throws SyncException {
        // Simulate editing the description of the event
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                "Updated description");

        // Update the event using the updateEvent method
        eventManager.updateEvent(0, updatedEvent);

        // Verify that the event's description was updated
        assertEquals("Updated description", eventManager.getEvent(0).getDescription());
    }

    @Test
    void testInvalidEventIndex() {
        // Try to update an event with an invalid index (out of bounds)
        Event updatedEvent = new Event(
                "Invalid Event",
                event.getStartTime(),
                event.getEndTime(),
                "Invalid Location",
                "Invalid description");

        // Verify that SyncException is thrown for an invalid index
        assertThrows(SyncException.class, () -> {
            eventManager.updateEvent(100, updatedEvent);  // index 100 is out of bounds
        });
    }
}
