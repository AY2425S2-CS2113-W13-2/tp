package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import java.util.logging.Logger;

/**
 * Represents a command to log out the current user.
 * This command handles the user logout process.
 */
public class LogOutCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(LogOutCommand.class.getName());

    /**
     * Constructs a new LogOutCommand.
     * Initializes the command for user logout.
     */
    public LogOutCommand() {
        LOGGER.info("LogOutCommand created");
    }

    /**
     * Executes the command to log out the current user.
     * Delegates to the participant manager to handle the logout process
     * and displays a logout message.
     *
     * @param events The event manager (not used in this command)
     * @param ui The user interface to display the logout message
     * @param participants The participant manager that handles the logout process
     * @throws SyncException if there is an error during the logout process
     * @throws AssertionError if ui or participants is null
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        LOGGER.info("Executing LogOutCommand");

        assert ui != null : "UI cannot be null";
        assert participants != null : "ParticipantManager cannot be null";
        participants.logout();
        ui.showLogOutMessage();
    }
}
