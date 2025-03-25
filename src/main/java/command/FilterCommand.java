package command;

import event.EventManager;
import ui.UI;
import exception.SyncException;
import event.Event;
import java.util.ArrayList;

public class FilterCommand extends Command {
    private final int lowerFilter;
    private final int upperFilter;

    public FilterCommand(int lower, int upper) {
        this.lowerFilter = lower;
        this.upperFilter = upper;
    }

    @Override
    public void execute(EventManager events, UI ui) throws SyncException {
        try {
            ArrayList<Event> matchingEvents = new ArrayList<>();
            for (Event event : events.getEvents()) {
                String eventName = event.getName().trim().toLowerCase();
                String eventDescription = event.getDescription().trim().toLowerCase();
                int priority = event.getPriority();
                System.out.println("Filtering for priority from " + lowerFilter +
                        " to " + upperFilter + " in list : " );
                if (priority >= lowerFilter && priority <= upperFilter) {
                    matchingEvents.add(event);
                }
            }
            ui.printMatchingEvents(matchingEvents);
        } catch (Exception e) {
            throw new SyncException("Error during find operation: " + e.getMessage());
        }
    }
}