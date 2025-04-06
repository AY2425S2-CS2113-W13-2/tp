import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import event.Event;
import event.EventManager;
import participant.Participant;
import participant.AvailabilitySlot;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class EditEventTest {

    private EventManager eventManager;
    private UI ui;
    private Event event;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("data/test-users.txt");
        Storage storage = new Storage("data/test-events.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);

        event = new Event(
                "Team Meeting",
                LocalDateTime.parse("2025-05-10 14:00", formatter),
                LocalDateTime.parse("2025-05-10 16:00", formatter),
                "Conference Room",
                "Discuss project updates"
        );
        eventManager.addEvent(event);
    }

    @Test
    void testEditEventName() throws SyncException {
        Event updatedEvent = new Event(
                "Updated Meeting",
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getDescription()
        );
        eventManager.updateEvent(0, updatedEvent);
        assertEquals("Updated Meeting", eventManager.getEvent(0).getName());
    }

    @Test
    void testEditEventStartTime() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                LocalDateTime.parse("2025-05-10 15:00", formatter),
                event.getEndTime(),
                event.getLocation(),
                event.getDescription()
        );
        eventManager.updateEvent(0, updatedEvent);
        assertEquals(LocalDateTime.parse("2025-05-10 15:00", formatter), eventManager.getEvent(0).getStartTime());
    }

    @Test
    void testEditEventEndTime() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                LocalDateTime.parse("2025-05-10 18:00", formatter),
                event.getLocation(),
                event.getDescription()
        );
        eventManager.updateEvent(0, updatedEvent);
        assertEquals(LocalDateTime.parse("2025-05-10 18:00", formatter), eventManager.getEvent(0).getEndTime());
    }

    @Test
    void testEditEventLocation() throws SyncException {
        Event updatedEvent = new Event(
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                "New Location",
                event.getDescription()
        );
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
                "Updated description"
        );
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
                "Invalid description"
        );
        assertThrows(SyncException.class, () -> {
            eventManager.updateEvent(100, updatedEvent);
        });
    }

    @Test
    void testStartTimeAfterEndTime() {
        Event updatedEvent = new Event(
                event.getName(),
                LocalDateTime.parse("2025-05-10 17:00", formatter),
                LocalDateTime.parse("2025-05-10 16:00", formatter),
                event.getLocation(),
                event.getDescription()
        );
        assertThrows(SyncException.class, () -> {
            eventManager.updateEvent(0, updatedEvent);
        });
    }

    @Test
    void testEndTimeBeforeStartTime() {
        Event updatedEvent = new Event(
                event.getName(),
                LocalDateTime.parse("2025-05-10 14:00", formatter),
                LocalDateTime.parse("2025-05-10 13:00", formatter),
                event.getLocation(),
                event.getDescription()
        );
        assertThrows(SyncException.class, () -> {
            eventManager.updateEvent(0, updatedEvent);
        });
    }

    @Test
    void testUnavailableParticipantAfterEdit() throws SyncException {
        // Add a participant with availability from 14:00 to 15:00 only
        Participant participant = new Participant("Alice", "pw", Participant.AccessLevel.MEMBER);
        participant.setAvailableTimes(List.of(new AvailabilitySlot(
                LocalDateTime.parse("2025-05-10 14:00", formatter),
                LocalDateTime.parse("2025-05-10 15:00", formatter))));

        event.addParticipant(participant);

        Event updatedEvent = new Event(
                event.getName(),
                LocalDateTime.parse("2025-05-10 15:30", formatter),
                LocalDateTime.parse("2025-05-10 16:30", formatter),
                event.getLocation(),
                event.getDescription()
        );

        // Reattach the same participant
        updatedEvent.addParticipant(participant);

        assertThrows(SyncException.class, () -> {
            eventManager.updateEvent(0, updatedEvent);
        });
    }
}
