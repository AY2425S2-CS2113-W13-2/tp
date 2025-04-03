package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import ui.UI;
import storage.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;

class CreateUserCommandTest {
    private CreateUserCommand createUserCommand;
    private Participant participant;
    private ParticipantManager participantManager;
    private UI ui;
    private UserStorage userStorage;
    private EventManager eventManager;

    @BeforeEach
    void setUp() throws SyncException {
        participant = new Participant("john_doe", "password123", Participant.AccessLevel.MEMBER, new ArrayList<>());

        userStorage = new UserStorage("./data/test-users.txt");

        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        ui = new UI();
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        createUserCommand = new CreateUserCommand(participant);
    }

    @Test
    void testExecuteSuccessfullyAddsNewUser() throws SyncException {
        createUserCommand.execute(eventManager, ui, participantManager);

        assertTrue(participantManager.getParticipants().contains(participant),
                "Participant should be added to the ParticipantManager.");
    }

    @Test
    void testExecuteThrowsSyncExceptionWhenAddNewUserFails() throws SyncException {
        participantManager.addNewUser(participant);

        SyncException thrown = assertThrows(SyncException.class, () ->
                createUserCommand.execute(eventManager, ui, participantManager));

        assertEquals("User already exists.", thrown.getMessage());
    }
}
