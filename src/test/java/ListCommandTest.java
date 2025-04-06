import command.ListCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import label.Priority;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ListCommandTest {
    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;

    @BeforeEach
    void setUp() throws SyncException {
        UI ui = new UI();
        UserStorage userStorage = new UserStorage("data/test-users.txt");
        Storage storage = new Storage("data/test-events.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);

        Participant user = new Participant("testuser", "pass", Participant.AccessLevel.MEMBER);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(user);
        participantManager = new ParticipantManager(participants, ui, userStorage);

        Event event = new Event("Test Event",
                LocalDateTime.of(2025, 4, 2, 10, 0),
                LocalDateTime.of(2025, 4, 2, 11, 0),
                "Room 1", "Testing");
        event.addParticipant(user);
        eventManager.addEvent(event);

        Priority.getAllPriorities().add("medium");
    }

    @Test
    void testListCommandExecute() {
        ListCommand command = new ListCommand("priority");
        assertDoesNotThrow(() -> command.execute(eventManager, ui, participantManager));
    }
}
