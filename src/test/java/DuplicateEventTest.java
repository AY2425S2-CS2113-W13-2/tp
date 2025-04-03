import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import event.EventManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class DuplicateEventTest {

    private EventManager eventManager;
    private UI ui;
    private Event event;

    @BeforeEach
    void setUp() throws SyncException {
        UI ui = new UI();
        UserStorage userStorage = new UserStorage("test-users.txt");
        Storage storage = new Storage("test-events.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);

        event = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                LocalDateTime.parse("2025/05/10 16:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                "Conference Room", "Discuss project updates");
        eventManager.addEvent(event);
    }

//    @Test
//    void testDuplicateEvent() throws SyncException {
//        // Simulate duplicating the event
//        eventManager.duplicateEvent(event, "Duplicate Meeting");
//
//        // Verify that the event was duplicated and added to the list
//        assertEquals(2, eventManager.size());
//        assertEquals("Duplicate Meeting", eventManager.getEvent(1).getName());
//        assertEquals(event.getStartTime(), eventManager.getEvent(1).getStartTime());
//        assertEquals(event.getEndTime(), eventManager.getEvent(1).getEndTime());
//        assertEquals(event.getLocation(), eventManager.getEvent(1).getLocation());
//        assertEquals(event.getDescription(), eventManager.getEvent(1).getDescription());
//    }
//
//    @Test
//    void testFindEventByDescription() throws SyncException {
//        // Add another event to test find method with multiple events
//        Event anotherEvent = new Event("Presentation",
//                LocalDateTime.parse("2025/05/11 10:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
//                LocalDateTime.parse("2025/05/11 12:00", DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
//                "Lecture Hall", "Present project findings");
//        eventManager.addEvent(anotherEvent);
//
//        // Simulate finding events by description
//        ArrayList<Event> matchingEvents = new ArrayList<>();
//        for (Event currentEvent : eventManager.getEvents()) {
//            if (currentEvent.getDescription().toLowerCase().contains("project")) {
//                matchingEvents.add(currentEvent);
//            }
//        }
//
//        // Verify that the find method returns the correct events
//        assertEquals(2, matchingEvents.size());
//        assertTrue(matchingEvents.contains(event));
//        assertTrue(matchingEvents.contains(anotherEvent));
//    }
//
//    @Test
//    void testFindEventNoMatch() throws SyncException {
//        // Simulate finding events by description with no matches
//        ArrayList<Event> matchingEvents = new ArrayList<>();
//        for (Event currentEvent : eventManager.getEvents()) {
//            if (currentEvent.getDescription().toLowerCase().contains("nonexistent")) {
//                matchingEvents.add(currentEvent);
//            }
//        }
//
//        // Verify that the find method returns an empty list when no matches are found
//        assertTrue(matchingEvents.isEmpty());
//    }
}
