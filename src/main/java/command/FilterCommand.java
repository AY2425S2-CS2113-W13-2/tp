package command;

import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import event.Event;
import label.Priority;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Represents a command to filter events based on priority.
 * This command filters events assigned to the current user that have priorities
 * within the specified bounds.
 */
public class FilterCommand extends Command {
    private final int lowerBound;
    private final int upperBound;
    private static final Logger LOGGER = Logger.getLogger(FilterCommand.class.getName());

    /**
     * Constructs a new FilterCommand with the specified priority bounds.
     *
     * @param lower The lower bound of the priority range (inclusive)
     * @param upper The upper bound of the priority range (inclusive)
     * @throws AssertionError if bounds are outside the valid range
     */
    public FilterCommand(int lower, int upper) {
        this.lowerBound = lower;
        this.upperBound = upper;

        assert lower <= upper : "Lower bound must not exceed upper bound";

        LOGGER.info("FilterCommand created with priority bounds: " + lower + " to " + upper);
    }

    /**
     * Executes the command to filter events based on priority.
     * Shows the user events that match the specified priority range.
     *
     * @param eventManager The event manager containing the events
     * @param ui The user interface for displaying messages and results
     * @param participantManager The participant manager for accessing user information
     * @throws SyncException if there are invalid bounds, synchronization issues, or other errors
     * @throws AssertionError if any of the parameters are null
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing FilterCommand with bounds: " + lowerBound + " to " + upperBound);

        assert eventManager != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            if (!isValidBound(lowerBound) || !isValidBound(upperBound)) {
                LOGGER.warning("Invalid priority bounds: " + lowerBound + ", " + upperBound);
                throw new SyncException("Invalid priority bounds: lowerBound and upperBound must be between 1 and 3.");
            }

            Participant currentUser = participantManager.getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("No user is currently logged in");
                ui.showMessage("Please login first to view your events.");
                return;
            }
            LOGGER.info("Current user: " + currentUser.getName());

            List<Event> userEvents = eventManager.getEvents().stream()
                    .filter(event -> event.hasParticipant(currentUser))
                    .collect(Collectors.toList());

            if (userEvents.isEmpty()) {
                LOGGER.info("No events assigned to current user");
                ui.showMessage("No events assigned to you.");
                return;
            }
            LOGGER.info("Found " + userEvents.size() + " events assigned to current user");

            ArrayList<Event> matchingEvents = new ArrayList<>();
            ArrayList<String> allPriorities = Priority.getAllPriorities();

            if (allPriorities.size() != eventManager.size()) {
                LOGGER.severe("Priority list size (" + allPriorities.size() +
                        ") doesn't match event count (" + eventManager.size() + ")");
                throw new SyncException("Priority list is out of sync with events");
            }

            for (int i = 0; i < eventManager.size(); i++) {
                Event event = eventManager.getEvent(i);
                if (event.hasParticipant(currentUser)) {  // Check if user is assigned
                    String priority = allPriorities.get(i);
                    int priorityValue = Priority.getValue(priority);
                    if (priorityValue >= lowerBound && priorityValue <= upperBound) {
                        LOGGER.fine("Event matches filter criteria: " + event.getName() +
                                " (Priority: " + priority + ")");
                        matchingEvents.add(event);
                    }
                }
            }

            LOGGER.info("Found " + matchingEvents.size() + " events matching priority filter");
            ui.printMatchingEvents(matchingEvents);
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException while filtering events", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while filtering events", e);
            throw new SyncException("Error filtering events: " + e.getMessage());
        }
    }

    /**
     * Checks if the given bound is valid for priority filtering.
     *
     * @param bound The priority bound to validate
     * @return true if the bound is valid (between 1 and 3 inclusive), false otherwise
     */
    private boolean isValidBound(int bound) {
        return bound >= 1 && bound <= 3;
    }
}
