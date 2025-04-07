package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import command.AddParticipantCommand;
import exception.SyncException;
import event.Event;
import event.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import participant.Participant.AccessLevel;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;



public class AddParticipantCommandFactoryTest {

    private UI ui;
    private ParticipantManager participantManager;
    private EventManager eventManager;
    private AddParticipantCommandFactory factory;
    private Participant admin;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);

        admin = new Participant("admin", "admin123", AccessLevel.ADMIN);
        ArrayList<Participant> participantList = new ArrayList<>();
        participantList.add(admin);

        participantManager = new ParticipantManager(participantList, ui, userStorage);
        participantManager.setCurrentUser(admin);

        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
        factory = new AddParticipantCommandFactory(eventManager, participantManager, ui);
    }

    @Test
    void testCreateCommand_invalidInputFormat_throws() {
        simulateInput("1|john|extra"); // Incorrect number of parts
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Invalid format. Use: <EventIndex> | <Participant Name>. Enter 'addparticipant' to try again.",
                e.getMessage());
    }

    @Test
    void testCreateCommand_inputCancelled_throws() {
        simulateInput("exit");
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("❌ Add participant cancelled by user.", e.getMessage());
    }

    @Test
    void testCreateCommand_emptyParticipantName_throws() throws SyncException {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        simulateInput("1|"); // Empty participant name
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Invalid format. Use: <EventIndex> | <Participant Name>. Enter 'addparticipant' to try again.",
                e.getMessage());
    }

    @Test
    void testCreateCommand_validInput_success() throws SyncException {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        Participant p2 = new Participant("john", "pw", AccessLevel.MEMBER);
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        slots.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 14, 30)));
        p2.setAvailableTimes(slots);
        participantManager.addNewUser(p2);


        simulateInput("1|john");

        AddParticipantCommand command = factory.createCommand();

        assertNotNull(command);
        assertEquals(0, command.getEventIndex());
        assertEquals("john", command.getParticipantName());
    }
    @Test
    void testCreateCommand_negativeEventIndex_throws() throws SyncException {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        simulateInput("-1|john");
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Event index cannot be negative. Please enter a valid index.", e.getMessage());
    }

    @Test
    void testCreateCommand_eventIndexOutOfBounds_throws() throws SyncException {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        simulateInput("100|john");
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Event index is out of bounds. Please enter a valid index.", e.getMessage());
    }

    @Test
    void testCreateCommand_notLoggedIn_throws() {
        participantManager.setCurrentUser(null);
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("You are not logged in. Enter 'login' to log in first.", e.getMessage());
    }

    @Test
    void testCreateCommand_nonAdminUser_throws() {
        Participant user = new Participant("user", "pw", AccessLevel.MEMBER);
        participantManager.setCurrentUser(user);
        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Only ADMIN users can add participants. Please 'logout' and 'login' to an ADMIN user",
                e.getMessage());
    }

    @Test
    void testCreateCommand_invalidEventNumber_throws() throws SyncException {
        LocalDateTime startTime = LocalDateTime.of(2020, 5, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 5, 10, 14, 30);
        Event event = new Event("Test Event", startTime, endTime,
                "Test Location", "Test Description");
        eventManager.addEvent(event);

        Participant p2 = new Participant("john", "pw", AccessLevel.MEMBER);
        participantManager.addNewUser(p2);

        simulateInput("abc\njohn");

        SyncException e = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Invalid format. Use: <EventIndex> | <Participant Name>. Enter 'addparticipant' to try again.",
                e.getMessage());
    }

    private void simulateInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(in);
        ui.setScanner(testScanner);
    }
}
