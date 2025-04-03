//package commandfactory;
//
//import command.AddParticipantCommand;
//import event.Event;
//import event.EventManager;
//import participant.Participant;
//import participant.ParticipantManager;
//import exception.SyncException;
//import ui.UI;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//class AddParticipantCommandFactoryTest {
//
//    private ParticipantManager participantManager;
//    private UI ui;
//    private EventManager eventManager;
//    private AddParticipantCommandFactory addParticipantCommandFactory;
//
//    @BeforeEach
//    void setUp() {
//        // Mock UI
//        ui = new UI() {
//            @Override
//            public void showMessage(String message) {
//                // Do nothing in tests, or print message for debugging purposes
//            }
//        };
//
//        // Create a ParticipantManager and add some participants
//        List<Participant> participants = new ArrayList<>();
//        participants.add(new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, new ArrayList<>()));
//        participants.add(new Participant("jane_doe", "password456", Participant.AccessLevel.MEMBER, new ArrayList<>()));
//        participantManager = new ParticipantManager(participants, ui, null);
//
//        // Create some events for the EventManager
//        List<Event> events = new ArrayList<>();
//        events.add(new Event("Event 1", null, null, "Room A", "Description 1"));
//        events.add(new Event("Event 2", null, null, "Room B", "Description 2"));
//        eventManager = new EventManager(events, ui, null, null);
//
//        // Create the AddParticipantCommandFactory
//        addParticipantCommandFactory = new AddParticipantCommandFactory(eventManager, participantManager, ui);
//    }
//
//    @Test
//    void testCreateCommandWithValidInput() throws SyncException {
//        // Simulate the input for adding a participant
//        String simulatedInput = "1 | john_doe";  // Event index: 1, Participant: john_doe
//
//        // Create the command using the factory
//        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
//        System.setIn(in);
//
//        // Create the AddParticipantCommand
//        AddParticipantCommand command = (AddParticipantCommand) addParticipantCommandFactory.createCommand();
//
//        // Validate the created command
//        assertNotNull(command);
//        assertEquals(0, command.getEventIndex());  // Index is 1, but array is 0-based, so it should be 0
//        assertEquals("john_doe", command.getParticipantName());
//    }
//
//    @Test
//    void testCreateCommandWithAdminPrivileges() throws SyncException {
//        // Set the current user as admin
//        participantManager.setCurrentUser(new Participant("admin", "adminpass", Participant.AccessLevel.ADMIN, new ArrayList<>()));
//
//        // Simulate the input for adding a participant
//        String simulatedInput = "2 | jane_doe";  // Event index: 2, Participant: jane_doe
//
//        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
//        System.setIn(in);
//
//        // Create the AddParticipantCommand
//        AddParticipantCommand command = (AddParticipantCommand) addParticipantCommandFactory.createCommand();
//
//        // Validate the created command
//        assertNotNull(command);
//        assertEquals(1, command.getEventIndex());  // Index is 2, but array is 0-based, so it should be 1
//        assertEquals("jane_doe", command.getParticipantName());
//    }
//
//    @Test
//    void testCreateCommandWithNonAdminUser() {
//        // Set the current user as non-admin
//        participantManager.setCurrentUser(new Participant("user", "userpass", Participant.AccessLevel.USER, new ArrayList<>()));
//
//        // Attempt to create a command should throw SyncException due to lack of admin privileges
//        assertThrows(SyncException.class, () -> addParticipantCommandFactory.createCommand());
//    }
//
//    @Test
//    void testCreateCommandWhenNoParticipantsAvailable() throws SyncException {
//        // Remove all participants
//        participantManager.setParticipants(new ArrayList<>());
//
//        // Simulate the input for adding a participant
//        String simulatedInput = "1 | john_doe";  // Event index: 1, Participant: john_doe
//        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
//        System.setIn(in);
//
//        // No participants available, should display a message
//        assertThrows(SyncException.class, () -> addParticipantCommandFactory.createCommand());
//    }
//
//    @Test
//    void testCreateCommandWhenNoEventsAvailable() throws SyncException {
//        // Remove all events
//        eventManager.setEvents(new ArrayList<>());
//
//        // Simulate the input for adding a participant
//        String simulatedInput = "1 | john_doe";  // Event index: 1, Participant: john_doe
//        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
//        System.setIn(in);
//
//        // No events available, should throw SyncException
//        assertThrows(SyncException.class, () -> addParticipantCommandFactory.createCommand());
//    }
//}
