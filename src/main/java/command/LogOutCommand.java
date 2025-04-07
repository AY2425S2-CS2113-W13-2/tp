package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Command to handle the logout functionality for participants.
 */
public class LogOutCommand extends Command {

    /**
     * Executes the logout command, which logs out the participant and displays a logout message.
     *
     * @param events the EventManager instance used to manage events
     * @param ui the UI instance used to display messages to the user
     * @param participants the ParticipantManager instance that manages participant data
     * @throws SyncException if an error occurs during the logout process
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        participants.logout();
        ui.showLogOutMessage();
    }
}
