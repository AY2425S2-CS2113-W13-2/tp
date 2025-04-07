package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.ListAllCommand;
import exception.SyncException;
import ui.UI;
import participant.ParticipantManager;

/**
 * Factory class for creating ListAllCommand instances.
 * Handles the process of listing all events with specific sorting criteria.
 */
public class ListAllCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(ListAllCommandFactory.class.getName());

    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructs a ListAllCommandFactory with necessary managers and UI.
     *
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     */
    public ListAllCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a ListAllCommand after validating user permissions and sorting input.
     *
     * @return ListAllCommand with specified sorting criteria
     * @throws SyncException if there are issues with user permissions or input validation
     */
    @Override
    public Command createCommand() throws SyncException {
        // Validate user login
        if (participantManager.getCurrentUser() == null) {
            LOGGER.log(Level.WARNING, "Unauthorized list all attempt: User not logged in");
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        }

        // Validate admin status
        if (!participantManager.isCurrentUserAdmin()) {
            LOGGER.log(Level.WARNING, "Unauthorized list all attempt: Non-admin user");
            throw new SyncException("Sorry, you need to be an ADMIN to access all events.");
        }

        // Read sorting input
        LOGGER.log(Level.INFO, "Preparing to create list all command");
        String sort = ui.readListCommandInput();
        LOGGER.log(Level.FINE, "Received sorting input: {0}", sort);

        // Validate sort input
        assert sort != null : "Input sort type should not be null";
        assert !sort.trim().isEmpty() : "Input string should not be empty";

        try {
            // Validate sorting criteria (you might want to add more specific validation)
            if (!isValidSortCriteria(sort)) {
                LOGGER.log(Level.WARNING, "Invalid sorting criteria: {0}", sort);
                throw new SyncException("Invalid sorting criteria.");
            }

            LOGGER.log(Level.INFO, "Creating list all command with sorting: {0}", sort);
            return new ListAllCommand(sort, ui);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating list all command", e);
            throw new SyncException("Error creating list all command: " + e.getMessage());
        }
    }

    /**
     * Validates the sorting criteria.
     *
     * @param sort The sorting input to validate
     * @return true if the sorting criteria is valid, false otherwise
     */
    private boolean isValidSortCriteria(String sort) {
        // This is a placeholder method. Implement actual validation based on your requirements
        // For example, check against a list of allowed sorting options
        return sort != null && !sort.trim().isEmpty();
    }
}
