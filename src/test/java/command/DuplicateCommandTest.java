package command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import event.Event;
import event.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DuplicateCommandTest {

    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;
    private UserStorage userStorage;
    private Storage eventStorage;

    private Event originalEvent;

    @BeforeEach
    public void setUp() throws SyncException {
        InputStream testInput = new ByteArrayInputStream("".getBytes());
        ui = new UI();
        ui.setScanner(new Scanner(testInput));
        userStorage = new UserStorage("./data/test-users.txt");
        eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        ArrayList<Event> events = new ArrayList<>();
        originalEvent = new Event("Event",
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0),
                "Lab", "na");
        events.add(originalEvent);
        eventManager = new EventManager(events, ui, eventStorage, userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui,
                new UserStorage("./data/test-users.txt"));
    }

    @Test
    public void testDuplicateCommandCreatesNewEvent() throws SyncException {
        String newName = "Duplicated Event";
        DuplicateCommand command = new DuplicateCommand(originalEvent, newName);

        command.execute(eventManager, ui, participantManager);

        List<Event> events = eventManager.getEvents();

        assertEquals(2, events.size());

        Event duplicated = events.get(1);
        assertEquals(newName, duplicated.getName());
        assertEquals(originalEvent.getStartTime(), duplicated.getStartTime());
        assertEquals(originalEvent.getEndTime(), duplicated.getEndTime());
    }
}
