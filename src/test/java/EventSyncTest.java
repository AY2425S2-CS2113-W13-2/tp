import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.SyncException;
import event.Event;
import event.EventManager;
import storage.Storage;
import storage.UserStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class EventSyncTest {
    private EventManager eventManager;
    private ui.UI ui;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new ui.UI();
        UserStorage userStorage = new UserStorage("./data/UserSyncTest.txt");
        Storage storage = new Storage("./data/EventSyncTest.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
    }

    @Test
    void testAddEvent() throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        Event event = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter),
                "Conference Room", "Team Meeting");

        eventManager.addEvent(event);

        assertEquals(1, eventManager.size());
        assertEquals(event, eventManager.getEvent(0));
    }

    @Test
    void testGetEventValidIndex() throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        Event event = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter),
                "Conference Room", "Team Meeting");
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
}
