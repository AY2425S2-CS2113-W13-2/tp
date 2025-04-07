package command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.SyncException;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import event.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginCommandTest {

    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;

    @BeforeEach
    public void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        ArrayList<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(
                LocalDateTime.of(2025, 4, 9, 10, 0),
                LocalDateTime.of(2025, 4, 9, 14, 0)
        ));
        Participant admin = new Participant("admin", "pw", Participant.AccessLevel.ADMIN, availableTimes);
        participantManager.addNewUser(admin);
    }

    @Test
    public void testExecuteLogin() throws SyncException {
        String simulatedInput = "admin\npw\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        LoginCommand command = new LoginCommand();
        command.execute(eventManager, ui, participantManager);

        assertTrue(participantManager.getCurrentUser() != null,
                "ParticipantManager should be logged in after calling login.");
    }
}
