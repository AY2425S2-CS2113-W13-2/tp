package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import event.Event;
import label.Priority;
import java.util.ArrayList;

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
            ArrayList<Event> matchingEvents = new ArrayList<>();
            ArrayList<String> allPriorities = Priority.getAllPriorities();

            if (!isValidBound(lowerBound) || !isValidBound(upperBound)) {
                throw new SyncException("Invalid priority bounds: lowerBound and upperBound must be between 1 and 3.");
            }

            if (allPriorities.size() != eventManager.size()) {
                throw new SyncException("Priority list is out of sync with events");
            }

            for (int i = 0; i < eventManager.size(); i++) {
                Event event = eventManager.getEvent(i);
                String priority = allPriorities.get(i);
                int priorityValue = Priority.getValue(priority);
                if (priorityValue >= lowerBound && priorityValue <= upperBound) {
                    matchingEvents.add(event);
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
