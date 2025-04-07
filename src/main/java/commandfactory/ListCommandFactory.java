package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.ListCommand;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class for creating ListCommand instances.
 * Handles the process of creating a command to list events with specified sorting.
 */
public class ListCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(ListCommandFactory.class.getName());

    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructs a ListCommandFactory with necessary managers and UI.
     *
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     */
    public ListCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a ListCommand after validating the sort type input.
     *
     * @return ListCommand with specified sort type
     * @throws SyncException if the sort type input is invalid
     */
    @Override
    public Command createCommand() throws SyncException {
        // Read list command input
        LOGGER.log(Level.INFO, "Preparing to create list command");
        String sortType = ui.readListCommandInput();
        LOGGER.log(Level.FINE, "Received sort type: {0}", sortType);

        // Validate sort type
        assert sortType != null : "Sort type should not be null";

        if (sortType.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty sort type provided");
            throw new SyncException("List sort type is empty. Please enter 'list' and try again.");
        }

        try {
            // Additional validation can be added here if needed
            LOGGER.log(Level.INFO, "Creating list command with sort type: {0}", sortType);
            return new ListCommand(sortType);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating list command", e);
            throw new SyncException("Error creating list command: " + e.getMessage());
        }
    }
}
