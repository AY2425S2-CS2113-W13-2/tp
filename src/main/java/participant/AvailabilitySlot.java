package participant;

import java.time.LocalDateTime;
import java.util.Objects;
import logger.EventSyncLogger;

/**
 * Represents a time slot during which a participant is available.
 * <p>
 * This immutable class stores a time interval defined by start and end times,
 * and provides methods to access these values. The class ensures that the
 * end time is always after the start time.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 */
public final class AvailabilitySlot {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Constructs an AvailabilitySlot with the specified start and end times.
     *
     * @param startTime the starting time of the availability slot (must not be null)
     * @param endTime the ending time of the availability slot (must not be null and must be after startTime)
     * @throws IllegalArgumentException if endTime is not after startTime
     * @throws NullPointerException if either startTime or endTime is null
     */
    public AvailabilitySlot(LocalDateTime startTime, LocalDateTime endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");

        if (!endTime.isAfter(startTime)) {
            EventSyncLogger.getLogger().severe("Invalid time range: " + startTime + " to " + endTime);
            throw new IllegalArgumentException("End time must be after start time");
        }

        this.startTime = startTime;
        this.endTime = endTime;

        EventSyncLogger.getLogger().fine("Created new AvailabilitySlot: " + this);
    }

    /**
     * Returns the start time of this availability slot.
     *
     * @return the start time (never null)
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of this availability slot.
     *
     * @return the end time (never null)
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Checks if this availability slot overlaps with another slot.
     *
     * @param other the other availability slot to check against (must not be null)
     * @return true if the slots overlap, false otherwise
     * @throws NullPointerException if the other slot is null
     */
    public boolean overlapsWith(AvailabilitySlot other) {
        Objects.requireNonNull(other, "Other availability slot cannot be null");
        return !this.endTime.isBefore(other.startTime) && !this.startTime.isAfter(other.endTime);
    }

    /**
     * Returns the duration of this availability slot in minutes.
     *
     * @return duration in minutes
     */
    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Returns a string representation of this availability slot.
     *
     * @return a string in the format "startTime - endTime"
     */
    @Override
    public String toString() {
        return startTime.toString() + " - " + endTime.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AvailabilitySlot that = (AvailabilitySlot) obj;
        return startTime.equals(that.startTime) && endTime.equals(that.endTime);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }
}
