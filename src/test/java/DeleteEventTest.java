import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import event.Event;
import event.EventManager;
import ui.UI;
import exception.SyncException;

public class DeleteEventTest {
    private EventManager eventManager;
    private UI ui;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        eventManager = new EventManager("./data/DeleteEventTest.txt");
    }

    @Test
    void testDeleteEventSuccessfulDeletion() throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        Event event1 = new Event("Workshop",
                LocalDateTime.parse("2025/06/15 10:00", formatter),
                LocalDateTime.parse("2025/06/15 12:00", formatter),
                "Hall A", "Tech discussion");

        Event event2 = new Event("Meeting",
                LocalDateTime.parse("2025/06/16 14:00", formatter),
                LocalDateTime.parse("2025/06/16 15:00", formatter),
                "Room 101", "Project discussion");

        eventManager.addEvent(event1);
        eventManager.addEvent(event2);

        eventManager.deleteEvent(0);

        assertEquals(1, eventManager.getEvents().size());
        assertEquals("Meeting", eventManager.getEvents().get(0).getName());
    }

    @Test
    void testDeleteEventInvalidIndex() {
        Exception exception = assertThrows(SyncException.class, () -> {
            eventManager.deleteEvent(0);
        });
        assertTrue(exception.getMessage().contains("Invalid event index"));
    }
}
