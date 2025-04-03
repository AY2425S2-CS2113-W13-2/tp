import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import participant.AvailabilitySlot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    private Participant participant;
    private AvailabilitySlot slot1;
    private AvailabilitySlot slot2;

    @BeforeEach
    void setUp() {
        participant = new Participant("Alice", "1234", Participant.AccessLevel.ADMIN);

        slot1 = new AvailabilitySlot(
                LocalDateTime.of(2025, 5, 1, 9, 0),
                LocalDateTime.of(2025, 5, 1, 10, 0)
        );

        slot2 = new AvailabilitySlot(
                LocalDateTime.of(2025, 5, 2, 14, 0),
                LocalDateTime.of(2025, 5, 2, 15, 0)
        );
    }

    @Test
    void testParticipantCreation() {
        assertEquals("Alice", participant.getName());
        assertEquals(Participant.AccessLevel.ADMIN, participant.getAccessLevel());
        assertTrue(participant.getAvailableTimes().isEmpty());
    }

    @Test
    void testSetAccessLevel() {
        participant.setAccessLevel(Participant.AccessLevel.MEMBER);
        assertEquals(Participant.AccessLevel.MEMBER, participant.getAccessLevel());
    }

    @Test
    void testSetAndGetAvailabilitySlots() {
        List<AvailabilitySlot> availabilityList = new ArrayList<>();
        availabilityList.add(slot1);
        availabilityList.add(slot2);

        participant.setAvailableTimes(availabilityList);
        List<AvailabilitySlot> retrievedSlots = participant.getAvailableTimes();

        assertEquals(2, retrievedSlots.size());
        assertEquals(slot1.getStartTime(), retrievedSlots.get(0).getStartTime());
        assertEquals(slot2.getEndTime(), retrievedSlots.get(1).getEndTime());
    }

    @Test
    void testToStringOutput() {
        List<AvailabilitySlot> availabilityList = new ArrayList<>();
        availabilityList.add(slot1);
        participant.setAvailableTimes(availabilityList);

        String output = participant.toString();
        assertTrue(output.contains("Participant: Alice"));
        assertTrue(output.contains("Available: 1 slots"));
    }

    @Test
    void testAssignEventTimeValidSlot() {
        participant.addAvailableTime(slot1.getStartTime(), slot1.getEndTime());

        boolean assigned = participant.assignEventTime(
                LocalDateTime.of(2025, 5, 1, 9, 0),
                LocalDateTime.of(2025, 5, 1, 10, 0)
        );

        assertTrue(assigned, "Participant should be assigned if available.");
        assertEquals(0, participant.getAvailableTimes().size());
    }

    @Test
    void testAssignEventTimeNoOverlap() {
        participant.addAvailableTime(slot1.getStartTime(), slot1.getEndTime());

        boolean assigned = participant.assignEventTime(
                LocalDateTime.of(2025, 5, 1, 11, 0),
                LocalDateTime.of(2025, 5, 1, 12, 0)
        );

        assertFalse(assigned, "Participant should not be assigned if unavailable.");
        assertEquals(1, participant.getAvailableTimes().size());
    }

    @Test
    void testEqualsAndHashCode() {
        Participant p1 = new Participant("Alice", "pass", Participant.AccessLevel.ADMIN);
        Participant p2 = new Participant("alice", "different", Participant.AccessLevel.MEMBER); // same name, different case

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
