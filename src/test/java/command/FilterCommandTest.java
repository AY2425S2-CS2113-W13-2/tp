package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import label.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;


public class FilterCommandTest {

    private EventManager eventManager;
    private UI ui;
    private ParticipantManager participantManager;
    private UserStorage userStorage;
    private Storage eventStorage;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() throws SyncException {
        ui = new UI();

        userStorage = new UserStorage("./data/test-users.txt");

        eventStorage = new Storage("./data/test-events.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);


        ArrayList<Event> events = new ArrayList<>();
        eventManager = new EventManager(events, ui, eventStorage, userStorage);

        System.setOut(new PrintStream(outputStreamCaptor));

        Priority.clearPriorities();
    }


    @Test
    public void testFilterCommand_ValidPriorityRange() throws SyncException {
        String simulatedInput = "HIGH\nMEDIUM\nLOW\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 16, 0);
        eventManager.addEvent(new Event("Event 1", startTime, endTime, "Location 1", "Description 1"));
        eventManager.addEvent(new Event("Event 2", startTime, endTime, "Location 2", "Description 2"));
        eventManager.addEvent(new Event("Event 3", startTime, endTime, "Location 3", "Description 3"));


        FilterCommand command = new FilterCommand(1, 1);

        command.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Event 2"));
        assertTrue(output.contains("Event 3"));
    }

    @Test
    public void testFilterCommand_EmptyMatchingEvents() throws SyncException {
        FilterCommand command = new FilterCommand(4, 5);

        assertThrows(SyncException.class, () -> {
            command.execute(eventManager, ui, participantManager);
        });
    }

    @Test
    public void testAddEventWithPriority() throws SyncException {
        String simulatedInput = "MEDIUM\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 16, 0);
        Event newEvent = new Event("New Event", startTime, endTime, "New Location", "New Description");

        eventManager.addEvent(newEvent);

        assertEquals("MEDIUM", Priority.getPriority(eventManager.size() - 1));
    }
}
