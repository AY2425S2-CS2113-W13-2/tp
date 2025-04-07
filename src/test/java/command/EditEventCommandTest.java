package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import event.EventManager;
import exception.SyncException;
import logger.EventSyncLogger;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class EditEventCommandTest {

    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;
    private Participant admin;
    private Participant member;
    private Event originalEvent;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    public void setUp() throws SyncException {
        ui = new UI();

        originalEvent = new Event("Event",
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0),
                "Lab", "Description");

        ArrayList<Event> events = new ArrayList<>();
        events.add(originalEvent);

        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/test-events.txt", userStorage);
        eventManager = new EventManager(events, ui, eventStorage, userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        System.setOut(new PrintStream(outputStreamCaptor));

        ArrayList<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 4, 9, 10, 0),
                LocalDateTime.of(2025, 4, 9, 14, 0)
        ));
        availableTimes.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 5, 10, 12, 0),
                LocalDateTime.of(2025, 5, 10, 16, 0)
        ));
        admin = new Participant("admin", "pw", Participant.AccessLevel.ADMIN, availableTimes);
        member = new Participant("member", "pw", Participant.AccessLevel.MEMBER, availableTimes);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(admin);
        originalEvent.setParticipants(participants);
    }

    @Test
    public void testEditEventCommand_AdminRole() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "1\nNew Event Name\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("New Event Name", originalEvent.getName());
        assertTrue(outputStreamCaptor.toString().contains("✅ Event editing completed."));
    }

    @Test
    public void testEditEventCommand_NoAdminRole() {
        participantManager.setCurrentUser(member);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        SyncException exception = assertThrows(SyncException.class, () -> {
            command.execute(eventManager, ui, participantManager);
        });

        assertEquals("You are not currently administrator.", exception.getMessage());
    }

    @Test
    public void testEditEventCommand_EditName() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "1\nNew Event Name\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("New Event Name", originalEvent.getName());
    }

    @Test
    public void testEditEventCommand_EditStartTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput= "2\n2025-05-10 12:00\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals(LocalDateTime.of(2025, 5, 10, 12, 0), originalEvent.getStartTime());
    }

    @Test
    public void testEditEventCommand_EditEndTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "3\n2025-05-10 15:00\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals(LocalDateTime.of(2025, 5, 10, 15, 0), originalEvent.getEndTime());
    }

    @Test
    public void testEditEventCommand_EditLocation() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "4\nNew Location\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("New Location", originalEvent.getLocation());
    }

    @Test
    public void testEditEventCommand_EditDescription() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "5\nNew Description\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("New Description", originalEvent.getDescription());
    }

    @Test
    public void testEditEventCommand_EditCancel() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("Event", originalEvent.getName());

        assertTrue(outputStreamCaptor.toString().contains("✅ Event editing completed."));
    }

    @Test
    public void testEditEventCommand_InvalidChoice() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "invalid\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertTrue(outputStreamCaptor.toString().contains("✅ Event editing completed."));
    }

    @Test
    public void testEditEventCommand_OutOfRangeChoice() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "7\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertTrue(outputStreamCaptor.toString().contains("✅ Event editing completed."));
    }

    @Test
    public void testEditEventCommand_InvalidStartTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "2\ninvalid format\n2025-05-10 12:30\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);
        assertTrue(outputStreamCaptor.toString().contains("Invalid format"));
    }

    @Test
    public void testEditEventCommand_CancelStartTimeEdit() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        LocalDateTime originalStartTime = originalEvent.getStartTime();

        command.execute(eventManager, ui, participantManager);

        assertEquals(originalStartTime, originalEvent.getStartTime());
    }

    @Test
    public void testEditEventCommand_CancelEndTimeEdit() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        LocalDateTime originalEndTime = originalEvent.getEndTime();

        command.execute(eventManager, ui, participantManager);

        assertEquals(originalEndTime, originalEvent.getEndTime());
    }

    @Test
    public void testEditEventCommand_StartTimeAfterEndTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "2\n2025-05-10 15:00\n2025-05-10 12:30\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals(LocalDateTime.of(2025, 5, 10, 12, 30), originalEvent.getStartTime());
    }

    @Test
    public void testEditEventCommand_EndTimeBeforeStartTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "3\n2025-05-10 12:00\n2025-05-10 15:30\n6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals(LocalDateTime.of(2025, 5, 10, 15, 30), originalEvent.getEndTime());
    }

    @Test
    public void testEditEventCommand_ParticipantConflictStartTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        ArrayList<AvailabilitySlot> limitedAvailability = new ArrayList<>();
        limitedAvailability.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0)
        ));

        Participant participantWithLimitedAvailability = new Participant("limited", "pw",
                Participant.AccessLevel.MEMBER, limitedAvailability);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participantWithLimitedAvailability);
        originalEvent.setParticipants(participants);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals(LocalDateTime.of(2025, 5, 10, 13, 0), originalEvent.getStartTime());
    }

    @Test
    public void testEditEventCommand_ParticipantConflictEndTime() throws SyncException {
        participantManager.setCurrentUser(admin);

        ArrayList<AvailabilitySlot> limitedAvailability = new ArrayList<>();
        limitedAvailability.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 5, 10, 13, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0)
        ));

        Participant participantWithLimitedAvailability = new Participant("limited", "pw",
                Participant.AccessLevel.MEMBER, limitedAvailability);

        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participantWithLimitedAvailability);
        originalEvent.setParticipants(participants);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals(LocalDateTime.of(2025, 5, 10, 14, 0), originalEvent.getEndTime());
    }

    @Test
    public void testEditEventCommand_CancelLocationEdit() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("Lab", originalEvent.getLocation());
    }

    @Test
    public void testEditEventCommand_CancelDescriptionEdit() throws SyncException {
        participantManager.setCurrentUser(admin);

        String simulatedInput = "6\n";
        simulateInput(simulatedInput);

        EditEventCommand command = new EditEventCommand(0, participantManager);

        command.execute(eventManager, ui, participantManager);

        assertEquals("Description", originalEvent.getDescription());
    }

    private void simulateInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
    }
}