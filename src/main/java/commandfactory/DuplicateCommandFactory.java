package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.DuplicateCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class for creating DuplicateCommand instances.
 * Handles the process of duplicating events by authorized users.
 */
public class DuplicateCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(DuplicateCommandFactory.class.getName());

    private final ParticipantManager participantManager;
    private final ui.UI ui;
    private final EventManager eventManager;

    /**
     * Constructs a DuplicateCommandFactory with necessary managers and UI.
     *
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     * @param eventManager Manages event-related operations
     */
    public DuplicateCommandFactory(ParticipantManager participantManager, ui.UI ui, EventManager eventManager) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    /**
     * Creates a DuplicateCommand after validating user permissions and input.
     *
     * @return DuplicateCommand for the specified event
     * @throws SyncException if there are issues with user permissions or input validation
     */
    public Command createCommand() throws SyncException {
        // Validate user login and admin status
        if (participantManager.getCurrentUser() == null) {
            LOGGER.log(Level.WARNING, "Unauthorized duplicate attempt: User not logged in");
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        }

        if (!participantManager.isCurrentUserAdmin()) {
            LOGGER.log(Level.WARNING, "Unauthorized duplicate attempt: Non-admin user");
            throw new SyncException("Only admin can duplicate events!");
        }

        // Read duplicate event input
        String input = ui.readDuplicateEventInput();
        assert input != null : "Duplicate event input cannot be null";

        // Split input into index and new name
        String[] parts = input.split(" ", 2);

        // Validate input format
        if (parts.length < 2) {
            LOGGER.log(Level.WARNING, "Invalid duplicate command format: {0}", input);
            throw new SyncException("Invalid duplicate command format. Use: duplicate index New Event Name");
        }

        try {
            // Parse event index
            int index = Integer.parseInt(parts[0]) - 1;
            assert index >= 0 : "Event index must be non-negative";

            // Validate event index
            if (index < eventManager.getEvents().size()) {
                Event eventToDuplicate = eventManager.getEvents().get(index);
                String newName = parts[1];

                LOGGER.log(Level.INFO, "Preparing to duplicate event: {0} with new name: {1}",
                        new Object[]{eventToDuplicate.getName(), newName});

                return new DuplicateCommand(eventToDuplicate, newName);
            } else {
                LOGGER.log(Level.WARNING, "Invalid event index entered: {0}", index);
                throw new SyncException("Invalid event index.");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index format: {0}", parts[0]);
            throw new SyncException("Invalid index format. Use a number.");
        }
    }
}
