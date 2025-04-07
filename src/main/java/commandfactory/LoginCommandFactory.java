package commandfactory;

import java.util.logging.Logger;

import command.Command;
import command.LoginCommand;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class responsible for creating a LoginCommand.
 * This factory generates a command to log in a user if they are not already logged in.
 */
public class LoginCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    private final ParticipantManager participantManager;

    /**
     * Constructs a LoginCommandFactory with the given participant manager.
     *
     * @param participantManager The participant manager for managing user sessions
     */
    public LoginCommandFactory(ParticipantManager participantManager) {
        this.participantManager = participantManager;
    }

    /**
     * Creates a LoginCommand if the user is not already logged in.
     *
     * @return A LoginCommand to log the user in
     * @throws SyncException If the user is already logged in and tries to log in again
     */
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting LoginCommandFactory");
        if (participantManager.getCurrentUser() == null) {
            return new LoginCommand();
        } else {
            throw new SyncException("You are already logged in. Please enter 'logout' to log out first.");
        }
    }
}
