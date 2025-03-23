package event;

import java.util.ArrayList;
import ui.UI;
import exception.SyncException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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

    public void viewAllEventsSorted(String primary, String secondary) {
        assert events != null : "Events list should not be null";

        if (events.isEmpty()) {
            ui.showEmptyListMessage();
            return;
        }

        for (int i = 0; i < events.size() - 1; i++) {
            for (int j = i + 1; j < events.size(); j++) {
                Event e1 = events.get(i);
                Event e2 = events.get(j);
                boolean shouldSwap = false;

                if (primary.equalsIgnoreCase("priority")) {
                    if (comparePriority(e1.getPriority(), e2.getPriority()) > 0) {
                        shouldSwap = true;
                    } else if (e1.getPriority().equalsIgnoreCase(e2.getPriority())
                            && secondary.equalsIgnoreCase("end")) {
                        if (e1.getEndTime().isAfter(e2.getEndTime())) {
                            shouldSwap = true;
                        }
                    }
                } else if (primary.equalsIgnoreCase("start")) {
                    if (e1.getStartTime().isAfter(e2.getStartTime())) {
                        shouldSwap = true;
                    } else if (e1.getStartTime().isEqual(e2.getStartTime())
                            && secondary.equalsIgnoreCase("priority")) {
                        if (comparePriority(e1.getPriority(), e2.getPriority()) > 0) {
                            shouldSwap = true;
                        }
                    }
                } else if (primary.equalsIgnoreCase("end")) {
                    if (e1.getEndTime().isAfter(e2.getEndTime())) {
                        shouldSwap = true;
                    } else if (e1.getEndTime().isEqual(e2.getEndTime())
                            && secondary.equalsIgnoreCase("priority")) {
                        if (comparePriority(e1.getPriority(), e2.getPriority()) > 0) {
                            shouldSwap = true;
                        }
                    }
                }

                if (shouldSwap) {
                    events.set(i, e2);
                    events.set(j, e1);
                }
            }
        }

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            assert event != null : "Event at index " + i + " should not be null";
            ui.showEventWithIndex(event, i + 1);
        }
    }

    private int comparePriority(String p1, String p2) {
        int v1 = getPriorityValue(p1);
        int v2 = getPriorityValue(p2);
        return Integer.compare(v1, v2);
    }

    private int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 1;
            case "medium":
                return 2;
            case "low":
                return 3;
            default:
                return 4;
        }
    }


    public void deleteEvent(int index) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
        Event deletedEvent = events.remove(index);
        ui.showDeletedMessage(deletedEvent);
    }

    public void updateEvent(int index, Event updatedEvent) throws SyncException {
        if (index < 0 || index >= events.size()) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }

        events.set(index, updatedEvent);

        ArrayList<Event> collisions = checkCollision(
                updatedEvent.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                updatedEvent.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                events
        );

        if (!collisions.isEmpty()) {
            ui.showCollisionWarning(updatedEvent, collisions);
        } else {
            ui.showEditedEvent(updatedEvent);
        }
    }
    public void duplicateEvent(Event eventToDuplicate, String newName) {
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        events.add(duplicatedEvent);
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
