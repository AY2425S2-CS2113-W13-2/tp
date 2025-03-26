package participant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;


public class Participant {
    private final String name;
    private AccessLevel accessLevel;
    private List<AvailabilitySlot> availableTimes;


    public enum AccessLevel { ADMIN, MEMBER }

    public Participant(String name, AccessLevel accessLevel) {
        this.name = name;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>();
    }

    public String getName() { return name; }
    public AccessLevel getAccessLevel() { return accessLevel; }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<AvailabilitySlot> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<AvailabilitySlot> availableTimes) {
        this.availableTimes = availableTimes;
    }

    @Override
    public String toString() {
        return "Name:" + name + ", Access: "+ accessLevel + ", Availability: " + availableTimes;
    }
}
