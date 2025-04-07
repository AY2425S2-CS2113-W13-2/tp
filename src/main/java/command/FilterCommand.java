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

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

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

<<<<<<< HEAD
            if (!isValidBound(lowerBound) || !isValidBound(upperBound)) {
                throw new SyncException("Invalid priority bounds: lowerBound and upperBound must be between 1 and 3.");
            }

            for (Event event : userEvents) {
                String priority = event.getPriority();
                if (priority != null) {
=======
            if (allPriorities.size() != eventManager.size()) {
                throw new SyncException("Priority list is out of sync with events");
            }

            for (int i = 0; i < eventManager.size(); i++) {
                Event event = eventManager.getEvent(i);
                if (event.hasParticipant(currentUser)) {  // Check if user is assigned
                    String priority = allPriorities.get(i);
>>>>>>> 7ab5b74ca23220b4e8e5b04485de041d5fcd95d2
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

    private boolean isValidBound(int bound) {
        return bound >= 1 && bound <= 3;
    }
}
