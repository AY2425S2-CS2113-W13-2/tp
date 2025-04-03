import event.Event;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ParticipantManagerTest {

    private ParticipantManager participantManager;
    private ArrayList<Participant> participants;
    private UI ui;
    private UserStorage storage;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        storage = new UserStorage("test-users.txt");
        participants = new ArrayList<>();
        participantManager = new ParticipantManager(participants, ui, storage);
    }

//    @Test
//    void testAddAndDeleteUser() throws SyncException {
//        Participant p = new Participant("Alice", "pass", Participant.AccessLevel.MEMBER);
//        participantManager.addNewUser(p);
//        assertEquals(1, participantManager.getParticipants().size());
//
//        participantManager.deleteUser(p);
//        assertEquals(0, participantManager.getParticipants().size());
//    }
//
//    @Test
//    void testGetParticipant_success() throws SyncException {
//        Participant p = new Participant("Bob", "1234", Participant.AccessLevel.ADMIN);
//        participantManager.addNewUser(p);
//        Participant result = participantManager.getParticipant("Bob");
//        assertEquals("Bob", result.getName());
//    }
//
//    @Test
//    void testGetParticipant_notFound() throws SyncException {
//        Participant result = participantManager.getParticipant("Ghost");
//        assertNull(result);
//    }
//
//    @Test
//    void testAssignParticipant_success() throws SyncException {
//        LocalDateTime start = LocalDateTime.of(2025, 4, 2, 10, 0);
//        LocalDateTime end = LocalDateTime.of(2025, 4, 2, 11, 0);
//        Participant p = new Participant("Eve", "pwd", Participant.AccessLevel.MEMBER);
//        p.addAvailableTime(start.minusHours(1), end.plusHours(1));
//        participantManager.addNewUser(p);
//
//        Event event = new Event("Meeting", start, end, "Room 1", "Team Sync");
//
//        boolean assigned = participantManager.assignParticipant(event, p);
//        assertTrue(assigned);
//    }
//
//    @Test
//    void testCheckParticipantAvailability_true() {
//        LocalDateTime start = LocalDateTime.of(2025, 4, 2, 10, 0);
//        LocalDateTime end = LocalDateTime.of(2025, 4, 2, 11, 0);
//        Participant p = new Participant("Carol", "pass", Participant.AccessLevel.MEMBER);
//        p.addAvailableTime(start.minusHours(1), end.plusHours(1));
//
//        Event event = new Event("Event", start, end, "Room A", "Desc");
//
//        assertTrue(participantManager.checkParticipantAvailability(event, p));
//    }
//
//    @Test
//    void testCheckParticipantAvailability_false() {
//        LocalDateTime start = LocalDateTime.of(2025, 4, 2, 10, 0);
//        LocalDateTime end = LocalDateTime.of(2025, 4, 2, 11, 0);
//        Participant p = new Participant("Dave", "pass", Participant.AccessLevel.MEMBER);
//        p.addAvailableTime(start.minusHours(2), start.minusHours(1));
//
//        Event event = new Event("Clash", start, end, "Room B", "Blocked");
//
//        assertFalse(participantManager.checkParticipantAvailability(event, p));
//    }
//
//    @Test
//    void testLogout() throws SyncException {
//        Participant p = new Participant("Frank", "pass", Participant.AccessLevel.ADMIN);
//        participantManager.addNewUser(p);
//        participantManager.logout(); // nothing to logout
//        assertNull(participantManager.getCurrentUser());
//    }
}
