package commandfactory;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.FilterCommand;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;
import label.Priority;
import java.util.ArrayList;



public class FilterCommandFactoryTest {
    private MockUI ui;
    private Participant participant;
    private ParticipantManager participantManager;

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

    @BeforeEach
    void setUp() throws SyncException {
        ui = new MockUI();

        participant = new Participant("admin_user", "admin123",
                Participant.AccessLevel.ADMIN, new ArrayList<>());

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participant);

        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        participantManager = new ParticipantManager(participants, ui, userStorage);
        participantManager.setCurrentUser(participant);
    }

    @Test
    public void testSinglePriorityInput() throws SyncException {
        ui.setSimulatedInput("LOW");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        Command command = factory.createCommand();

        assertTrue(command instanceof FilterCommand);
        FilterCommand filterCommand = (FilterCommand) command;
        assertEquals(Priority.getValue(Priority.LOW), filterCommand.getLowerBound());
    }

    @Test
    public void testPriorityRangeInput() throws SyncException {
        ui.setSimulatedInput("LOW HIGH");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        Command command = factory.createCommand();

        assertTrue(command instanceof FilterCommand);
        FilterCommand filterCommand = (FilterCommand) command;
        assertEquals(Priority.getValue(Priority.LOW), filterCommand.getLowerBound());
        assertEquals(Priority.getValue(Priority.HIGH), filterCommand.getUpperBound());
    }

    @Test
    public void testInvalidPriorityThrowsException() {
        ui.setSimulatedInput("banana");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals(SyncException.invalidPriorityFilterErrorMessage(), e.getMessage());
    }

    @Test
    public void testReversedBoundThrowsException() {
        ui.setSimulatedInput("HIGH LOW");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals(SyncException.invalidBoundErrorMessage(), e.getMessage());
    }

    @Test
    public void testTooManyArgumentsThrowsException() {
        ui.setSimulatedInput("LOW MEDIUM HIGH");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("Please provide one or two priority levels (e.g.,'LOW', 'LOW MEDIUM')", e.getMessage());
    }

    @Test
    public void testEmptyInputThrowsException() {
        ui.setSimulatedInput("      ");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("Input string should not be empty", e.getMessage());
    }

    @Test
    public void testNullInputThrowsException() {
        ui.setSimulatedInput(null);

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("Input string should not be null", e.getMessage());
    }

    @Test
    public void testNoUserLoggedInThrowsException() {
        ui.setSimulatedInput("LOW");
        participantManager.setCurrentUser(null);

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("You are not logged in. Enter 'login' to log in first.", e.getMessage());
    }

    @Test
    public void testCaseInsensitivity() throws SyncException {
        ui.setSimulatedInput("lOw hIgH");
        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        Command command = factory.createCommand();
        FilterCommand filterCommand = (FilterCommand) command;
        assertEquals(Priority.getValue(Priority.LOW), filterCommand.getLowerBound());
        assertEquals(Priority.getValue(Priority.HIGH), filterCommand.getUpperBound());
    }

    @Test
    public void testWhitespaceHandling() throws SyncException {
        ui.setSimulatedInput("  LOW HIGH  ");
        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        Command command = factory.createCommand();
        FilterCommand filterCommand = (FilterCommand) command;
        assertEquals(Priority.getValue(Priority.LOW), filterCommand.getLowerBound());
        assertEquals(Priority.getValue(Priority.HIGH), filterCommand.getUpperBound());
    }

    @Test
    public void testNonPriorityStringThrowsException() {
        ui.setSimulatedInput("test");
        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals(SyncException.invalidPriorityFilterErrorMessage(), e.getMessage());
    }
}
