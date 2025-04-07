package commandfactory;

import java.util.logging.Logger;

import command.Command;
import command.LogOutCommand;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class responsible for creating a LogOutCommand.
 * This factory generates a command to log out the current user if they are logged in.
 */
public class LogOutCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LogOutCommandFactory.class.getName());
    private final ParticipantManager participantManager;

    /**
     * Constructs a LogOutCommandFactory with the given participant manager.
     *
     * @param participantManager The participant manager for managing user sessions
     */
    public LogOutCommandFactory(ParticipantManager participantManager) {
        this.participantManager = participantManager;
    }

    /**
     * Creates a LogOutCommand if the user is currently logged in.
     *
     * @return A LogOutCommand to log the user out
     * @throws SyncException If the user is not logged in and attempts to log out
     */
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting to create LogOutCommand");
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to log in first.");
        } else {
            return new LogOutCommand();
        }
    }
}
