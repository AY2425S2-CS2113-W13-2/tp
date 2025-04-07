package commandfactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.EditEventCommand;
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
import java.util.List;
import java.util.Scanner;


public class EditCommandFactoryTest {
    private static class MockUI extends UI {
        private String simulatedInput;

        @Override
        public String readFilterInput() {
            return simulatedInput;
        }

        public void setSimulatedInput(String input) {
            this.simulatedInput = input;
        }
    }

    private MockUI ui;
    private ParticipantManager participantManager;
    private EventManager eventManager;
    private EditCommandFactory editCommandFactory;
    private Participant adminUser;

    @BeforeEach
    void setUp() throws Exception {
        ui = new MockUI();
        adminUser = new Participant("admin", "pass", Participant.AccessLevel.ADMIN,
                List.of());
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
        editCommandFactory = new EditCommandFactory(participantManager, eventManager, ui);
    }

    @Test
    void testCreateCommandReturnsEditEventCommand() throws SyncException {
        String simulatedInput = "1";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        Command command = editCommandFactory.createCommand();

        assertTrue(command instanceof EditEventCommand);
    }

    @Test
    void testCreateCommandThrowsIfNotLoggedIn() {
        participantManager.setCurrentUser(null);
        ui.setSimulatedInput("1");

        EditCommandFactory factory = new EditCommandFactory(participantManager, eventManager, ui);
        SyncException ex = assertThrows(SyncException.class, factory::createCommand);
        assertTrue(ex.getMessage().contains("not logged in"));
    }

    @Test
    void testCreateCommandThrowsIfNotAdmin() {
        Participant user = new Participant("user", "pw", Participant.AccessLevel.MEMBER, List.of());
        participantManager.setCurrentUser(user);
        ui.setSimulatedInput("1");

        EditCommandFactory factory = new EditCommandFactory(participantManager, eventManager, ui);
        SyncException ex = assertThrows(SyncException.class, factory::createCommand);
        assertTrue(ex.getMessage().contains("Only admin"));
    }

    @Test
    void testCreateCommandThrowsOnInvalidInput() {
        ui.setSimulatedInput("abc"); // not a number

        assertThrows(SyncException.class, () -> editCommandFactory.createCommand());
    }
}
