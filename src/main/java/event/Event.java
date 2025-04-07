package event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import command.LoginCommand;
import exception.SyncException;
import participant.Participant;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents an event that has a name, start and end time, location, description, and a list of participants.
 * Provides functionality to add/remove participants, list participants, and duplicate the event with a new name.
 */
public class Event {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String description;
    private ArrayList<Participant> participants;
    private String priority;

    /**
     * Constructs an Event with the specified details.
     *
     * @param name        The name of the event.
     * @param startTime   The start time of the event.
     * @param endTime     The end time of the event.
     * @param location    The location of the event.
     * @param description A brief description of the event.
     */
    public Event(String name, LocalDateTime startTime, LocalDateTime endTime, String location, String description) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.participants = new ArrayList<>();
    }

    /**
     * Constructs an Event with the specified details and a list of participants.
     *
     * @param name        The name of the event.
     * @param startTime   The start time of the event.
     * @param endTime     The end time of the event.
     * @param location    The location of the event.
     * @param description A brief description of the event.
     * @param participants The list of participants in the event.
     */
    public Event(String name, LocalDateTime startTime, LocalDateTime endTime, String location, String description,
                 ArrayList<Participant> participants) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.participants = participants;
    }

    /**
     * Gets the name of the event.
     *
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the start time of the event.
     *
     * @return The start time of the event.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the event.
     *
     * @return The end time of the event.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Gets the location of the event.
     *
     * @return The location of the event.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the description of the event.
     *
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the name of the event.
     *
     * @param name The new name of the event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime The new start time of the event.
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime The new end time of the event.
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the location of the event.
     *
     * @param location The new location of the event.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the description of the event.
     *
     * @param description The new description of the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Creates a duplicate of the event with a new name.
     *
     * @param newName The new name for the duplicate event.
     * @return A new Event object that is a duplicate of the current event with the new name.
     */
    public Event duplicate(String newName) {
        return new Event(newName, this.startTime, this.endTime, this.location, this.description, this.participants);
    }

    /**
     * Gets the list of participants for this event.
     *
     * @return A list of participants.
     */
    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    /**
     * Adds a participant to the event.
     *
     * @param participant The participant to add to the event.
     * @throws SyncException If the participant is already added to the event.
     */
    public void addParticipant(Participant participant) throws SyncException {
        assert participant != null : "Participant cannot be null";
        LOGGER.info("Attempting adding participant ");
        if (!participants.contains(participant)) {
            participants.add(participant);
        } else {
            throw new SyncException("Participant is already in this event");
        }
    }

    /**
     * Removes a participant from the event by their name.
     *
     * @param participantName The name of the participant to remove.
     * @return true if the participant was removed successfully, false otherwise.
     */
    public boolean removeParticipant(String participantName) {
        return participants.removeIf(p -> p.getName().equalsIgnoreCase(participantName));
    }

    /**
     * Checks if a participant with the specified name is already in the event.
     *
     * @param participantName The name of the participant.
     * @return true if the participant is in the event, false otherwise.
     */
    public boolean hasParticipant(String participantName) {
        return participants.stream().anyMatch(p -> p.getName().equalsIgnoreCase(participantName));
    }

    /**
     * Checks if a participant is already in the event.
     *
     * @param participant The participant to check.
     * @return true if the participant is in the event, false otherwise.
     */
    public boolean hasParticipant(Participant participant) {
        return participants.stream().anyMatch(p -> p.getName().equalsIgnoreCase(participant.getName()));
    }

    /**
     * Lists all participants in the event.
     * Prints the list to the console.
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

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }


    /**
     * Returns a string representation of the event, including all details and participants.
     *
     * @return A formatted string representing the event.
     */
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

    /**
     * Sets the list of participants for the event.
     *
     * @param participants The new list of participants.
     */
    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}
