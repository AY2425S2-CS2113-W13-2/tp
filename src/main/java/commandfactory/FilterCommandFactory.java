package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.FilterCommand;
import exception.SyncException;
import label.Priority;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class for creating FilterCommand instances.
 * Handles the process of filtering events by priority levels.
 */
public class FilterCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(FilterCommandFactory.class.getName());

    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructs a FilterCommandFactory with necessary managers and UI.
     *
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     */
    public FilterCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a FilterCommand after validating user permissions and input.
     *
     * @return FilterCommand with specified priority range
     * @throws SyncException if there are issues with user permissions or input validation
     */
    public Command createCommand() throws SyncException {
        // Validate user login
        Participant participant = participantManager.getCurrentUser();
        if (participant == null) {
            LOGGER.log(Level.WARNING, "Unauthorized filter attempt: User not logged in");
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        // Read filter input
        LOGGER.log(Level.INFO, "Preparing to create filter command");
        String input = ui.readFilterInput();
        LOGGER.log(Level.FINE, "Received filter input: {0}", input);

        // Validate input
        assert input != null : "Input string should not be null";
        assert !input.trim().isEmpty() : "Input string should not be empty";

        // Split input
        String[] stringParts = input.split(" ");
        assert stringParts.length > 0 : "Split result should not be empty";

        // Validate input parts
        if (stringParts.length != 1 && stringParts.length != 2) {
            LOGGER.log(Level.WARNING, "Invalid number of input parts: {0}", stringParts.length);
            throw new SyncException("Please provide one or two priority levels (e.g.,'LOW', 'LOW MEDIUM')");
        }

        try {
            // Process priority levels
            String lowerPriority = stringParts[0].toUpperCase();
            String upperPriority = (stringParts.length == 1) ? lowerPriority : stringParts[1].toUpperCase();

            // Validate priority levels
            if (!Priority.isValid(lowerPriority) || !Priority.isValid(upperPriority)) {
                LOGGER.log(Level.WARNING, "Invalid priority levels: lower={0}, upper={1}",
                        new Object[]{lowerPriority, upperPriority});
                throw new SyncException(SyncException.invalidPriorityFilterErrorMessage());
            }

            // Convert priorities to numeric values
            int lower = Priority.getValue(lowerPriority);
            int upper = Priority.getValue(upperPriority);

            // Validate priority range
            if (lower > upper) {
                LOGGER.log(Level.WARNING, "Invalid priority range: lower={0}, upper={1}",
                        new Object[]{lower, upper});
                throw new SyncException(SyncException.invalidBoundErrorMessage());
            }

            LOGGER.log(Level.INFO, "Creating filter command with priority range: {0}-{1}",
                    new Object[]{lower, upper});
            return new FilterCommand(lower, upper);

        } catch (SyncException e) {
            // Re-throw known sync exceptions
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception during filter command creation", e);
            throw new SyncException(SyncException.invalidBoundErrorMessage());
        }
    }
}
