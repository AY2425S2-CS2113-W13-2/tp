package seedu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.SyncException;
import event.Event;
import event.EventManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class EventSyncTest {

    private EventSync eventSync;
    private EventManager eventManager;

    @BeforeEach
    void setUp() throws SyncException {
        eventSync = new EventSync();
        eventManager = new EventManager();
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

        EventSync eventSync = new EventSync(in);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));
        eventSync.run();

        System.setOut(originalSystemOut);

        String output = outputStream.toString().trim();
        assertTrue(output.contains("has been added to the list"));
    }


}
