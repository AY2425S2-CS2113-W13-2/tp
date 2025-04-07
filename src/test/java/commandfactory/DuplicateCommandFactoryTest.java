package commandfactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.DuplicateCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class DuplicateCommandFactoryTest {
    public class TestUI extends UI {
        private String simulatedInput;

        public void setSimulatedInput(String input) {
            this.simulatedInput = input;
        }

        @Override
        public String readDuplicateEventInput() {
            return simulatedInput;
        }
    }

    private TestUI ui;
    private ParticipantManager participantManager;
    private EventManager eventManager;
    private DuplicateCommandFactory factory;

    private Participant adminUser;

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
        factory = new DuplicateCommandFactory(participantManager, ui, eventManager);
    }

    @Test
    void testValidDuplicateCommandReturnsDuplicateCommand() throws SyncException {
        ui.setSimulatedInput("1 Copied Event");

        Command command = factory.createCommand();

        assertTrue(command instanceof DuplicateCommand);
    }

    @Test
    void testThrowsWhenUserNotLoggedIn() {
        participantManager.setCurrentUser(null);
        ui.setSimulatedInput("1 Copied Event");

        SyncException ex = assertThrows(SyncException.class, () -> {
            new DuplicateCommandFactory(participantManager, ui, eventManager).createCommand();
        });

        assertTrue(ex.getMessage().contains("not logged in"));
    }

    @Test
    void testThrowsWhenUserIsNotAdmin() {
        Participant normalUser = new Participant("user", "pw", Participant.AccessLevel.MEMBER, List.of());
        participantManager.setCurrentUser(normalUser);
        ui.setSimulatedInput("1 Copied Event");

        SyncException ex = assertThrows(SyncException.class, () -> {
            new DuplicateCommandFactory(participantManager, ui, eventManager).createCommand();
        });

        assertTrue(ex.getMessage().contains("Only admin"));
    }

    @Test
    void testThrowsWhenInputFormatInvalid() {
        ui.setSimulatedInput("1");

        SyncException ex = assertThrows(SyncException.class, () -> factory.createCommand());

        assertTrue(ex.getMessage().contains("Invalid duplicate command format"));
    }

    @Test
    void testThrowsWhenIndexNotNumber() {
        ui.setSimulatedInput("abc New Name");

        SyncException ex = assertThrows(SyncException.class, () -> factory.createCommand());

        assertTrue(ex.getMessage().contains("Invalid index format"));
    }

    @Test
    void testThrowsWhenIndexOutOfBounds() {
        ui.setSimulatedInput("10 New Name");

        SyncException ex = assertThrows(SyncException.class, () -> factory.createCommand());

        assertTrue(ex.getMessage().contains("Invalid event index"));
    }
}
