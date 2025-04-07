package participant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Participant {
    private final String name;
    private final String password;
    private AccessLevel accessLevel;
    private List<AvailabilitySlot> availableTimes;

    public enum AccessLevel { ADMIN, MEMBER }

    public Participant(String name, String password, AccessLevel accessLevel) {
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>();
    }

    public Participant(String name, String password, AccessLevel accessLevel, List<AvailabilitySlot> availableTimes) {
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
        this.availableTimes = new ArrayList<>(availableTimes); // Defensive copy
    }

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

    public boolean isAvailableDuring(LocalDateTime start, LocalDateTime end) {
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

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public boolean isAdmin() {
        return accessLevel == AccessLevel.ADMIN;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<AvailabilitySlot> getAvailableTimes() {
        return new ArrayList<>(availableTimes);
    }

    public void setAvailableTimes(List<AvailabilitySlot> availableTimes) {
        this.availableTimes = new ArrayList<>(availableTimes);
    }

    @Override
    public String toString() {
        return "Participant: " + name + " (Available: " + availableTimes.size() + " slots)";
    }
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

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode(); // Match the equals rule
    }

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
