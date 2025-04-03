package command;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.AddEventCommand;
import event.Event;
import event.EventManager;
import participant.AvailabilitySlot;
import participant.ParticipantManager;
import participant.Participant;
import exception.SyncException;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class LoginCommandTest {

    private ParticipantManager participantManager;
    private UI ui;
    private Storage storage;
    private EventManager eventManager;
    private Participant participant;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));

        Participant testUser = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, availableTimes);
        participantManager.addNewUser(testUser);
        EventManager eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        Event event = new Event("Test Event",
                LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0),
                "Lab", "Test Desc");

        outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testLoginWithValidCredentials() throws SyncException {
        String simulatedInput = "john_doe\npassword123\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        participantManager.login();
        assertNotNull(participantManager.getCurrentUser());
        assertEquals("john_doe", participantManager.getCurrentUser().getName());
    }

    @Test
    void testLoginWithInvalidUsername() throws SyncException {
        String simulatedInput = "invalid_user\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        participantManager.login();
        assertTrue(outputStream.toString().contains("User not found"));
    }

    @Test
    void testLoginWithInvalidPassword() throws SyncException {
        String simulatedInput = "john_doe\nwrong_password\nno\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        participantManager.login();
        assertTrue(outputStream.toString().contains("Wrong password"));
    }
}
