package participant;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import command.LoginCommand;

/**
 * Represents an availability slot with a start time and an end time.
 * This class is used to define a period during which a participant is available.
 */
public class AvailabilitySlot {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());


    /**
     * The start time of the availability slot.
     */
    private final LocalDateTime startTime;

    /**
     * The end time of the availability slot.
     */
    private final LocalDateTime endTime;

    /**
     * Constructs an AvailabilitySlot object with the given start and end times.
     *
     * @param startTime The start time of the availability slot.
     * @param endTime The end time of the availability slot.
     */
    public AvailabilitySlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the start time of the availability slot.
     *
     * @return The start time of the availability slot.
     */
    public LocalDateTime getStartTime() {
        assert this != null;
        LOGGER.info("Attempting getting start time");
        return startTime;
    }

    /**
     * Returns the end time of the availability slot.
     *
     * @return The end time of the availability slot.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns a string representation of the availability slot in the format
     * "startTime - endTime".
     *
     * @return A string representation of the availability slot.
     */
    @Override
    public String toString() {
        return startTime.toString() + " - " + endTime.toString();
    }
}
