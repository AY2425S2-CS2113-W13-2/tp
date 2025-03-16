import java.util.ArrayList;

public class EventManager {
    public ArrayList<Event> events;

    public EventManager(ArrayList<Event> events) {
        this.events = events;
    }

    public EventManager() {
        this.events = new ArrayList<>();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public Event getEvent(int index) throws SyncException {
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        } else {
            throw new SyncException("Give me a proper number!!!");
        }
    }

    public int size(){
        return events.size();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void duplicateEvent(Event eventToDuplicate, String newName) {
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        events.add(duplicatedEvent);
    }
}