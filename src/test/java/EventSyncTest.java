import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.SyncException;
import event.Event;
import event.EventManager;
import participant.Participant;
import seedu.EventSync;
import storage.Storage;
import storage.UserStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class EventSyncTest {
    private EventManager eventManager;
    private ui.UI ui;
    private Participant adminUser;

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

    @Test
    public void testEventSync() throws SyncException {
        String simulatedInput = "add\nTeam Meeting | 2020/05/10 14:00 | 2025/05/10 16:00 | Conference Room | " +
                "A team meeting to discuss project updates\nbye\n";

        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());

        EventSync eventSync = new EventSync(in, "./data/EventSyncTest.txt", "./data/UserSyncTest.txt");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));
        eventSync.run();

        System.setOut(originalSystemOut);

        String output = outputStream.toString().trim();
        assertTrue(output.contains("has been added to the list"));
    }

    @Test
    void testAddEventWithCollision() throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        Event event1 = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter),
                "Conference Room", "Team Meeting");
        eventManager.addEvent(event1);
        assertEquals(1, eventManager.size());
        Event event2 = new Event("Project Discussion",
                LocalDateTime.parse("2025/05/10 15:00", formatter),
                LocalDateTime.parse("2025/05/10 17:00", formatter),
                "Meeting Room", "Project brainstorming");
        eventManager.addEvent(event2);
        assertEquals(2, eventManager.size());
        ArrayList<Event> collisions = eventManager.checkCollision(
                "2025-05-10 15:00",
                "2025-05-10 17:00",
                "Conference Room",
                eventManager.getEvents(),
                -1
        );

        // Verify collision detected with team meeting
        assertEquals(1, collisions.size());
        assertEquals("Team Meeting", collisions.get(0).getName());
    }

    @Test
    void testAddEventWithoutCollision() throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        Event event1 = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter),
                "Conference Room", "Team Meeting");
        eventManager.addEvent(event1);
        Event event2 = new Event("Project Discussion",
                LocalDateTime.parse("2025/05/10 16:30", formatter),
                LocalDateTime.parse("2025/05/10 17:30", formatter),
                "Meeting Room", "Project brainstorming");
        eventManager.addEvent(event2);
        assertEquals(2, eventManager.size());
        event2.setStartTime(LocalDateTime.parse("2025/05/10 15:00", formatter));
        event2.setEndTime(LocalDateTime.parse("2025/05/10 16:30", formatter));
        ArrayList<Event> collisions = eventManager.checkCollision(
                "2025-05-10 15:00",
                "2025-05-10 18:00",
                "Home",
                eventManager.getEvents(),
                -1
        );

        // Verify no collision detected
        assertEquals(0, collisions.size());
    }

    @Test
    void testEditEventToResolveCollision() throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        Event event1 = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter),
                "Conference Room", "Team Meeting");
        eventManager.addEvent(event1);

        ArrayList<Event> collisions1 = eventManager.checkCollision(
                "2025-05-10 15:30",
                "2025-05-10 17:30",
                "Conference Room",
                eventManager.getEvents(),
                -1
        );
        assertEquals(1, collisions1.size());

        Event event2 = new Event("Project Discussion",
                LocalDateTime.parse("2025/05/10 15:30", formatter),
                LocalDateTime.parse("2025/05/10 17:30", formatter),
                "Conference Room", "Project brainstorming");
        eventManager.addEvent(event2);
        assertEquals(2, eventManager.size());
        ArrayList<Event> collisions2 = eventManager.checkCollision(
                "2025-05-10 15:30",
                "2025-05-10 17:30",
                "Home",
                eventManager.getEvents(),
                1
        );

        // Verify no collision detected
        assertEquals(0, collisions2.size());
    }
}
