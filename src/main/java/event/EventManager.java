package event;

import java.util.ArrayList;

import participant.Participant;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import storage.Storage;
import label.Priority;


public class EventManager {
    public ArrayList<Event> events;
    private final UI ui;
    private final Storage storage;
    private final UserStorage userStorage;

    public EventManager(ArrayList<Event> events, UI ui, Storage storage, UserStorage userStorage) {
        this.events = events;
        this.ui = ui;
        this.storage = storage;
        this.userStorage = userStorage;
    }

    public EventManager(String filePath, UserStorage userStorage) throws SyncException {
        this.userStorage = userStorage;
        this.events = new ArrayList<>();
        this.ui = new UI();
        this.storage = new Storage(filePath, userStorage);
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
                event.getLocation(),
                events
        );

        // If collisions are detected, show the collision warning
        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(event, collisions);
        }

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    public void addEvent(Event event, Participant participant) throws SyncException {
        assert event != null : "Event cannot be null";

        ArrayList<Event> collisions = checkCollision(
                event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getLocation(),
                events
        );

        if (!collisions.isEmpty()) {
            throw new SyncException("Event conflicts with existing events.");
        }

        if (participant == null) {
            throw new SyncException("No user is currently selected.");
        }

        if (!participant.isAvailableDuring(event.getStartTime(), event.getEndTime()) ) {
            throw new SyncException("Participant is not available at the given time.");
        }

        event.addParticipant(participant);
        events.add(event);

        String priority;
        try {
            priority = Priority.priorityInput();
        } catch (NoSuchElementException e) {
            priority = "NULL";
        }
        Priority.addPriority(priority);

        ui.showAddedMessage(event);
        storage.saveEvents(events, Priority.getAllPriorities());
    }


    public void viewAllEvents() {
        assert events != null : "Events list should not be null";

        if (!events.isEmpty()) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                assert event != null : "Event at index " + i + " should not be null";
                ui.showEventWithIndex(event, i + 1, Priority.getPriority(i));
            }
        } else {
            ui.showEmptyListMessage();
        }
    }

    public void viewEvents(List<Event> events) throws SyncException {
        assert events != null : "Events list should not be null";

        if (!events.isEmpty()) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                assert event != null : "Event at index " + i + " should not be null";
                String priority = Priority.getPriority(i);
                ui.showEventWithIndex(event, i + 1,priority);
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
                updatedEvent.getLocation(),
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

    public ArrayList<Event> checkCollision(String start, String end, String location, ArrayList<Event> events) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        ArrayList<Event> collisions = new ArrayList<>();

        for (int i = 0; i < events.size() - 1; i++) {
            Event event = events.get(i);
            boolean timeOverlap = !(event.getEndTime().isBefore(startTime) || event.getStartTime().isAfter(endTime));
            boolean sameLocation = event.getLocation().equals(location);
            if (timeOverlap && sameLocation) {
                collisions.add(event);
            }
        }
        return collisions;
    }

    public Storage getStorage() {
        return storage;
    }
}
