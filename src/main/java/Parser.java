import java.util.ArrayList;

public class Parser {
    private final EventManager eventManager;
    private final UI ui;
//    private final Storage storage;

    public Parser(EventManager eventManager, UI ui) {
        this.eventManager = eventManager;
        this.ui = ui;
//        this.storage = storage;
    }

    public void parse(String input) throws SyncException {

        if (!input.equalsIgnoreCase("bye")) {
            return;
        }
        if (input.toLowerCase().startsWith("find ")) {
            find(input);
        } else if (input.toLowerCase().startsWith("duplicate ")) {
            duplicate(input);
        } else {
            throw new SyncException("Unknown command ");
        }
    }

    private void find(String input) throws SyncException {
        String keyword = input.substring(5).trim().toLowerCase();
        if (keyword.isEmpty()) {
            throw new SyncException("Keyword empty! Type properly");
        }

        ArrayList<Event> matchingEvents = new ArrayList<>();
        for (Event event : eventManager.getEvents()) {
            if (event.getDescription().toLowerCase().contains(keyword)) {
                matchingEvents.add(event);
            }
        }
        ui.printMatchingEvents(matchingEvents);
    }

    private void duplicate(String input) throws SyncException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            throw new SyncException("Invalid duplicate command format. Use: duplicate index New Event Name");
        }

        try {
            int index = Integer.parseInt(parts[0]) - 1;
            if (index >= 0 && index < eventManager.getEvents().size()) {
                Event eventToDuplicate = eventManager.getEvents().get(index);
                Event duplicatedEvent = eventToDuplicate.duplicate(parts[1]);
                eventManager.addEvent(duplicatedEvent);
                ui.showMessage("Event duplicated: " + duplicatedEvent);
            } else {
                throw new SyncException("Invalid event index.");
            }
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid index format. Use a number.");
        }
    }
}