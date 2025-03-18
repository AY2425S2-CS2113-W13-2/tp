package command;

import event.EventManager;
import ui.UI;
import exception.SyncException;
import event.Event;
import java.util.ArrayList;

public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(EventManager events, UI ui) throws SyncException {
        try {
            ArrayList<Event> matchingEvents = new ArrayList<>();
            for (Event event : events.getEvents()) {
                String eventName = event.getName().trim().toLowerCase();
                String eventDescription = event.getDescription().trim().toLowerCase();
                String searchKeyword = keyword.trim().toLowerCase();

                System.out.println("Searching for: '" + searchKeyword + " in list : " ); // Debugging

                if (eventName.contains(searchKeyword) || eventDescription.contains(searchKeyword)) {
                    matchingEvents.add(event);
                }
            }
            ui.printMatchingEvents(matchingEvents);
        } catch (Exception e) {
            throw new SyncException("Error during find operation: " + e.getMessage());
        }
    }
}