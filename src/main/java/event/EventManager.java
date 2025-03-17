package event;

import java.util.ArrayList;
import ui.UI;
import exception.SyncException;

public class EventManager {
    public ArrayList<Event> events;
    private UI ui;

    public EventManager(ArrayList<Event> events, UI ui) {
        this.events = events;
        this.ui = ui;
    }

    public EventManager() {
        this.events = new ArrayList<>();
        this.ui = new UI();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public Event getEvent(int index) throws SyncException {
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        } else {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
    }

    public int size(){
        return events.size();
    }

    public void addEvent(Event event) {
        events.add(event);
        ui.showAddedMessage(event);
    }

    public void viewAllEvents() {
        if (events.size() > 0) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                ui.showEventWithIndex(event, i + 1);
            }
        } else {
            ui.showEmptyListMessage();
        }

    }

    public void deleteEvent(int index) throws SyncException {
        return;
    }

    public void duplicateEvent(Event eventToDuplicate, String newName) {
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        events.add(duplicatedEvent);
    }
}
