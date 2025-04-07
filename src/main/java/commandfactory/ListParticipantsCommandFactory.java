package commandfactory;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.ListParticipantsCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class for creating ListParticipantsCommand instances.
 * Handles the process of selecting and listing participants for a specific event.
 */
public class ListParticipantsCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(ListParticipantsCommandFactory.class.getName());

    private final UI ui;
    private final EventManager eventManager;
    private final ParticipantManager participantManager;

    /**
     * Constructs a ListParticipantsCommandFactory with necessary managers and UI.
     *
     * @param ui User interface for interaction
     * @param eventManager Manages event-related operations
     * @param participantManager Manages participant-related operations
     */
    public ListParticipantsCommandFactory(UI ui, EventManager eventManager, ParticipantManager participantManager) {
        this.ui = ui;
        this.eventManager = eventManager;
        this.participantManager = participantManager;
    }

    /**
     * Creates a ListParticipantsCommand after validating user permissions and event selection.
     *
     * @return ListParticipantsCommand for the selected event
     * @throws SyncException if there are issues with user permissions or event selection
     */
    public Command createCommand() throws SyncException {
        // Validate user login
        Participant participant = participantManager.getCurrentUser();
        if (participant == null) {
            LOGGER.log(Level.WARNING, "Unauthorized list participants attempt: User not logged in");
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        // Get events for the current participant
        ArrayList<Event> events = eventManager.getEventsByParticipant(participantManager);

        // Validate events availability
        if (events.isEmpty()) {
            LOGGER.log(Level.INFO, "No events available for user: {0}", participant.getName());
            throw new SyncException("No events available.");
        }

        assert events != null : "Events list should not be null";
        assert !events.isEmpty() : "Events list should not be empty";

        // Event selection loop
        while (true) {
            try {
                // Show available events
                showAllEvents(events);

                // Prompt for event selection
                ui.showMessage("Enter event index to list participants (or type 'exit' to cancel):");
                String input = ui.readLine().trim();

                // Check for exit command
                ui.checkForExit(input);

                // Parse and validate event index
                int eventIndex = parseEventIndex(input, events);

                // Create and execute list participants command
                LOGGER.log(Level.INFO, "Preparing to list participants for event: {0}", events.get(eventIndex).getName());
                Command listCommand = new ListParticipantsCommand(eventIndex);
                listCommand.execute(eventManager, ui, participantManager);

            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid input format: {0}", e.getMessage());
                ui.showMessage("Invalid input. Please enter a valid number or 'exit'.");
            } catch (SyncException e) {
                LOGGER.log(Level.WARNING, "Error listing participants: {0}", e.getMessage());
                ui.showMessage("Error: " + e.getMessage());
            }

            ui.showMessage("--------------------------------------------------");
        }
    }

    /**
     * Displays all available events to the user.
     *
     * @param events List of events to display
     */
    private void showAllEvents(ArrayList<Event> events) {
        assert events != null : "Events list should not be null";

        LOGGER.log(Level.FINE, "Displaying available events");
        ui.showMessage("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            ui.showMessage((i + 1) + ". " + events.get(i).getName());
        }
    }

    /**
     * Parses and validates the event index from user input.
     *
     * @param input User input string
     * @param events List of available events
     * @return Validated event index
     * @throws SyncException if the index is invalid
     */
    private int parseEventIndex(String input, ArrayList<Event> events) throws SyncException {
        assert input != null : "Input should not be null";
        assert events != null : "Events list should not be null";

        try {
            int eventIndex = Integer.parseInt(input) - 1;

            if (eventIndex < 0 || eventIndex >= events.size()) {
                LOGGER.log(Level.WARNING, "Invalid event index: {0}", eventIndex);
                ui.showMessage("Invalid index. Please try again.");
                throw new SyncException("Invalid event index.");
            }

            return eventIndex;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Non-numeric input received: {0}", input);
            throw new SyncException("Invalid input format. Please enter a valid number.");
        }
    }
}
