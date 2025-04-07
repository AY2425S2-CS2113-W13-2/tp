package commandfactory;

import command.Command;
import command.ListCommand;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class responsible for creating a ListCommand.
 * This factory creates a command that lists events with a specified sorting order.
 */
public class ListCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final ui.UI ui;

    /**
     * Constructs a ListCommandFactory with the given participant manager and UI.
     *
     * @param participantManager The participant manager to check the current user's role
     * @param ui The UI to interact with the user and get the sorting input
     */
    public ListCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a ListCommand that lists events with the selected sorting order.
     *
     * @return A new ListCommand with the selected sorting option
     * @throws SyncException If the user provides an invalid or empty sorting type
     */
    @Override
    public Command createCommand() throws SyncException {
        String sortType = ui.readListCommandInput();

        if (sortType.equals("")) {
            throw new SyncException("List sort type is empty. Please enter 'list' and try again.");
        } else if (sortType.equals("asc")) {
            // You can add specific logic here for sorting in ascending order
        }

        return new ListCommand(sortType);
    }
}
