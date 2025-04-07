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
 * Manages events, including creation, deletion, updating, and participant assignments.
 * It also handles event collision checks, stores events in persistent storage, and interacts with the UI.
 */
public class EventManager {
    private ArrayList<Event> events;
    private final UI ui;
    private final Storage storage;
    private final UserStorage userStorage;

    /**
     * Constructs an EventManager with the specified event list, UI, storage, and user storage.
     *
     * @param events      the list of events to manage.
     * @param ui          the UI used for displaying information.
     * @param storage     the storage system for saving events.
     * @param userStorage the user storage system.
     */
    public EventManager(ArrayList<Event> events, UI ui, Storage storage, UserStorage userStorage) {
        this.events = events;
        this.ui = ui;
        this.storage = storage;
        this.userStorage = userStorage;
    }

    /**
     * Constructs an EventManager by loading events from a file and initializing necessary components.
     *
     * @param filePath    the file path from which to load events.
     * @param userStorage the user storage system.
     * @throws SyncException if there is an error during synchronization.
     */
    public EventManager(String filePath, UserStorage userStorage) throws SyncException {
        this.userStorage = userStorage;
        this.events = new ArrayList<>();
        this.ui = new UI();
        this.storage = new Storage(filePath, userStorage);
    }

    /**
     * Returns the list of all events managed by the EventManager.
     *
     * @return the list of events.
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Returns a specific event by its index.
     *
     * @param index the index of the event.
     * @return the event at the specified index.
     * @throws SyncException if the index is invalid.
     */
    public Event getEvent(int index) throws SyncException {
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        } else {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
    }

    /**
     * Returns the number of events managed by the EventManager.
     *
     * @return the number of events.
     */
    public int size() {
        return events.size();
    }

    /**
     * Adds a new event to the event list after checking for any collisions.
     * If collisions are found, a warning is displayed.
     *
     * @param event the event to be added.
     * @throws SyncException if there is a collision or other error.
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
     * Adds a new event and assigns a participant to the event.
     *
     * @param event              the event to be added.
     * @param participantManager the participant manager that provides the current user.
     * @throws SyncException if there are any issues with participant availability or event collision.
     */
    public void addEvent(Event event, ParticipantManager participantManager) throws SyncException {
        assert event != null : "Event cannot be null";
        Participant participant = participantManager.getCurrentUser();

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
        } else {
            participantManager.assignParticipant(event, participant);
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
     * Displays all events managed by the EventManager.
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
     * Displays a list of specified events.
     *
     * @param events the list of events to display.
     * @throws SyncException if there is an error with event retrieval.
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
     * Deletes an event by its index.
     *
     * @param index the index of the event to be deleted.
     * @throws SyncException if the index is invalid.
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
     * Updates an existing event with new details.
     *
     * @param index        the index of the event to update.
     * @param updatedEvent the new event details.
     * @throws SyncException if there are any issues with the update (e.g., invalid times, participant availability).
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

        for (Participant p : originalEvent.getParticipants()) {
            p.unassignEventTime(originalEvent.getStartTime(), originalEvent.getEndTime());
        }

        for (Participant p : originalEvent.getParticipants()) {
            if (!p.isAvailableDuring(updatedEvent.getStartTime(), updatedEvent.getEndTime())) {
                for (Participant recover : originalEvent.getParticipants()) {
                    recover.assignEventTime(originalEvent.getStartTime(), originalEvent.getEndTime());
                }
                throw new SyncException(SyncException.participantUnavailableDuringEditError(
                        p.getName(), updatedEvent.getStartTime(), updatedEvent.getEndTime()));
            }
        }

        events.set(index, updatedEvent);
        for (Participant p : updatedEvent.getParticipants()) {
            p.assignEventTime(updatedEvent.getStartTime(), updatedEvent.getEndTime());
        }

        events.set(index, updatedEvent);

        ArrayList<Event> collisions = checkCollision(
                updatedEvent.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedEvent.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedEvent.getLocation(),
                events,
                index
        );

        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(updatedEvent, collisions);
        } else {
            ui.showEditedEvent(updatedEvent);
        }

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Duplicates an existing event with a new name and adds it to the event list.
     *
     * @param eventToDuplicate the event to be duplicated.
     * @param newName          the new name for the duplicated event.
     * @throws SyncException if there is an error during the duplication process.
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
     * Checks for collisions between the specified event and the existing events.
     * A collision occurs when an event overlaps in time and location with another event.
     *
     * @param start        the start time of the event to check for collisions.
     * @param end          the end time of the event to check for collisions.
     * @param location     the location of the event to check for collisions.
     * @param events       the list of existing events to compare against.
     * @param excludeIndex the index of the event to exclude from the collision
     *                     check (typically the event being edited).
     * @return a list of events that collide with the specified event.
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
     * Returns the storage system used for managing events.
     *
     * @return the storage system.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Returns the list of events that a specific participant is involved in.
     * If the participant is an admin, all events are returned.
     *
     * @param participantManager the participant manager that provides the current user.
     * @return a list of events the participant is involved in.
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
     * Sets the list of events managed by the EventManager.
     *
     * @param events the list of events to set.
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    /**
     * Saves the current events and their associated priorities to persistent storage.
     *
     * @throws SyncException if there is an error saving the events.
     */
    public void save() throws SyncException {
        storage.saveEvents(events, Priority.getAllPriorities());
    }
}
