package commandfactory;

import command.Command;
import command.LogOutCommand;
import exception.SyncException;
import participant.ParticipantManager;

import java.util.logging.Logger;

/**
 * A factory class responsible for creating {@link LogOutCommand} instances.
 * Ensures that a logout command is only created when there is a user currently logged in.
 */
public class LogOutCommandFactory implements CommandFactory {

    private static final Logger logger = Logger.getLogger(LogOutCommandFactory.class.getName());

    private final ParticipantManager participantManager;

    /**
     * Constructs a {@code LogOutCommandFactory} with the specified {@code ParticipantManager}.
     *
     * @param participantManager the participant manager to check the current login status
     */
    public LogOutCommandFactory(ParticipantManager participantManager) {
        assert participantManager != null : "ParticipantManager must not be null";
        this.participantManager = participantManager;
    }

    /**
     * Creates a {@link LogOutCommand} if a user is currently logged in.
     *
     * @return a new {@code LogOutCommand} instance
     * @throws SyncException if no user is currently logged in
     */
    @Override
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager is unexpectedly null during command creation";

        if (participantManager.getCurrentUser() == null) {
            logger.warning("LogOutCommand creation failed: no user currently logged in.");
            throw new SyncException("You are not logged in. Please enter 'login' to log in first.");
        } else {
            logger.info("Creating LogOutCommand: user is currently logged in.");
            return new LogOutCommand();
        }
    }
}
