package command;

import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Command to handle the login functionality for participants.
 */
public class LoginCommand extends Command {

    /**
     * Constructs a LoginCommand.
     */
    public LoginCommand() {

    }

    /**
     * Executes the login command, which prompts the ParticipantManager to handle the login process.
     *
     * @param events the EventManager instance used to manage events
     * @param ui the UI instance used to display messages to the user
     * @param participantManager the ParticipantManager instance that manages participant data
     * @throws SyncException if an error occurs during the login process
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        participantManager.login();
    }
}
