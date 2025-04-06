import event.Event;
import participant.Participant;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListParticipantsTest {

    private Event event;
    private Participant participant1;
    private Participant participant2;

    @BeforeEach
    void setUp() {
        // Set up the event and participants for testing
        event = new Event("Team Meeting",
                LocalDateTime.of(2025, 5, 1, 9, 0),
                LocalDateTime.of(2025, 5, 1, 10, 0),
                "Conference Room",
                "Discussion on project status");

        participant1 = new Participant("Alice", "1234", Participant.AccessLevel.ADMIN);
        participant2 = new Participant("Bob", "5678", Participant.AccessLevel.MEMBER);
    }

    @Test
    void testListParticipantsWhenNoParticipants() {
        // Test when no participants have been added to the event
        event.listParticipants(); // Expect the message "No participants assigned to this event."
    }

    @Test
    void testListParticipantsWithOneParticipant() throws SyncException {
        // Add one participant and list participants
        event.addParticipant(participant1);

        // Capture the output (assuming listParticipants prints output)
        // Here, you might want to use a custom OutputStream or similar approach to capture printed statements.
        event.listParticipants(); // Expect the message "Participants for event: Team Meeting"
    }

    @Test
    void testListParticipantsWithMultipleParticipants() throws SyncException {
        // Add two participants and list participants
        event.addParticipant(participant1);
        event.addParticipant(participant2);

        // Verify the list output is correct, assuming it prints the list of participants
        event.listParticipants(); // Expect both Alice and Bob to be printed as participants
    }

    @Test
    void testAddParticipant() throws SyncException {
        // Add a participant and ensure they are added successfully
        event.addParticipant(participant1);
        assertTrue(event.hasParticipant("Alice"), "Participant should be added to the event.");
    }

    @Test
    void testAddDuplicateParticipant() {
        // Try adding the same participant twice and expect an exception
        assertThrows(SyncException.class, () -> {
            event.addParticipant(participant1);
            event.addParticipant(participant1); // This should throw SyncException
        });
    }

    @Test
    void testRemoveParticipant() throws SyncException {
        // Add a participant and then remove them
        event.addParticipant(participant1);
        boolean removed = event.removeParticipant("Alice");
        assertTrue(removed, "Participant should be removed successfully.");
        assertFalse(event.hasParticipant("Alice"), "Participant should no longer be in the event.");
    }
}
