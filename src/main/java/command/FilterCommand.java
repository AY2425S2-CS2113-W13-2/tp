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
import java.util.stream.Collectors;

/**
 * Command that filters events based on priority bounds for the current user.
 * It checks the assigned events to the user and filters them based on the priority range.
 */
public class FilterCommand extends Command {
    private final int lowerBound;
    private final int upperBound;

    /**
     * Constructor to initialize the FilterCommand with a lower and upper priority bound.
     *
     * @param lower The lower bound for filtering events by priority
     * @param upper The upper bound for filtering events by priority
     */
    public FilterCommand(int lower, int upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
    }


    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }


    /**
     * Executes the command to filter events based on the priority bounds.
     * The method validates the bounds, checks if the user is logged in, and filters the events accordingly.
     *
     * @param eventManager The manager for handling events
     * @param ui The UI interface for displaying messages to the user
     * @param participantManager The manager for handling participant data
     * @throws SyncException If there is an error while executing the command, such as invalid bounds or missing user
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        try {
            if (!isValidBound(lowerBound) || !isValidBound(upperBound)) {
                throw new SyncException("Invalid priority bounds: lowerBound and upperBound must be between 1 and 3.");
            }

            Participant currentUser = participantManager.getCurrentUser();
            if (currentUser == null) {
                ui.showMessage("Please login first to view your events.");
                return;
            }

            List<Event> userEvents = eventManager.getEvents().stream()
                    .filter(event -> event.hasParticipant(currentUser))
                    .collect(Collectors.toList());

            if (userEvents.isEmpty()) {
                ui.showMessage("No events assigned to you.");
                return;
            }

            ArrayList<Event> matchingEvents = new ArrayList<>();
            ArrayList<String> allPriorities = Priority.getAllPriorities();

            if (allPriorities.size() != eventManager.size()) {
                throw new SyncException("Priority list is out of sync with events");
            }

            for (int i = 0; i < eventManager.size(); i++) {
                Event event = eventManager.getEvent(i);
                if (event.hasParticipant(currentUser)) {  // Check if user is assigned
                    String priority = allPriorities.get(i);
                    int priorityValue = Priority.getValue(priority);
                    if (priorityValue >= lowerBound && priorityValue <= upperBound) {
                        matchingEvents.add(event);
                    }
                }
            }
            ui.printMatchingEvents(matchingEvents);
        } catch (Exception e) {
            throw new SyncException("Error filtering events: " + e.getMessage());
        }
    }

    /**
     * Validates if the given priority bound is within the acceptable range (1 to 3).
     *
     * @param bound The priority bound to validate
     * @return true if the bound is valid (between 1 and 3), otherwise false
     */
    private boolean isValidBound(int bound) {
        return bound >= 1 && bound <= 3;
    }
}
