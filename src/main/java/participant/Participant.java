package participant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a participant in the system, with a name, password, access level, and availability slots.
 * The participant can be an admin or a member and can have availability slots that indicate when they are free.
 */
public class Participant {
    private final String name;
    private final String password;
    private AccessLevel accessLevel;
    private List<AvailabilitySlot> availableTimes;

    /**
     * Enum representing the access level of a participant.
     */
    public enum AccessLevel { ADMIN, MEMBER }

    /**
     * Constructs a Participant object with the given name, password, and access level.
     *
     * @param name The name of the participant.
     * @param password The password of the participant.
     * @param accessLevel The access level of the participant (ADMIN or MEMBER).
     */
    public Participant(String name, String password, AccessLevel accessLevel) {
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>();
    }

    /**
     * Constructs a Participant object with the given name, password, access level, and availability slots.
     *
     * @param name The name of the participant.
     * @param password The password of the participant.
     * @param accessLevel The access level of the participant (ADMIN or MEMBER).
     * @param availableTimes The list of availability slots for the participant.
     */
    public Participant(String name, String password, AccessLevel accessLevel, List<AvailabilitySlot> availableTimes) {
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>(availableTimes); // Defensive copy
    }

    /**
     * Assigns a new event time to the participant by checking their availability.
     * If the participant has overlapping availability, the slot will be split or removed accordingly.
     *
     * @param eventStart The start time of the event.
     * @param eventEnd The end time of the event.
     * @return True if the event time was successfully assigned, false otherwise.
     * @throws IllegalArgumentException If the event end time is before the start time.
     */
    public boolean assignEventTime(LocalDateTime eventStart, LocalDateTime eventEnd) {
        if (eventStart.isAfter(eventEnd)) {
            throw new IllegalArgumentException("Event end time must be after start time.");
        }

        List<AvailabilitySlot> newSlots = new ArrayList<>();
        boolean assigned = false;

        for (AvailabilitySlot slot : availableTimes) {
            if (slot.getEndTime().isBefore(eventStart) || slot.getStartTime().isAfter(eventEnd)) {
                newSlots.add(slot);
            } else {
                assigned = true;

                if (slot.getStartTime().isBefore(eventStart)) {
                    newSlots.add(new AvailabilitySlot(slot.getStartTime(), eventStart.minusMinutes(1)));
                }

                if (slot.getEndTime().isAfter(eventEnd)) {
                    newSlots.add(new AvailabilitySlot(eventEnd.plusMinutes(1), slot.getEndTime()));
                }
            }
        }

        if (assigned) {
            availableTimes = newSlots;
            return true;
        }
        return false;
    }

    /**
     * Checks if the participant is available during the given time period.
     *
     * @param start The start time to check for availability.
     * @param end The end time to check for availability.
     * @return True if the participant is available during the given time, false otherwise.
     */
    public boolean isAvailableDuring(LocalDateTime start, LocalDateTime end) {
        return availableTimes.stream().anyMatch(slot ->
                !slot.getStartTime().isAfter(start) &&
                        !slot.getEndTime().isBefore(end)
        );
    }

    /**
     * Gets the name of the participant.
     *
     * @return The name of the participant.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the password of the participant.
     *
     * @return The password of the participant.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Checks if the provided password matches the participant's password.
     *
     * @param password The password to check.
     * @return True if the provided password matches, false otherwise.
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Gets the access level of the participant.
     *
     * @return The access level of the participant (ADMIN or MEMBER).
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Checks if the participant has an admin access level.
     *
     * @return True if the participant is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return accessLevel == AccessLevel.ADMIN;
    }

    /**
     * Sets the access level of the participant.
     *
     * @param accessLevel The new access level to set.
     */
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Gets the list of availability slots for the participant.
     *
     * @return A list of the participant's availability slots.
     */
    public List<AvailabilitySlot> getAvailableTimes() {
        return new ArrayList<>(availableTimes);
    }

    /**
     * Sets the availability slots for the participant.
     *
     * @param availableTimes A list of new availability slots to set.
     */
    public void setAvailableTimes(List<AvailabilitySlot> availableTimes) {
        this.availableTimes = new ArrayList<>(availableTimes);
    }

    /**
     * Returns a string representation of the participant, including their name and number of available slots.
     *
     * @return A string representing the participant.
     */
    @Override
    public String toString() {
        return "Participant: " + name + " (Available: " + availableTimes.size() + " slots)";
    }

    /**
     * Checks if two participants are equal based on their name (case-insensitive).
     *
     * @param obj The object to compare to.
     * @return True if the participants have the same name, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Participant other = (Participant) obj;
        return this.name.equalsIgnoreCase(other.name); // Use ignore case to match on name
    }

    /**
     * Returns the hash code for the participant, based on their name (case-insensitive).
     *
     * @return The hash code for the participant.
     */
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode(); // Match the equals rule
    }

    /**
     * Adds a new availability slot for the participant, merging or removing any overlapping slots.
     *
     * @param start The start time of the new availability slot.
     * @param end The end time of the new availability slot.
     */
    public void addAvailableTime(LocalDateTime start, LocalDateTime end) {
        AvailabilitySlot newSlot = new AvailabilitySlot(start, end);

        availableTimes.removeIf(slot ->
                !slot.getStartTime().isAfter(newSlot.getEndTime()) &&
                        !slot.getEndTime().isBefore(newSlot.getStartTime())
        );

        availableTimes.add(newSlot);
        availableTimes.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
    }
}
