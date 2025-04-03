package command;

import commandfactory.CommandFactory;
import commandfactory.CreateUserCommandFactory;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.AvailabilitySlot;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AddParticipantCommandTest {
    private AddParticipantCommand addParticipantCommand;
    private Event testEvent;
    private EventManager testEventManager;
    private UI testUI;
    private ParticipantManager testParticipantManager;
    private Participant testParticipant;
    private Storage testStorage;
    private UserStorage testUserStorage;
    private UI ui;

    @BeforeEach
    void setUp() throws SyncException {
        // Initialize test data
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        testEvent = new Event("Test Event", startTime, endTime, "Location", "Description", new ArrayList<>());

        testUserStorage = new UserStorage("./data/test-users.txt");
        testStorage = new Storage("./data/test-events.txt", testUserStorage);

        testUI = new UI();

        ArrayList<Event> eventList = new ArrayList<>();
        eventList.add(testEvent);
        testEventManager = new EventManager(eventList, testUI, testStorage, testUserStorage);

        testParticipant = new Participant("TestUser", "password123", Participant.AccessLevel.MEMBER, new ArrayList<>());
        ArrayList<Participant> participantList = new ArrayList<>();
        testParticipantManager = new ParticipantManager(participantList, testUI, testUserStorage);

        addParticipantCommand = new AddParticipantCommand(0, "TestUser");

        UI ui = new UI();
    }

    @Test
    void testExecuteWhenParticipantExists() throws SyncException {
        // Add participant to the manager
        testParticipantManager.addNewUser(testParticipant);

        ArrayList<AvailabilitySlot> availabilitySlots = new ArrayList<>();
        AvailabilitySlot availabilitySlot = new AvailabilitySlot(
                testEvent.getStartTime().minusMinutes(30),
                testEvent.getEndTime().plusMinutes(30)
        );
        availabilitySlots.add(availabilitySlot);
        testParticipant.setAvailableTimes(availabilitySlots);

        // Execute command
        addParticipantCommand.execute(testEventManager, ui, testParticipantManager);

        // Assert participant is added to the event
        assertTrue(testEvent.getParticipants().contains(testParticipant),
                "Participant should be added to the event.");
    }

    @Test
    void testExecuteWhenParticipantDoesNotExist() throws SyncException {
        // Setup CreateUserCommandFactory to return a command that adds the test participant
        CommandFactory testFactory = new CreateUserCommandFactory() {
            @Override
            public Command createCommand() {
                return new Command() {
                    @Override
                    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager)
                            throws SyncException {
                        participantManager.addNewUser(testParticipant);
                    }
                };
            }
        };

        // Use reflection to set the private field in the command
        try {
            java.lang.reflect.Field field = AddParticipantCommand.class.getDeclaredField("factory");
            field.setAccessible(true);
            field.set(addParticipantCommand, testFactory);
        } catch (Exception e) {
            fail("Failed to set CommandFactory field: " + e.getMessage());
        }

        ArrayList<AvailabilitySlot> availabilitySlots = new ArrayList<>();
        AvailabilitySlot availabilitySlot = new AvailabilitySlot(
                testEvent.getStartTime().minusMinutes(30),
                testEvent.getEndTime().plusMinutes(30)
        );
        availabilitySlots.add(availabilitySlot);
        testParticipant.setAvailableTimes(availabilitySlots);

        addParticipantCommand.execute(testEventManager, ui, testParticipantManager);
        assertThrows(SyncException.class, () -> addParticipantCommand.execute(
                testEventManager, testUI, testParticipantManager));
    }

    @Test
    void testExecuteWhenParticipantIsUnavailable() throws SyncException {
        // Add participant to the manager
        testParticipantManager.addNewUser(testParticipant);

        ArrayList<AvailabilitySlot> availabilitySlots = new ArrayList<>();
        AvailabilitySlot availabilitySlot = new AvailabilitySlot(
                testEvent.getStartTime().minusMinutes(2),
                testEvent.getEndTime().plusMinutes(30)
        );
        availabilitySlots.add(availabilitySlot);
        testParticipant.setAvailableTimes(availabilitySlots);

        // Execute command
        addParticipantCommand.execute(testEventManager, testUI, testParticipantManager);

        // Assert participant is not added to the event
        assertFalse(testEvent.getParticipants().contains(testParticipant),
                "Participant should not be added to the event when unavailable.");
    }

    @Test
    void testExecuteWhenUserDeclinesToCreateNewParticipant() throws SyncException {
        addParticipantCommand.execute(testEventManager, ui, testParticipantManager);

        // Assert no participant is added
        assertNull(testParticipantManager.getParticipant("TestUser"),
                "No participant should be created when user declines.");
        assertEquals(0, testEvent.getParticipants().size(),
                "No participant should be added to the event when user declines creation.");
    }
}