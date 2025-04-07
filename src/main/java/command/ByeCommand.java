package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to exit the application.
 * This command is used to terminate the program and display a goodbye message to the user.
 */
public class ByeCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ByeCommand.class.getName());

    /**
     * Constructs a new ByeCommand.
     * Initializes the command for application exit.
     */
    public ByeCommand() {
        LOGGER.info("ByeCommand created");
    }

    /**
     * Executes the command to exit the application.
     * Displays a goodbye message to the user through the UI.
     *
     * @param events The event manager
     * @param ui The user interface to display the goodbye message
     * @param participantManager The participant manager
     * @throws SyncException if there is a synchronization error when showing the message
     * @throws AssertionError if ui is null
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing ByeCommand - application exit requested");
        assert ui != null : "UI cannot be null";

        try {
            ui.showByeMessage();
            LOGGER.info("Goodbye message displayed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error displaying goodbye message", e);
            throw e;
        }
    }

    /**
     * Indicates whether this command should cause the application to exit.
     *
     * @return true as this command always signals application exit
     */
    public boolean isExit() {
        LOGGER.info("isExit() called, returning true");
        return true;
    }
}
