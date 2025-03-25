package event;

import java.util.ArrayList;
import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import storage.Storage;


public class EventManager {
    public ArrayList<Event> events;
    private UI ui;
    private Storage storage;

    public EventManager(ArrayList<Event> events, UI ui, Storage storage) {
        this.events = events;
        this.ui = ui;
        this.storage = storage;
    }

    public EventManager(String filePath) {
        this.events = new ArrayList<>();
        this.ui = new UI();
        this.storage = new Storage(filePath);
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

    public void addEvent(Event event) throws SyncException{
        assert event != null : "Event cannot be null";

        events.add(event);
        ui.showAddedMessage(event);

        ArrayList<Event> collisions = checkCollision(
                event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                events
        );

        // If collisions are detected, show the collision warning
        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(event, collisions);
        }
        storage.saveEvents(events);
    }

    public void viewAllEvents() {
        assert events != null : "Events list should not be null";

        if (events.size() > 0) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                assert event != null : "Event at index " + i + " should not be null";
                ui.showEventWithIndex(event, i + 1);
            }
        } else {
            ui.showEmptyListMessage();
        }
    }

    public void deleteEvent(int index) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
        Event deletedEvent = events.remove(index);
        ui.showDeletedMessage(deletedEvent);
    }
    //Make sure the events are updated and checks for collisions
    public void updateEvent(int index, Event updatedEvent) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }

        // Update the event at the given index with the updated event
        events.set(index, updatedEvent);

        // Check for conflicts after editing the event
        ArrayList<Event> collisions = checkCollision(
                updatedEvent.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedEvent.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                events
        );

        // If collisions are detected, show the collision warning
        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(updatedEvent, collisions);
        } else {
            ui.showEditedEvent(updatedEvent);
        }
        storage.saveEvents(events);
    }
    public void duplicateEvent(Event eventToDuplicate, String newName) {
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        events.add(duplicatedEvent);
        storage.saveEvents(events);
    }

    public ArrayList<Event> checkCollision (String start, String end, ArrayList<Event> events) throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        ArrayList<Event> collisions = new ArrayList<>();

        for (int i = 0; i < events.size() - 1; i++) {
            Event event = events.get(i);
            if (!(event.getEndTime().isBefore(startTime) || event.getStartTime().isAfter(endTime))) {
                collisions.add(event);
            }
        }
        return collisions;
    }
}