package commandfactory;

import command.ByeCommand;
import command.Command;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.ParticipantManager;
import participant.Participant;
import storage.UserStorage;
import ui.UI;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ByeCommandFactoryTest {

    private ParticipantManager participantManager;
    private UI ui;
    private ByeCommandFactory factory;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("admin", "admin123", Participant.AccessLevel.ADMIN));
        UserStorage userStorage = new UserStorage("./data/test-users.txt");

        participantManager = new ParticipantManager(participants, ui, userStorage);
        factory = new ByeCommandFactory(participantManager, ui);
    }

    @Test
    void testCreateCommand_returnsByeCommand() throws SyncException {
        Command command = factory.createCommand();
        assertNotNull(command);
        assertTrue(command instanceof ByeCommand);
    }
}
