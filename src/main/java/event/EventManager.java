package event;

import java.util.ArrayList;

import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import storage.Storage;
import label.Priority;


public class EventManager {
    public ArrayList<Event> events;
    private final UI ui;
    private final Storage storage;

    public EventManager(ArrayList<Event> events, UI ui, Storage storage) {
        this.events = events;
        this.ui = ui;
        this.storage = storage;
    }

    public EventManager(String filePath) throws SyncException {
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

    public int size() {
        return events.size();
    }

    public void addEvent(Event event) {
        assert event != null : "Event cannot be null";

        events.add(event);
        String priority;
        try {
            priority = Priority.priorityInput();
        } catch (NoSuchElementException e) {
            priority = "NULL";
        }
        Priority.addPriority(priority);

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

        storage.saveEvents(events, Priority.getAllPriorities());
    }


    public void viewAllEvents() {
        assert events != null : "Events list should not be null";

        if (!events.isEmpty()) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                assert event != null : "Event at index " + i + " should not be null";
                String priority = Priority.getPriority(i);
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
        Priority.removePriority(index);
        ui.showDeletedMessage(deletedEvent);
        storage.saveEvents(events, Priority.getAllPriorities());
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
        storage.saveEvents(events, Priority.getAllPriorities());
    }

    public void duplicateEvent(Event eventToDuplicate, String newName) {
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        events.add(duplicatedEvent);

        int originalIndex = events.indexOf(eventToDuplicate);
        String originalPriority = Priority.getPriority(originalIndex);
        Priority.addPriority(originalPriority);

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    public ArrayList<Event> checkCollision(String start, String end, ArrayList<Event> events) {
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

    public Storage getStorage() {
        return storage;
    }

}
