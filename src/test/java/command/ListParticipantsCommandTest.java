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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class ListParticipantsCommandTest {

    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() throws SyncException {
        System.setOut(new PrintStream(outputStreamCaptor));

        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 1, 10, 0);
        LocalDateTime endTime1 = LocalDateTime.of(2023, 5, 1, 12, 0);
        Event event = new Event("Event 1", startTime1, endTime1, "Location 1", "Description 1");

        Participant participant1 = new Participant("Alice", "password", Participant.AccessLevel.MEMBER);
        Participant participant2 = new Participant("Bob", "password", Participant.AccessLevel.MEMBER);

        event.addParticipant(participant1);
        event.addParticipant(participant2);

        eventManager.addEvent(event);
    }

    @Test
    public void testExecute_ListParticipants() throws SyncException {
        ListParticipantsCommand command = new ListParticipantsCommand(0);

        command.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString().trim();

        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("Bob"));
    }

    @Test
    public void testExecute_InvalidEventIndex() throws SyncException {
        ListParticipantsCommand command = new ListParticipantsCommand(999);

        SyncException thrown = assertThrows(SyncException.class, () -> {
            command.execute(eventManager, ui, participantManager);
        });
    }
}
