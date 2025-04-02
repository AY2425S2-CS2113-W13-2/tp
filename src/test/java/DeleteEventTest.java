import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import event.Event;
import event.EventManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

class DeleteEventTest {
    private EventManager eventManager;

    @BeforeEach
    void setUp() throws SyncException {
        UI ui = new UI();
        UserStorage userStorage = new UserStorage("test-users.txt");
        Storage storage = new Storage("test-delete-events.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
    }

    @Test
    void testDeleteValidEvent() throws SyncException {
        Event event = new Event("Test Event",
                LocalDateTime.of(2025, 5, 1, 10, 0),
                LocalDateTime.of(2025, 5, 1, 11, 0),
                "Lab", "Test Desc");
        eventManager.addEvent(event);
        assertEquals(1, eventManager.size());

        eventManager.deleteEvent(0);
        assertEquals(0, eventManager.size());
    }

    @Test
    void testDeleteInvalidIndex() {
        assertThrows(SyncException.class, () -> eventManager.deleteEvent(5));
    }
}