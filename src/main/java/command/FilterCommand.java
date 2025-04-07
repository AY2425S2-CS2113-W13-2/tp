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

public class FilterCommand extends Command {
    private final int lowerBound;
    private final int upperBound;

    public FilterCommand(int lower, int upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
    }

    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        try {
            Participant currentUser = participantManager.getCurrentUser();
            if (currentUser == null) {
                ui.showMessage("Please login first to view your events.");
                return;
            }

            // Get all events with their indices
            List<Event> allEvents = eventManager.getEvents();
            List<String> allPriorities = Priority.getAllPriorities();

            // Filter events by both user assignment and priority
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