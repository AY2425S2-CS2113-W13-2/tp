package commandfactory;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.DeleteCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class for creating DeleteCommand instances.
 * Handles the process of selecting and deleting events by authorized users.
 */
public class DeleteCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(DeleteCommandFactory.class.getName());

    private final ParticipantManager participantManager;
    private final ui.UI ui;
    private final EventManager eventManager;

    /**
     * Constructs a DeleteCommandFactory with necessary managers and UI.
     *
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     * @param eventManager Manages event-related operations
     */
    public DeleteCommandFactory(ParticipantManager participantManager, ui.UI ui, EventManager eventManager) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    /**
     * Creates a DeleteCommand after validating user permissions and selecting an event.
     *
     * @return DeleteCommand for the selected event
     * @throws SyncException if there are issues with user permissions or event selection
     */
    public Command createCommand() throws SyncException {
        // Validate user login and admin status
        if (participantManager.getCurrentUser() == null) {
            LOGGER.log(Level.WARNING, "Unauthorized delete attempt: User not logged in");
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        }

        if (!participantManager.isCurrentUserAdmin()) {
            LOGGER.log(Level.WARNING, "Unauthorized delete attempt: Non-admin user");
            throw new SyncException("Only admin can delete events!");
        }

        // Read event name to delete
        String name = ui.readDeleteName();
        ArrayList<Event> matchingEvents = findMatchingEvents(name);

        // Validate matching events
        if (matchingEvents.isEmpty()) {
            LOGGER.log(Level.INFO, "No events found with name: {0}", name);
            throw new SyncException("No events found with the name: " + name);
        }

        // Select event to delete
        Event eventToDelete;
        if (matchingEvents.size() == 1) {
            eventToDelete = matchingEvents.get(0);
        } else {
            ui.showMatchingEventsWithIndices(matchingEvents, eventManager);
            int eventIndex = readDeleteEventIndex(matchingEvents);
            eventToDelete = matchingEvents.get(eventIndex);
        }

        // Verify event still exists
        int actualIndex = eventManager.getEvents().indexOf(eventToDelete);
        if (actualIndex == -1) {
            LOGGER.log(Level.WARNING, "Attempt to delete non-existent event");
            throw new SyncException("Event no longer exists.");
        }

        LOGGER.log(Level.INFO, "Preparing to delete event: {0}", eventToDelete.getName());
        return new DeleteCommand(actualIndex);
    }

    /**
     * Finds events matching the given name.
     *
     * @param name Partial or full name of the event to find
     * @return List of events matching the name
     */
    private ArrayList<Event> findMatchingEvents(String name) {
        assert name != null : "Event name cannot be null";

        ArrayList<Event> matchingEvents = new ArrayList<>();
        for (Event event : eventManager.getEvents()) {
            if (event.getName().toLowerCase().contains(name.toLowerCase())) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents;
    }

    /**
     * Reads the index of the event to delete from user input.
     *
     * @param matchingEvents List of events matching the search criteria
     * @return Index of the event to delete
     * @throws SyncException if index is invalid
     */
    private int readDeleteEventIndex(ArrayList<Event> matchingEvents) throws SyncException {
        assert matchingEvents != null && !matchingEvents.isEmpty() : "Matching events list cannot be null or empty";

        ui.showMessage("Enter the index of the event you want to delete: ");
        try {
            Scanner scanner = new Scanner(System.in);
            int index = Integer.parseInt(scanner.nextLine()) - 1;

            if (index < 0 || index >= matchingEvents.size()) {
                LOGGER.log(Level.WARNING, "Invalid event index entered: {0}", index);
                throw new SyncException("Invalid event index. Please enter a valid index.");
            }

            return eventManager.getEvents().indexOf(matchingEvents.get(index));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid index format entered");
            throw new SyncException("Invalid index format. Please enter a number.");
        }
    }
}
