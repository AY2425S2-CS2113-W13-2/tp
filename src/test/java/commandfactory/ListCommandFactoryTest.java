package commandfactory;

import command.Command;
import command.ListCommand;
import exception.SyncException;
import event.EventManager;
import logger.EventSyncLogger;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListCommandFactoryTest {

    private ListCommandFactory listCommandFactory;
    private EventManager eventManager;
    private UI ui;
    private ParticipantManager participantManager;
    private Participant participant;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        participant = new Participant("john_doe", "password123", Participant.AccessLevel.MEMBER, new ArrayList<>());

        UserStorage userStorage = new UserStorage("./data/test-users.txt");

        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));
        participant = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, availableTimes);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participant);
        participantManager = new ParticipantManager(participants, ui, null);
        participantManager.setCurrentUser(participant);

        listCommandFactory = new ListCommandFactory(participantManager, ui);

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testCreateCommandWithValidInput() throws SyncException {
        String simulatedInput = "end\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        Command command = listCommandFactory.createCommand();

        assertNotNull(command, "Command should not be null");
        assertTrue(command instanceof ListCommand, "Command should be an instance of ListCommand");
    }

    @Test
    void testCreateCommandWithEmptyInput() {
        String simulatedInput = "\n"; // 空输入，模拟换行符
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
        SyncException exception = assertThrows(SyncException.class, () -> {
            listCommandFactory.createCommand();
        });
    }

    @Test
    void testCreateCommandWithValidEventIndex() throws SyncException {
        String simulatedInput = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        Command command = listCommandFactory.createCommand();

        assertNotNull(command, "Command should not be null");
        assertTrue(command instanceof ListCommand, "Command should be an instance of ListCommand");
    }
}
