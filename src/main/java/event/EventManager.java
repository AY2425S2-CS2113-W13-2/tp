package event;

import java.util.ArrayList;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import storage.Storage;
import label.Priority;

/**
 * Manages events, including adding, deleting, updating, viewing, and checking for collisions among events.
 */
public class EventManager {
    public ArrayList<Event> events;
    private final UI ui;
    private final Storage storage;
    private final UserStorage userStorage;

    /**
     * Constructor for initializing the EventManager with a list of events, UI, storage, and userStorage.
     *
     * @param events the list of events
     * @param ui the UI instance for user interaction
     * @param storage the storage instance for saving events
     * @param userStorage the user storage for managing users
     */
    public EventManager(ArrayList<Event> events, UI ui, Storage storage, UserStorage userStorage) {
        this.events = events;
        this.ui = ui;
        this.storage = storage;
        this.userStorage = userStorage;
    }

    /**
     * Constructor for initializing the EventManager from a file path and userStorage.
     *
     * @param filePath the path to the event storage file
     * @param userStorage the user storage for managing users
     * @throws SyncException if there is an error initializing from the file
     */
    public EventManager(String filePath, UserStorage userStorage) throws SyncException {
        this.userStorage = userStorage;
        this.events = new ArrayList<>();
        this.ui = new UI();
        this.storage = new Storage(filePath, userStorage);
    }

    /**
     * Returns the list of events.
     *
     * @return the list of events
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Returns the event at the specified index.
     *
     * @param index the index of the event
     * @return the event at the specified index
     * @throws SyncException if the event index is invalid
     */
    public Event getEvent(int index) throws SyncException {
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        } else {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
    }

    /**
     * Returns the number of events.
     *
     * @return the number of events
     */
    public int size() {
        return events.size();
    }

    /**
     * Adds an event to the list of events and checks for collisions.
     *
     * @param event the event to be added
     * @throws SyncException if there is an error adding the event
     */
    public void addEvent(Event event) throws SyncException {
        assert event != null : "Event cannot be null";

        // Set the exclude index to -1 to avoid excluding any element
        ArrayList<Event> collisions = checkCollision(
                event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getLocation(),
                events,
                -1
        );

        events.add(event);
        String priority;
        try {
            priority = Priority.priorityInput();
        } catch (NoSuchElementException e) {
            priority = "NULL";
        }
        Priority.addPriority(priority);

        ui.showAddedMessage(event);

        // If collisions are detected, show the collision warning
        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(event, collisions);
        }

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Adds an event and a participant to the event, checking for availability and collisions.
     *
     * @param event the event to be added
     * @param participant the participant to be added
     * @throws SyncException if there is an error adding the event or participant
     */
    public void addEvent(Event event, Participant participant) throws SyncException {
        assert event != null : "Event cannot be null";

        // Set the exclude index to -1 to avoid excluding any element
        ArrayList<Event> collisions = checkCollision(
                event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getLocation(),
                events,
                -1
        );

        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(event, collisions);
            throw new SyncException("You can try choose a different timing or venue instead. " +
                    "Enter 'add' to try again");
        }

        if (participant == null) {
            throw new SyncException("No user is currently selected. Please enter 'login' to log in.");
        }

        if (!participant.isAvailableDuring(event.getStartTime(), event.getEndTime())) {
            throw new SyncException("Participant is not available at the given time. Enter 'add' to try again");
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

    /**
     * Views all events, showing their details.
     */
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

    /**
     * Views the specified list of events.
     *
     * @param events the list of events to view
     * @throws SyncException if there is an error viewing the events
     */
    public void viewEvents(List<Event> events) throws SyncException {
        assert events != null : "Events list should not be null";

        if (!events.isEmpty()) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                assert event != null : "Event at index " + i + " should not be null";
                String priority = Priority.getPriority(i);
                ui.showEventWithIndex(event, i + 1, priority);
            }
        } else {
            ui.showEmptyListMessage();
        }
    }

    /**
     * Deletes the event at the specified index.
     *
     * @param index the index of the event to delete
     * @throws SyncException if the event index is invalid
     */
    public void deleteEvent(int index) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
        Event deletedEvent = events.remove(index);
        Priority.removePriority(index);
        ui.showDeletedMessage(deletedEvent);
        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Updates the event at the specified index with the new event details.
     *
     * @param index the index of the event to update
     * @param updatedEvent the updated event
     * @throws SyncException if the event index is invalid or the update is invalid
     */
    public void updateEvent(int index, Event updatedEvent) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }

        // Validate start/end time relationship
        if (updatedEvent.getStartTime().isAfter(updatedEvent.getEndTime())) {
            throw new SyncException(SyncException.startTimeAfterEndTimeMessage());
        }

        if (updatedEvent.getEndTime().isBefore(updatedEvent.getStartTime())) {
            throw new SyncException(SyncException.endTimeBeforeStartTimeMessage());
        }
        Event originalEvent = events.get(index);

        if (originalEvent.equals(updatedEvent)) {
            return;
        }

        // Validate participant availability
        for (Participant p : updatedEvent.getParticipants()) {
            if (!p.isAvailableDuring(updatedEvent.getStartTime(), updatedEvent.getEndTime())) {
                throw new SyncException(SyncException.participantUnavailableDuringEditError(
                        p.getName(), updatedEvent.getStartTime(), updatedEvent.getEndTime()));
            }
        }

        // Update the event
        events.set(index, updatedEvent);

        // Collision check
        ArrayList<Event> collisions = checkCollision(
                updatedEvent.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedEvent.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedEvent.getLocation(),
                events,
                index
        );

        // If collisions are detected, show the collision warning
        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(updatedEvent, collisions);
        } else {
            ui.showEditedEvent(updatedEvent);
        }

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Duplicates the specified event with a new name and adds it to the event list.
     *
     * @param eventToDuplicate the event to duplicate
     * @param newName the new name for the duplicated event
     * @throws SyncException if there is an error duplicating the event
     */
    public void duplicateEvent(Event eventToDuplicate, String newName) throws SyncException {
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        events.add(duplicatedEvent);

        int originalIndex = events.indexOf(eventToDuplicate);
        String originalPriority = Priority.getPriority(originalIndex);
        Priority.addPriority(originalPriority);

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Checks for collisions between the specified event and other events in the list.
     *
     * @param start the start time of the event
     * @param end the end time of the event
     * @param location the location of the event
     * @param events the list of events to check for collisions
     * @param excludeIndex the index of the event to exclude from the collision check
     * @return a list of events that collide with the specified event
     */
    public ArrayList<Event> checkCollision(
            String start,
            String end,
            String location,
            ArrayList<Event> events,
            int excludeIndex
    ) {
        assert start != null : "Start time cannot be null";
        assert end != null : "End time cannot be null";
        assert location != null : "Location cannot be null";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        ArrayList<Event> collisions = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            if (i == excludeIndex) {
                continue;
            }
            Event event = events.get(i);
            boolean timeOverlap = !(event.getEndTime().isBefore(startTime) || event.getStartTime().isAfter(endTime));
            boolean sameLocation = event.getLocation().equals(location);

            if (timeOverlap && sameLocation) {
                collisions.add(event);
            }
        }
        return collisions;
    }

    /**
     * Returns the storage instance used for saving events.
     *
     * @return the storage instance
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Returns a list of events associated with the current participant.
     *
     * @param participantManager the participant manager for retrieving the current user
     * @return the list of events associated with the current participant
     */
    public ArrayList<Event> getEventsByParticipant(ParticipantManager participantManager) {
        Participant participant = participantManager.getCurrentUser();
        ArrayList<Event> events = new ArrayList<>();
        if (participant.isAdmin()) {
            events = this.events;
        } else {
            for (Event event : this.events) {
                if (event.hasParticipant(participant)) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    /**
     * Sets the list of events.
     *
     * @param events the list of events to set
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
