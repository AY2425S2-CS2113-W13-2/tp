package commandfactory;

import java.util.logging.Logger;

import command.Command;
import command.ListCommand;
import command.LoginCommand;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class responsible for creating instances of the ListCommand.
 * The ListCommand is used to list events or participants with a specific sorting order.
 */
public class ListCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    private final ParticipantManager participantManager;
    private final ui.UI ui;

    /**
     * Constructs a ListCommandFactory with the provided ParticipantManager and UI.
     *
     * @param participantManager the participant manager used to manage participants.
     * @param ui the user interface used to interact with the user.
     */
    public ListCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a new ListCommand based on user input.
     * The user is prompted to provide a sorting type for listing events or participants.
     *
     * @return a new ListCommand object with the specified sort type.
     * @throws SyncException if the sort type input is empty or invalid.
     */
    @Override
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting ListCommandFactory");
        String sortType = ui.readListCommandInput();
        if (sortType.equals("")) {
            throw new SyncException("List sort type is empty. Please enter 'list' and try again.");
        }
        return new ListCommand(sortType);
    }
}
