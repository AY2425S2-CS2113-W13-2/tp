package commandfactory;

import java.util.logging.Logger;

import command.Command;
import command.ListAllCommand;
import command.LoginCommand;
import exception.SyncException;
import ui.UI;
import participant.ParticipantManager;

/**
 * Factory class responsible for creating a ListAllCommand.
 * This factory creates a command that lists all events with optional sorting.
 */
public class ListAllCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructs a ListAllCommandFactory with the given participant manager and UI.
     *
     * @param participantManager The participant manager to check the current user's role
     * @param ui The UI to interact with the user and get the sorting input
     */
    public ListAllCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a ListAllCommand that lists all events, either sorted or unsorted.
     *
     * @return A new ListAllCommand with the selected sorting option
     * @throws SyncException If the user is not logged in or is not an admin
     */
    @Override
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting ListAllCommandFactory");
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Sorry, you need to be an ADMIN to access all events.");
        } else {
            String sort = ui.readListCommandInput();

            assert sort != null : "Input sort type should not be null";
            assert !sort.trim().isEmpty() : "Input string should not be empty";

            return new ListAllCommand(sort, ui);
        }
    }
}
