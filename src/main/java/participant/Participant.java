package participant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a participant with a name, password, access level, and available time slots.
 * Provides methods to manage participant availability and access control.
 */
public class Participant {
    private final String name;
    private final String password;
    private AccessLevel accessLevel;
    private List<AvailabilitySlot> availableTimes;

    /**
     * Enum representing the access levels for a participant.
     */
    public enum AccessLevel { ADMIN, MEMBER }

    /**
     * Constructs a Participant with the specified name, password, and access level.
     *
     * @param name The name of the participant.
     * @param password The password for the participant.
     * @param accessLevel The access level of the participant (ADMIN or MEMBER).
     */
    public Participant(String name, String password, AccessLevel accessLevel) {
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>();
    }

    /**
     * Constructs a Participant with the specified name, password, access level, and availability slots.
     *
     * @param name The name of the participant.
     * @param password The password for the participant.
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
     * Assigns an event time to the participant by adjusting their availability slots.
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
     * Checks if the participant is available during a specific time range.
     *
     * @param start The start time of the period to check.
     * @param end The end time of the period to check.
     * @return True if the participant is available during the specified time range, false otherwise.
     */
    public boolean isAvailableDuring(LocalDateTime start, LocalDateTime end) {
        return availableTimes.stream().anyMatch(slot ->
                !slot.getStartTime().isAfter(start) &&
                        !slot.getEndTime().isBefore(end)
        );
    }

    /**
     * Returns the name of the participant.
     *
     * @return The name of the participant.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the password of the participant.
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
     * @return True if the provided password matches the participant's password, false otherwise.
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Returns the access level of the participant.
     *
     * @return The access level of the participant.
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Returns whether the participant has ADMIN access.
     *
     * @return True if the participant has ADMIN access, false otherwise.
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
     * Returns a copy of the list of availability slots for the participant.
     *
     * @return A copy of the list of availability slots.
     */
    public List<AvailabilitySlot> getAvailableTimes() {
        return new ArrayList<>(availableTimes);
    }

    /**
     * Sets the list of availability slots for the participant.
     *
     * @param availableTimes The list of availability slots to set.
     */
    public void setAvailableTimes(List<AvailabilitySlot> availableTimes) {
        this.availableTimes = new ArrayList<>(availableTimes);
    }

    /**
     * Returns a string representation of the participant, including their name and the number of available slots.
     *
     * @return A string representation of the participant.
     */
    @Override
    public String toString() {
        return "Participant: " + name + " (Available: " + availableTimes.size() + " slots)";
    }

    /**
     * Checks if this participant is equal to another object.
     * Two participants are considered equal if their names are the same (case-insensitive).
     *
     * @param obj The object to compare.
     * @return True if the participants are equal, false otherwise.
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
     * Returns a hash code for this participant based on their name (case-insensitive).
     *
     * @return The hash code of the participant.
     */
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode(); // Match the equals rule
    }

    /**
     * Adds an availability slot for the participant. If the new slot overlaps with existing ones, the overlapping slots are removed.
     * The slots are then sorted by their start time.
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

    /**
     * Unassigns an event time from the participant by merging the affected availability slots.
     * The slots are then sorted by their start time.
     *
     * @param eventStart The start time of the event to unassign.
     * @param eventEnd The end time of the event to unassign.
     */
    public void unassignEventTime(LocalDateTime eventStart, LocalDateTime eventEnd) {
        AvailabilitySlot newSlot = new AvailabilitySlot(eventStart, eventEnd);
        List<AvailabilitySlot> mergedSlots = new ArrayList<>();

        boolean inserted = false;

        for (AvailabilitySlot slot : availableTimes) {
            if (slot.getEndTime().isBefore(newSlot.getStartTime().minusMinutes(1))) {
                mergedSlots.add(slot);
            } else if (slot.getStartTime().isAfter(newSlot.getEndTime().plusMinutes(1))) {
                if (!inserted) {
                    mergedSlots.add(newSlot);
                    inserted = true;
                }
                mergedSlots.add(slot);
            } else {
                newSlot = new AvailabilitySlot(
                        slot.getStartTime().isBefore(newSlot.getStartTime()) ? slot.getStartTime() : newSlot.getStartTime(),
                        slot.getEndTime().isAfter(newSlot.getEndTime()) ? slot.getEndTime() : newSlot.getEndTime()
                );
            }
        }

        if (!inserted) {
            mergedSlots.add(newSlot);
        }

        mergedSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        availableTimes = mergedSlots;
    }
}
