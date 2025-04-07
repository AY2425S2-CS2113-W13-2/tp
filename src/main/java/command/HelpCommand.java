package command;

import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to display the help menu.
 * This command shows available commands and their usage to the user.
 */
public class HelpCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(HelpCommand.class.getName());

    /**
     * Constructs a new HelpCommand.
     * Initializes the command to display help information.
     */
    public HelpCommand() {
        LOGGER.info("HelpCommand created");
    }

    /**
     * Executes the command to show the help menu.
     * Displays a list of available commands and their usage through the UI.
     *
     * @param events The event manager (not used in this command)
     * @param ui The user interface to display the help menu
     * @param participants The participant manager (not used in this command)
     * @throws SyncException if there is an error displaying the help menu
     * @throws AssertionError if ui is null
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        LOGGER.info("Executing HelpCommand - showing help menu");

        assert ui != null : "UI cannot be null";

        try {
            ui.showMenu();
            LOGGER.info("Help menu displayed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error displaying help menu", e);
            throw new SyncException("Error displaying help menu: " + e.getMessage());
        }
    }
}
