package command;

import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to log in a user.
 * This command handles the user authentication process.
 */
public class LoginCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    /**
     * Constructs a new LoginCommand.
     * Initializes the command for user login.
     */
    public LoginCommand() {
        LOGGER.info("LoginCommand created");
    }

    /**
     * Executes the command to log in a user.
     * Delegates to the participant manager to handle the login process.
     *
     * @param events The event manager (not used in this command)
     * @param ui The user interface (not used directly in this command)
     * @param participantManager The participant manager that handles the login process
     * @throws SyncException if there is an error during the login process
     * @throws AssertionError if participantManager is null
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing LoginCommand");

        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            LOGGER.info("Initiating login process");
            participantManager.login();
            LOGGER.info("Login process completed successfully");
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException during login process", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login process", e);
            throw new SyncException("Error during login: " + e.getMessage());
        }
    }
}
