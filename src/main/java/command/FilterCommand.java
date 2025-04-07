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
 * Represents a command to filter events based on participant priority.
 * This command filters the events the current user is assigned to, and
 * displays those events that have a priority value within a specified range.
 */
public class FilterCommand extends Command {

    private final int lowerBound;
    private final int upperBound;

    /**
     * Constructs a new FilterCommand with the specified priority range.
     *
     * @param lower the lower bound of the priority range (inclusive)
     * @param upper the upper bound of the priority range (inclusive)
     */
    public FilterCommand(int lower, int upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
    }

    /**
     * Executes the filter command, which filters the events based on the current user's
     * assigned events and their priority values within the given range.
     * The events are displayed if they match the priority filter criteria.
     *
     * @param eventManager the EventManager instance that manages the events
     * @param ui the UI instance used to display messages to the user
     * @param participantManager the ParticipantManager instance that manages participants
     * @throws SyncException if an error occurs while processing the command
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        try {
            Participant currentUser = participantManager.getCurrentUser();

            if (currentUser == null) {
                ui.showMessage("Please login first to view your events.");
                return;
            }

            List<Event> allEvents = eventManager.getEvents();
            List<String> allPriorities = Priority.getAllPriorities();

            List<Event> matchingEvents = new ArrayList<>();

            for (int i = 0; i < allEvents.size(); i++) {
                Event event = allEvents.get(i);
                String priority = allPriorities.get(i);

                if (event.hasParticipant(currentUser)) {
                    int priorityValue = Priority.getValue(priority);
                    if (priorityValue >= lowerBound && priorityValue <= upperBound) {
                        matchingEvents.add(event);
                    }
                }
            }

            if (matchingEvents.isEmpty()) {
                ui.showMessage("No events match your filter criteria.");
            } else {
                ui.printMatchingEvents((ArrayList<Event>) matchingEvents);
            }

        } catch (Exception e) {
            throw new SyncException("Error filtering events: " + e.getMessage());
        }
    }
}
