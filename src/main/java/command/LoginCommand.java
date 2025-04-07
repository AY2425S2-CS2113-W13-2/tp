package command;

import java.util.logging.Logger;

import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Command to handle the login functionality for participants.
 */
public class LoginCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

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
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting to log in user");
        participantManager.login();
    }
}
