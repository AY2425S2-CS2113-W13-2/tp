package event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.SyncException;
import label.Priority;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

/**
 * Manages a list of events including add, delete, update, view, and save operations.
 */
public class EventManager {
    private static final Logger logger = Logger.getLogger(EventManager.class.getName());

    private ArrayList<Event> events;
    private final UI ui;
    private final Storage storage;
    private final UserStorage userStorage;

    /**
     * Constructs EventManager with dependencies.
     */
    public EventManager(ArrayList<Event> events, UI ui, Storage storage, UserStorage userStorage) {
        this.events = events;
        this.ui = ui;
        this.storage = storage;
        this.userStorage = userStorage;
        logger.info("EventManager initialized with external dependencies.");
    }

    /**
     * Constructs EventManager with file path for persistent storage.
     */
    public EventManager(String filePath, UserStorage userStorage) throws SyncException {
        this.userStorage = userStorage;
        this.events = new ArrayList<>();
        this.ui = new UI();
        this.storage = new Storage(filePath, userStorage);
        logger.info("EventManager initialized from file path.");
    }

    /**
     * Returns all events.
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Returns the event at the specified index.
     */
    public Event getEvent(int index) throws SyncException {
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        } else {
            logger.warning("Invalid index when getting event: " + index);
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
    }

    /**
     * Returns the number of events.
     */
    public int size() {
        return events.size();
    }

    /**
     * Adds a new event to the manager.
     */
    public void addEvent(Event event) throws SyncException {
        assert event != null : "Event cannot be null";
        logger.info("Adding event: " + event);

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
            logger.warning("No input for priority, defaulting to NULL.");
            priority = "NULL";
        }
        Priority.addPriority(priority);

        ui.showAddedMessage(event);

        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(event, collisions);
        }

        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Adds an event with participant validation.
     */
    public void addEvent(Event event, ParticipantManager participantManager) throws SyncException {
        assert event != null : "Event cannot be null";
        Participant participant = participantManager.getCurrentUser();
        logger.info("Adding event with participant: " + participant);

        ArrayList<Event> collisions = checkCollision(
                event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                event.getLocation(),
                events,
                -1
        );

        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(event, collisions);
            throw new SyncException("You can try choose a different timing or venue instead. Enter 'add' to try again");
        }

        if (participant == null) {
            logger.warning("No user is currently selected.");
            throw new SyncException("No user is currently selected. Please enter 'login' to log in.");
        }

        if (!participant.isAvailableDuring(event.getStartTime(), event.getEndTime())) {
            throw new SyncException("Participant is not available at the given time. Enter 'add' to try again");
        }

        participantManager.assignParticipant(event, participant);
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
     * Displays all events.
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
     * Displays specific list of events.
     */
    public void viewEvents(List<Event> events) throws SyncException {
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
     * Deletes the event at the specified index.
     */
    public void deleteEvent(int index) throws SyncException {
        if (index < 0 || index >= events.size()) {
            logger.warning("Invalid index during deletion: " + index);
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
        Event deletedEvent = events.remove(index);
        Priority.removePriority(index);
        ui.showDeletedMessage(deletedEvent);
        storage.saveEvents(events, Priority.getAllPriorities());
    }

    /**
     * Updates the event at a given index with a new version.
     */
    public void updateEvent(int index, Event updatedEvent) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }

        if (updatedEvent.getStartTime().isAfter(updatedEvent.getEndTime())) {
            throw new SyncException(SyncException.startTimeAfterEndTimeMessage());
        }

        if (updatedEvent.getEndTime().isBefore(updatedEvent.getStartTime())) {
            throw new SyncException(SyncException.endTimeBeforeStartTimeMessage());
        }

        Event originalEvent = events.get(index);
        if (originalEvent.equals(updatedEvent)) return;

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
     * Creates a copy of an event with a new name.
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
     * Checks if an event clashes with existing ones.
     */
    public ArrayList<Event> checkCollision(String start, String end, String location,
                                           ArrayList<Event> events, int excludeIndex) {
        assert start != null;
        assert end != null;
        assert location != null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        ArrayList<Event> collisions = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            if (i == excludeIndex) continue;
            Event event = events.get(i);
            boolean timeOverlap = !(event.getEndTime().isBefore(startTime) || event.getStartTime().isAfter(endTime));
            boolean sameLocation = event.getLocation().equals(location);
            if (timeOverlap && sameLocation) collisions.add(event);
        }
        return collisions;
    }

    /**
     * Gets storage object used by EventManager.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Gets list of events for the current participant.
     */
    public ArrayList<Event> getEventsByParticipant(ParticipantManager participantManager) {
        Participant participant = participantManager.getCurrentUser();
        ArrayList<Event> userEvents = new ArrayList<>();
        if (participant.isAdmin()) {
            userEvents = this.events;
        } else {
            for (Event event : this.events) {
                if (event.hasParticipant(participant)) {
                    userEvents.add(event);
                }
            }
        }
        return userEvents;
    }

    /**
     * Sets the event list.
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    /**
     * Saves the current state of events to storage.
     */
    public void save() throws SyncException {
        storage.saveEvents(events, Priority.getAllPriorities());
    }
}
