package event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import exception.SyncException;
import participant.Participant;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents an event with details such as name, time, location, and participants.
 */
public class Event {

    private static final Logger logger = Logger.getLogger(Event.class.getName());

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String description;
    private ArrayList<Participant> participants;

    /**
     * Constructs an Event with the specified details and an empty participant list.
     *
     * @param name        the name of the event
     * @param startTime   the start time of the event
     * @param endTime     the end time of the event
     * @param location    the location of the event
     * @param description the description of the event
     */
    public Event(String name, LocalDateTime startTime, LocalDateTime endTime, String location, String description) {
        assert name != null && startTime != null && endTime != null && location != null && description != null;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.participants = new ArrayList<>();
    }

    /**
     * Constructs an Event with the specified details and participants.
     *
     * @param name         the name of the event
     * @param startTime    the start time of the event
     * @param endTime      the end time of the event
     * @param location     the location of the event
     * @param description  the description of the event
     * @param participants the initial list of participants
     */
    public Event(String name, LocalDateTime startTime, LocalDateTime endTime, String location, String description,
                 ArrayList<Participant> participants) {
        assert name != null && startTime != null && endTime != null && location != null && description != null && participants != null;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setName(String name) {
        assert name != null;
        this.name = name;
    }

    public void setStartTime(LocalDateTime startTime) {
        assert startTime != null;
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        assert endTime != null;
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        assert location != null;
        this.location = location;
    }

    public void setDescription(String description) {
        assert description != null;
        this.description = description;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        assert participants != null;
        this.participants = participants;
    }

    /**
     * Duplicates the current event with a new name.
     *
     * @param newName the new name for the duplicated event
     * @return a new Event with the same details and participants
     */
    public Event duplicate(String newName) {
        assert newName != null;
        logger.info("Duplicating event '" + name + "' with new name: " + newName);
        return new Event(newName, this.startTime, this.endTime, this.location, this.description, this.participants);
    }

    /**
     * Adds a participant to the event.
     *
     * @param participant the participant to add
     * @throws SyncException if the participant is already in the event
     */
    public void addParticipant(Participant participant) throws SyncException {
        assert participant != null;
        if (!participants.contains(participant)) {
            participants.add(participant);
            logger.info("Added participant: " + participant.getName() + " to event: " + name);
        } else {
            logger.warning("Attempted to add duplicate participant: " + participant.getName() + " to event: " + name);
            throw new SyncException("Participant is already in this event");
        }
    }

    /**
     * Removes a participant by their name.
     *
     * @param participantName the name of the participant to remove
     * @return true if the participant was removed, false otherwise
     */
    public boolean removeParticipant(String participantName) {
        assert participantName != null;
        boolean removed = participants.removeIf(p -> p.getName().equalsIgnoreCase(participantName));
        if (removed) {
            logger.info("Removed participant: " + participantName + " from event: " + name);
        } else {
            logger.info("No participant named: " + participantName + " found in event: " + name);
        }
        return removed;
    }

    /**
     * Checks if a participant with the given name is in the event.
     *
     * @param participantName the name of the participant to check
     * @return true if found, false otherwise
     */
    public boolean hasParticipant(String participantName) {
        assert participantName != null;
        return participants.stream().anyMatch(
                p -> p.getName().equalsIgnoreCase(participantName));
    }

    /**
     * Checks if the given participant is in the event.
     *
     * @param participant the participant to check
     * @return true if found, false otherwise
     */
    public boolean hasParticipant(Participant participant) {
        assert participant != null;
        return participants.stream().anyMatch(
                p -> p.getName().equalsIgnoreCase(participant.getName()));
    }

    /**
     * Lists all participants in the event to the console.
     */
    public void listParticipants() {
        if (participants.isEmpty()) {
            System.out.println("No participants assigned to this event.");
        } else {
            System.out.println("Participants for event \"" + name + "\":");
            for (Participant p : participants) {
                System.out.println("- " + p);
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
                "+----------------------+--------------------------------+\n" +
                        "| Name                 | %s\n" +
                        "| Start Time           | %s\n" +
                        "| End Time             | %s\n" +
                        "| Location             | %s\n" +
                        "| Description          | %s\n" +
                        "| Participants         | %s\n" +
                        "+----------------------+--------------------------------+",
                name,
                startTime.format(formatter),
                endTime.format(formatter),
                location,
                description,
                participants
        );
    }
}
