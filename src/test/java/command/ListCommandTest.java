package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ListCommandTest {

    private ListCommand listCommand;
    private EventManager eventManager;
    private UI ui;
    private ParticipantManager participantManager;
    private Participant participant;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        participant = new Participant("john_doe", "password123", Participant.AccessLevel.MEMBER, new ArrayList<>());

        UserStorage userStorage = new UserStorage("./data/test-users.txt");

        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        ui = new UI();
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));
        participant = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, availableTimes);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participant);
        participantManager = new ParticipantManager(participants, ui, null);
        participantManager.setCurrentUser(participant);
        listCommand = new ListCommand("priority");
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testExecuteWithNoEvents() throws SyncException {
        ListCommand listCommandNoEvents = new ListCommand("priority");
        listCommandNoEvents.execute(eventManager, ui, participantManager);
        assertTrue(outputStream.toString().contains("No events assigned to you."));
    }

    @Test
    void testExecuteWithEvents() throws SyncException {
        Event event = new Event("Event1", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "meeting room", "NA");
        event.addParticipant(participant);
        eventManager.addEvent(event);
        listCommand.execute(eventManager, ui, participantManager);
        assertTrue(outputStream.toString().contains("Event1"));
    }

    @Test
    void testExecuteWithInvalidSortType() throws SyncException {
        participantManager.setCurrentUser(participant);
        ListCommand invalidSortCommand = new ListCommand("invalid");
        Event event = new Event("Test Event",
                LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0),
                "Lab", "Test Desc");
        event.addParticipant(participant);
        eventManager.addEvent(event);
        invalidSortCommand.execute(eventManager, ui, participantManager);
        assertTrue(outputStream.toString().contains("Unknown sort type."));
    }
}
