package commandfactory;

import command.Command;
import command.LoginCommand;
import exception.SyncException;
import participant.ParticipantManager;

import java.util.logging.Logger;

/**
 * A factory class responsible for creating {@link LoginCommand} instances.
 * Ensures that a login command is only created when there is no user currently logged in.
 */
public class LoginCommandFactory implements CommandFactory {

    private static final Logger logger = Logger.getLogger(LoginCommandFactory.class.getName());

    private final ParticipantManager participantManager;

    /**
     * Constructs a {@code LoginCommandFactory} with the specified {@code ParticipantManager}.
     *
     * @param participantManager the participant manager to check the current login status
     */
    public LoginCommandFactory(ParticipantManager participantManager) {
        assert participantManager != null : "ParticipantManager must not be null";
        this.participantManager = participantManager;
    }

    /**
     * Creates a {@link LoginCommand} if no user is currently logged in.
     *
     * @return a new {@code LoginCommand} instance
     * @throws SyncException if a user is already logged in
     */
    @Override
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager is unexpectedly null during command creation";

        if (participantManager.getCurrentUser() == null) {
            logger.info("Creating LoginCommand: no user is currently logged in.");
            return new LoginCommand();
        } else {
            logger.warning("LoginCommand creation failed: user already logged in.");
            throw new SyncException("You are already logged in. Please enter 'logout' to log out first.");
        }
    }
}
