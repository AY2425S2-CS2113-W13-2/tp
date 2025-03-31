
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
        participant = new Participant("Alice", "1234",  Participant.AccessLevel.ADMIN);

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
        assertTrue(output.contains("Name:Alice"));
        assertTrue(output.contains("Access: ADMIN"));
        assertTrue(output.contains(slot1.toString()));
    }
    @Test
    void testInvalidAvailabilitySlot_TimeOrder() {
        LocalDateTime start = LocalDateTime.of(2025, 5, 2, 15, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 2, 14, 0); // earlier than start

        AvailabilitySlot invalidSlot = new AvailabilitySlot(start, end);

        List<AvailabilitySlot> list = new ArrayList<>();
        list.add(invalidSlot);

        participant.setAvailableTimes(list);

        assertTrue(participant.getAvailableTimes().get(0).getStartTime()
                        .isAfter(participant.getAvailableTimes().get(0).getEndTime()),
                "Invalid time slot should have start time after end time");
    }

}
