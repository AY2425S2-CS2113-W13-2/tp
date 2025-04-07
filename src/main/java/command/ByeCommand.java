package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Represents a command to terminate the application.
 * A {@code ByeCommand} displays a farewell message and signals the system to exit.
 */
public class ByeCommand extends Command {

    /**
     * Executes the command to display the goodbye message.
     *
     * @param events the manager handling event data.
     * @param ui the user interface used for displaying messages.
     * @param participantManager the manager handling participant data.
     * @throws SyncException if there is an error during execution.
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        ui.showByeMessage();
    }

    /**
     * Returns whether this command signals the application to exit.
     *
     * @return {@code true} since this command indicates program termination.
     */
    public boolean isExit() {
        return true;
    }
}
