package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.EditEventCommand;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class for creating EditEventCommand instances.
 * Handles the process of editing events by authorized users.
 */
public class EditCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(EditCommandFactory.class.getName());

    private final ParticipantManager participantManager;
    private final EventManager eventManager;
    private final UI ui;

    /**
     * Constructs an EditCommandFactory with necessary managers and UI.
     *
     * @param participantManager Manages participant-related operations
     * @param eventManager Manages event-related operations
     * @param ui User interface for interaction
     */
    public EditCommandFactory(ParticipantManager participantManager, EventManager eventManager, UI ui) {
        this.participantManager = participantManager;
        this.eventManager = eventManager;
        this.ui = ui;
    }

    /**
     * Creates an EditEventCommand after validating user permissions.
     *
     * @return EditEventCommand for the selected event
     * @throws SyncException if there are issues with user permissions or event selection
     */
    @Override
    public Command createCommand() throws SyncException {
        // Validate user login
        if (participantManager.getCurrentUser() == null) {
            LOGGER.log(Level.WARNING, "Unauthorized edit attempt: User not logged in");
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        }

        // Validate admin status
        if (!participantManager.isCurrentUserAdmin()) {
            LOGGER.log(Level.WARNING, "Unauthorized edit attempt: Non-admin user");
            throw new SyncException("Only admin can edit events!");
        }

        // Show available events
        ui.showMessage("\n📅 Here are the available events to edit:\n");
        eventManager.viewAllEvents();

        // Read event index to edit
        int index = readEditEventIndex();

        LOGGER.log(Level.INFO, "Preparing to edit event at index: {0}", index);
        return new EditEventCommand(index, this.participantManager);
    }

    /**
     * Reads the index of the event to edit from user input.
     *
     * @return Index of the event to edit
     * @throws SyncException if input is invalid or user wants to exit
     */
    private int readEditEventIndex() throws SyncException {
        assert eventManager != null : "Event manager cannot be null";

        ui.showMessage("\nEnter event index to edit (or type 'exit' to cancel): ");

        // Check if input stream is available
        if (!UI.scanner.hasNextLine()) {
            LOGGER.log(Level.WARNING, "No input received for event edit");
            throw new SyncException("❌ No input received. Edit cancelled.");
        }

        // Read and process input
        String input = ui.readLine().trim().toLowerCase();

        // Check for exit command
        ui.checkForExit(input);

        try {
            // Parse and validate event index
            int index = Integer.parseInt(input) - 1;

            // Additional index validation
            if (index < 0 || index >= eventManager.getEvents().size()) {
                LOGGER.log(Level.WARNING, "Invalid event index entered: {0}", index);
                throw new SyncException(SyncException.invalidEventIndexErrorMessage());
            }

            return index;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index format entered: {0}", input);
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
    }
}
