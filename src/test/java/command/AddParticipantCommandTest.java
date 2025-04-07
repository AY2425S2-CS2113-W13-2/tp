package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import event.EventManager;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

class AddParticipantCommandTest {

    private UI ui;
    private EventManager eventManager;
    private ParticipantManager participantManager;
    private Participant admin;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();

        ArrayList<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 4, 9, 10, 0),
                LocalDateTime.of(2025, 4, 9, 14, 0)
        ));
        admin = new Participant("admin", "pw", Participant.AccessLevel.ADMIN, availableTimes);

        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        participantManager.setCurrentUser(admin);
        participantManager.addNewUser(admin);

        Storage eventStorage = new Storage("./data/test-events.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        Event event = new Event(
                "Team Meeting",
                LocalDateTime.of(2025, 4, 9, 11, 0),
                LocalDateTime.of(2025, 4, 9, 13, 0),
                "na", "na"
        );
        eventManager.addEvent(event);
    }

    @Test
    void testExecute_addExistingParticipantSuccessfully() throws SyncException {
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        slots.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 4, 9, 10, 0),
                LocalDateTime.of(2025, 4, 9, 14, 0)
        ));
        Participant bob = new Participant("Bob", "pw", Participant.AccessLevel.MEMBER, slots);
        participantManager.addNewUser(bob);

        AddParticipantCommand command = new AddParticipantCommand(0, "Bob",
                ui, participantManager);
        command.execute(eventManager, ui, participantManager);

        Event event = eventManager.getEvent(0);
        assertTrue(event.getParticipants().contains(bob), "Participant should be added to the event");
    }

    @Test
    void testExecute_addNonexistentParticipantCreatesNew() throws SyncException {
        simulateInput("Y\nNewGuy\npw\n1\n1\n2025-04-09 10:00\n2025-04-09 14:00");

        AddParticipantCommand command = new AddParticipantCommand(0, "NewGuy",
                ui, participantManager);
        command.execute(eventManager, ui, participantManager);

        Participant created = participantManager.getParticipant("NewGuy");
        assertNotNull(created);
        assertEquals("NewGuy", created.getName());

        Event event = eventManager.getEvent(0);
        assertTrue(event.getParticipants().contains(created));
    }

    @Test
    void testExecute_addUnavailableParticipant() throws SyncException {
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        slots.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 4, 9, 14, 0),
                LocalDateTime.of(2025, 4, 9, 16, 0)
        ));
        Participant eve = new Participant("Eve", "pw", Participant.AccessLevel.MEMBER, slots);
        participantManager.addNewUser(eve);

        AddParticipantCommand command = new AddParticipantCommand(0, "Eve",
                ui, participantManager);
        command.execute(eventManager, ui, participantManager);

        Event event = eventManager.getEvent(0);
        assertFalse(event.getParticipants().contains(eve));
    }

    private void simulateInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        ui.setScanner(scanner);
    }
}
