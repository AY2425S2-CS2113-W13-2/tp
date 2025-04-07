package command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListAllCommandTest {

    private UI ui;
    private ParticipantManager participantManager;
    private EventManager eventManager;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private ArrayList<Event> events;

    @BeforeEach
    void setUp() throws SyncException {
        System.setOut(new PrintStream(outputStreamCaptor));

        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        String simulatedInput = "HIGH\nMEDIUM\nLOW\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        events = new ArrayList<>();
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 1, 10, 0);
        LocalDateTime endTime1 = LocalDateTime.of(2023, 5, 1, 12, 0);
        eventManager.addEvent(new Event("Event 1", startTime1, endTime1, "Location 1", "Description 1"));

        LocalDateTime startTime2 = LocalDateTime.of(2023, 6, 1, 14, 0);
        LocalDateTime endTime2 = LocalDateTime.of(2023, 6, 1, 16, 0);
        eventManager.addEvent(new Event("Event 2", startTime2, endTime2, "Location 2", "Description 2"));

        Participant admin = new Participant("admin", "password", Participant.AccessLevel.ADMIN);
        participantManager.setCurrentUser(admin);
    }

    @Test
    void testExecuteAdminUser() throws SyncException {
        ListAllCommand listAllCommand = new ListAllCommand("start", ui);
        listAllCommand.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Event 1"));
        assertTrue(output.contains("Event 2"));
    }

    @Test
    void testExecuteNoUserLoggedIn() throws SyncException {
        participantManager.setCurrentUser(null);

        String simulatedInput = "no\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        ListAllCommand listAllCommand = new ListAllCommand("start", ui);
        assertThrows(SyncException.class, () -> listAllCommand.execute(eventManager, ui, participantManager));
    }

    @Test
    void testExecuteNonAdminUser() throws SyncException {
        Participant nonAdmin = new Participant("user", "password", Participant.AccessLevel.MEMBER);
        participantManager.setCurrentUser(nonAdmin);

        ListAllCommand listAllCommand = new ListAllCommand("start", ui);
        listAllCommand.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Sorry, you need to be an ADMIN to access all events"));
    }

    @Test
    void testExecuteEmptyEventList() throws SyncException {
        eventManager.setEvents(new ArrayList<>());

        ListAllCommand listAllCommand = new ListAllCommand("start", ui);
        listAllCommand.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("No events in the system"));
    }

    @Test
    void testExecuteInvalidSortType() throws SyncException {
        ListAllCommand listAllCommand = new ListAllCommand("invalid", ui);
        listAllCommand.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Unknown sort type. Showing unsorted list."));
    }
}
