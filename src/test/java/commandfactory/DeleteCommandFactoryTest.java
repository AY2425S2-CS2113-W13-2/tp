package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.DeleteCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class DeleteCommandFactoryTest {

    private TestUI ui;
    private ParticipantManager participantManager;
    private EventManager eventManager;
    private DeleteCommandFactory factory;

    private Participant adminUser;

    public class TestUI extends UI {
        private final Queue<String> inputs = new LinkedList<>();

        public void addInput(String input) {
            inputs.offer(input);
        }

        @Override
        public String readDeleteName() {
            return inputs.poll();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        ui = new TestUI();

        adminUser = new Participant("admin", "pass", Participant.AccessLevel.ADMIN, List.of());
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(adminUser);
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        participantManager = new ParticipantManager(participants, ui, userStorage);
        participantManager.setCurrentUser(adminUser);

        eventManager = new EventManager("./data/test-events.txt", userStorage);
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);
        eventManager.addEvent(new Event("Meeting", LocalDateTime.of(2025, 4, 7, 10, 0),
                LocalDateTime.of(2025,4,7,10,30), "Test Location", "Test Description"));
        eventManager.addEvent(new Event("Meeting2", LocalDateTime.of(2025, 4, 7, 12, 0),
                LocalDateTime.of(2025, 4, 7, 12, 30), "Test Location", "Test Description"));
        eventManager.addEvent(new Event("Meeting with team", LocalDateTime.of(2025, 4, 7, 15, 0),
                LocalDateTime.of(2025, 4, 7, 15, 30), "Test Location", "Test Description"));

        factory = new DeleteCommandFactory(participantManager, ui, eventManager);
    }

    @Test
    void testDeleteSingleMatchingEvent() throws SyncException {
        ui.addInput("team");

        Command command = factory.createCommand();
        assertTrue(command instanceof DeleteCommand);
    }

    @Test
    void testDeleteMultipleMatchingEventsWithUserInputIndex() throws SyncException {
        ui.addInput("Meeting");
        String consoleInput = "2\n";
        System.setIn(new ByteArrayInputStream(consoleInput.getBytes()));

        Command command = factory.createCommand();
        assertTrue(command instanceof DeleteCommand);

        DeleteCommand deleteCommand = (DeleteCommand) command;
        assertEquals(3, deleteCommand.getEventIndex());  // "Meeting with team"
    }

    @Test
    void testThrowsIfUserNotLoggedIn() {
        participantManager.setCurrentUser(null);
        ui.addInput("Meeting");

        SyncException ex = assertThrows(SyncException.class, () -> {
            new DeleteCommandFactory(participantManager, ui, eventManager).createCommand();
        });

        assertTrue(ex.getMessage().contains("not logged in"));
    }

    @Test
    void testThrowsIfUserIsNotAdmin() {
        Participant normalUser = new Participant("user", "pw", Participant.AccessLevel.MEMBER, List.of());
        participantManager.setCurrentUser(normalUser);
        ui.addInput("Meeting");

        SyncException ex = assertThrows(SyncException.class, () -> {
            new DeleteCommandFactory(participantManager, ui, eventManager).createCommand();
        });

        assertTrue(ex.getMessage().contains("Only admin"));
    }

    @Test
    void testThrowsIfEventNotFound() {
        ui.addInput("Nonexistent");

        SyncException ex = assertThrows(SyncException.class, () -> {
            factory.createCommand();
        });

        assertTrue(ex.getMessage().contains("No events found"));
    }

    @Test
    void testThrowsIfInvalidIndexFormatEntered() {
        ui.addInput("Meeting");
        System.setIn(new ByteArrayInputStream("abc\n".getBytes()));

        SyncException ex = assertThrows(SyncException.class, () -> {
            factory.createCommand();
        });

        assertTrue(ex.getMessage().contains("Invalid index format"));
    }

    @Test
    void testThrowsIfOutOfBoundsIndexEntered() {
        ui.addInput("Meeting");
        System.setIn(new ByteArrayInputStream("10\n".getBytes()));

        SyncException ex = assertThrows(SyncException.class, () -> {
            factory.createCommand();
        });

        assertTrue(ex.getMessage().contains("Invalid event index"));
    }
}
