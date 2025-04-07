package command;

import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import storage.Storage;
import storage.UserStorage;
import ui.UI;
import participant.ParticipantManager;
import event.EventManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    private UI ui;
    private ParticipantManager participantManager;
    private EventManager eventManager;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws SyncException {
        System.setOut(new PrintStream(outputStreamCaptor));

        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
    }

    @Test
    void testExecute_ShowsMenu() throws SyncException {
        HelpCommand helpCommand = new HelpCommand();
        helpCommand.execute(eventManager, ui, participantManager);

        String output = outputStreamCaptor.toString();

        assertTrue(output.contains("MENU"), "The menu should be shown.");
    }
}
