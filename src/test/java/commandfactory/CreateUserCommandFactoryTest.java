package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.Command;
import command.CreateUserCommand;
import exception.SyncException;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CreateUserCommandFactoryTest {

    private UI ui;
    private ParticipantManager participantManager;
    private CreateUserCommandFactory factory;
    private Participant participant;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
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
        factory = new CreateUserCommandFactory(ui, participantManager);
    }

    @Test
    void testCreateCommandWithValidInputs() throws SyncException {
        // Simulate valid input for creating a participant
        simulateInput("John Doe\npassword123\n1\n1\n2025-04-08 09:00\n2025-04-08 17:00");

        Command command = factory.createCommand();

        assertNotNull(command);
        assertTrue(command instanceof CreateUserCommand);
        CreateUserCommand createUserCommand = (CreateUserCommand) command;
        Participant participant = createUserCommand.getParticipant();
        assertEquals("John Doe", participant.getName());
        assertEquals(Participant.AccessLevel.ADMIN, participant.getAccessLevel());
        assertFalse(participant.getAvailableTimes().isEmpty());
    }

    @Test
    void testThrowsIfParticipantExists() throws SyncException {
        participantManager.addNewUser(new Participant("John Doe", "password123",
                Participant.AccessLevel.ADMIN));

        simulateInput("John Doe\nnewpassword123\n1\n1\n2025-04-08 09:00\n2025-04-08 17:00");

        SyncException exception = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals("Participant John Doe already exists. Please enter 'create' and try another name.",
                exception.getMessage());
    }

    @Test
    void testThrowsIfNoAvailabilitySlots() throws SyncException {
        simulateInput("Jane Doe\npassword123\n1\n0");

        SyncException exception = assertThrows(SyncException.class, () -> factory.createCommand());
        assertTrue(exception.getMessage().contains("Number of availability slots must be at least 1"));
    }

    @Test
    void testThrowsIfEndBeforeStart() throws SyncException {
        simulateInput("Alice\npassword123\n1\n1\n2025-04-08 17:00\n2025-04-08 09:00");

        SyncException exception = assertThrows(SyncException.class, () -> factory.createCommand());
        assertTrue(exception.getMessage().contains("End time must be after start time"));

    }

    @Test
    void testThrowsIfInvalidNumberOfSlots() throws SyncException {
        simulateInput("Bob\npassword123\n1\n-1");

        SyncException exception = assertThrows(SyncException.class, () -> factory.createCommand());
        assertEquals(true,
                exception.getMessage().contains("Number of availability slots must be at least 1"));
    }

    private void simulateInput(String input) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
    }
}
