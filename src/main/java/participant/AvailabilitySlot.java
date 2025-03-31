package participant;

import java.time.LocalDateTime;

public class AvailabilitySlot  {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public AvailabilitySlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return startTime.toString() + " - " + endTime.toString();
    }
}
