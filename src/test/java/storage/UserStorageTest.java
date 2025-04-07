package storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.SyncException;
import participant.Participant;
import participant.Participant.AccessLevel;
import participant.AvailabilitySlot;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserStorageTest {

    private static final String TEST_FILE_PATH = "./data/testUsers.txt";
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() throws SyncException {
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        } catch (IOException e) {
        }
        userStorage = new UserStorage(TEST_FILE_PATH);
    }

    @Test
    public void testEnsureFileExists() throws SyncException {
        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists(), "The user storage file should be created.");
    }

    @Test
    public void testSaveUsers() throws SyncException {
        Participant participant = new Participant("John Doe", "password123", AccessLevel.MEMBER);
        List<Participant> participants = new ArrayList<>();
        participants.add(participant);

        userStorage.saveUsers(participants);

        List<Participant> loadedParticipants = userStorage.loadUsers();
        assertEquals(1, loadedParticipants.size(), "There should be one participant loaded.");
        assertEquals("John Doe", loadedParticipants.get(0).getName(), "The participant's name should match.");
    }

    @Test
    public void testLoadUsers() throws SyncException {
        Participant participant1 = new Participant("John Doe", "password123", AccessLevel.MEMBER);
        Participant participant2 = new Participant("Jane Smith", "password456", AccessLevel.ADMIN);
        List<Participant> participants = new ArrayList<>();
        participants.add(participant1);
        participants.add(participant2);
        userStorage.saveUsers(participants);

        List<Participant> loadedParticipants = userStorage.loadUsers();
        assertEquals(2, loadedParticipants.size(), "There should be two participants loaded.");
        assertEquals("John Doe", loadedParticipants.get(0).getName(), "The first participant's name should match.");
        assertEquals("Jane Smith", loadedParticipants.get(1).getName(), "The second participant's name should match.");
    }

    @Test
    public void testSaveAndLoadAvailabilitySlots() throws SyncException {
        Participant participant = new Participant("John Doe", "password123", AccessLevel.MEMBER);
        AvailabilitySlot slot = new AvailabilitySlot(LocalDateTime.of(2025, 4, 7, 9, 0),
                LocalDateTime.of(2025, 4, 7, 10, 0));
        participant.addAvailableTime(slot.getStartTime(), slot.getEndTime());
        List<Participant> participants = new ArrayList<>();
        participants.add(participant);

        userStorage.saveUsers(participants);

        List<Participant> loadedParticipants = userStorage.loadUsers();
        assertEquals(1, loadedParticipants.size(), "There should be one participant loaded.");
        assertEquals(1, loadedParticipants.get(0).getAvailableTimes().size(), "The participant should have one availability slot.");
        assertEquals(slot.getStartTime(), loadedParticipants.get(0).getAvailableTimes().get(0).getStartTime(),
                "The availability start time should match.");
    }

    @Test
    public void testFindUserByName() throws SyncException {
        Participant participant1 = new Participant("John Doe", "password123", AccessLevel.MEMBER);
        Participant participant2 = new Participant("Jane Smith", "password456", AccessLevel.ADMIN);
        List<Participant> participants = new ArrayList<>();
        participants.add(participant1);
        participants.add(participant2);
        userStorage.saveUsers(participants);

        Participant foundParticipant = userStorage.findUserByName(participants, "john doe");
        assertNotNull(foundParticipant, "The participant should be found by name.");
        assertEquals("John Doe", foundParticipant.getName(), "The found participant's name should match.");

        Participant notFoundParticipant = userStorage.findUserByName(participants, "nonexistent");
        assertNull(notFoundParticipant, "The participant should not be found.");
    }

    @Test
    public void testHandleCorruptedFile() throws IOException {
        String corruptedLine = "John Doe | USER | password123 | invalidSlot\n";
        Files.write(Paths.get(TEST_FILE_PATH), corruptedLine.getBytes());

        SyncException exception = assertThrows(SyncException.class, () -> {
            userStorage.loadUsers();
        });
        assertTrue(exception.getMessage().contains("Skipping corrupted line"), "The exception message should mention corrupted lines.");
    }
}
