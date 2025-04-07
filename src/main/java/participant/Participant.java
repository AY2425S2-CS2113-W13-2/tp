package participant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a participant with a name, password, access level, and available time slots.
 */
public class Participant {
    private static final Logger logger = Logger.getLogger(Participant.class.getName());

    private final String name;
    private final String password;
    private AccessLevel accessLevel;
    private List<AvailabilitySlot> availableTimes;

    /**
     * Enum representing participant access levels.
     */
    public enum AccessLevel { ADMIN, MEMBER }

    /**
     * Constructs a participant with name, password, and access level.
     *
     * @param name         the participant's name
     * @param password     the participant's password
     * @param accessLevel  the participant's access level
     */
    public Participant(String name, String password, AccessLevel accessLevel) {
        assert name != null && password != null && accessLevel != null;
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>();
    }

    /**
     * Constructs a participant with availability slots.
     *
     * @param name           the participant's name
     * @param password       the participant's password
     * @param accessLevel    the participant's access level
     * @param availableTimes the participant's available time slots
     */
    public Participant(String name, String password, AccessLevel accessLevel, List<AvailabilitySlot> availableTimes) {
        assert name != null && password != null && accessLevel != null && availableTimes != null;
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>(availableTimes); // Defensive copy
    }

    /**
     * Attempts to assign an event to the participant and updates availability accordingly.
     *
     * @param eventStart the start time of the event
     * @param eventEnd   the end time of the event
     * @return true if event was successfully assigned, false otherwise
     */
    public boolean assignEventTime(LocalDateTime eventStart, LocalDateTime eventEnd) {
        logger.info("Attempting to assign event from " + eventStart + " to " + eventEnd);
        assert eventStart != null && eventEnd != null;

        if (eventStart.isAfter(eventEnd)) {
            logger.warning("Event end time is before start time.");
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
            logger.info("Event assigned successfully. Updated availability.");
            return true;
        }

        logger.info("Event not assigned. No overlapping slot.");
        return false;
    }

    /**
     * Checks if the participant is available during the given period.
     *
     * @param start the start of the time range
     * @param end   the end of the time range
     * @return true if available, false otherwise
     */
    public boolean isAvailableDuring(LocalDateTime start, LocalDateTime end) {
        assert start != null && end != null;
        return availableTimes.stream().anyMatch(slot ->
                !slot.getStartTime().isAfter(start) &&
                        !slot.getEndTime().isBefore(end)
        );
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Checks whether the input password matches the participant's password.
     *
     * @param password the password to check
     * @return true if matched, false otherwise
     */
    public boolean checkPassword(String password) {
        assert password != null;
        return this.password.equals(password);
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public boolean isAdmin() {
        return accessLevel == AccessLevel.ADMIN;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        assert accessLevel != null;
        this.accessLevel = accessLevel;
    }

    /**
     * Returns a copy of the participant's available time slots.
     *
     * @return the list of available time slots
     */
    public List<AvailabilitySlot> getAvailableTimes() {
        return new ArrayList<>(availableTimes);
    }

    public void setAvailableTimes(List<AvailabilitySlot> availableTimes) {
        assert availableTimes != null;
        this.availableTimes = new ArrayList<>(availableTimes);
    }

    @Override
    public String toString() {
        return "Participant: " + name + " (Available: " + availableTimes.size() + " slots)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Participant other = (Participant) obj;
        return this.name.equalsIgnoreCase(other.name); // Use ignore case to match on name
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode(); // Match the equals rule
    }

    /**
     * Adds a new available time slot and merges overlapping ones.
     *
     * @param start the start of the availability slot
     * @param end   the end of the availability slot
     */
    public void addAvailableTime(LocalDateTime start, LocalDateTime end) {
        logger.info("Adding available time from " + start + " to " + end);
        assert start != null && end != null;
        AvailabilitySlot newSlot = new AvailabilitySlot(start, end);

        availableTimes.removeIf(slot ->
                !slot.getStartTime().isAfter(newSlot.getEndTime()) &&
                        !slot.getEndTime().isBefore(newSlot.getStartTime())
        );

        availableTimes.add(newSlot);
        availableTimes.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
    }

    /**
     * Re-inserts a previously assigned time slot back into availability.
     *
     * @param eventStart the start time of the slot
     * @param eventEnd   the end time of the slot
     */
    public void unassignEventTime(LocalDateTime eventStart, LocalDateTime eventEnd) {
        logger.info("Unassigning event from " + eventStart + " to " + eventEnd);
        assert eventStart != null && eventEnd != null;
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
        logger.info("Updated availability after unassignment.");
    }
}
