package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.FilterCommand;
import exception.SyncException;
import logger.EventSyncLogger;
import participant.AvailabilitySlot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class FilterCommandFactoryTest {
    private MockUI ui;
    private Participant participant;
    private ParticipantManager participantManager;

    // Custom UI implementation for testing
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

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    void setUp() throws SyncException {
        ui = new MockUI();

        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));

        participant = new Participant("admin_user", "admin123",
                Participant.AccessLevel.ADMIN, availableTimes);

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
    }

    @Test
    public void testPriorityRangeInput() throws SyncException {
        ui.setSimulatedInput("LOW HIGH");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        Command command = factory.createCommand();

        assertTrue(command instanceof FilterCommand);
        FilterCommand filterCommand = (FilterCommand) command;
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
    public void testEmptyInputThrowsAssertionError() {
        ui.setSimulatedInput("      ");

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        assertThrows(AssertionError.class, factory::createCommand);
    }

    @Test
    public void testNullInputThrowsAssertionError() {
        ui.setSimulatedInput(null);

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        assertThrows(AssertionError.class, factory::createCommand);
    }

    @Test
    public void testNoUserLoggedInThrowsException() {
        ui.setSimulatedInput("LOW");
        participantManager.setCurrentUser(null);

        FilterCommandFactory factory = new FilterCommandFactory(participantManager, ui);
        SyncException e = assertThrows(SyncException.class, factory::createCommand);
        assertEquals("You are not logged in. Enter 'login' to log in first.", e.getMessage());
    }
}