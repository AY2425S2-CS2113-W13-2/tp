package storage;

import event.Event;
import exception.SyncException;
import label.Priority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class StorageTest {

    private static final String TEST_FILE_PATH = "./data/StorageTest.txt";
    private Storage storage;

    @BeforeEach
    public void setUp() throws SyncException, IOException {
        Files.createDirectories(Paths.get("./data"));

        UserStorage userStorage = new UserStorage("./data/StorageTestUsers.txt");
        storage = new Storage(TEST_FILE_PATH, userStorage);

        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    @AfterEach
    public void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveEvents() throws SyncException {
        Event event1 = new Event("Event 1", LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 25, 11, 0), "Room 101", "Description 1");
        Event event2 = new Event("Event 2", LocalDateTime.of(2025, 3, 26, 14, 0),
                LocalDateTime.of(2025, 3, 26, 15, 0), "Room 102", "Description 2");

        storage.saveEvents(List.of(event1, event2), Priority.getAllPriorities());

        Path filePath = Paths.get(TEST_FILE_PATH);
        assertTrue(Files.exists(filePath), "File should be created");

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            assertNotNull(line1, "File should contain events");
            assertNotNull(line2, "File should contain events");

            assertTrue(line1.contains("Event 1"));
            assertTrue(line2.contains("Event 2"));
        } catch (IOException e) {
            fail("Error reading file: " + e.getMessage());
        }
    }

    @Test
    public void testLoadEvents() throws SyncException {
        Event event1 = new Event("Event 1", LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 25, 11, 0), "Room 101", "Description 1");
        Event event2 = new Event("Event 2", LocalDateTime.of(2025, 3, 26, 14, 0),
                LocalDateTime.of(2025, 3, 26, 15, 0), "Room 102", "Description 2");
        storage.saveEvents(List.of(event1, event2), Priority.getAllPriorities());

        List<Event> events = storage.loadEvents();

        assertEquals(2, events.size(), "There should be 2 events loaded");
        assertEquals("Event 1", events.get(0).getName(), "Event 1 should be loaded correctly");
        assertEquals("Event 2", events.get(1).getName(), "Event 2 should be loaded correctly");
    }

    @Test
    public void testLoadEventsWithCorruptedFile() throws SyncException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(TEST_FILE_PATH))) {
            writer.write("Invalid Event Data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(SyncException.class, () ->
            storage.loadEvents());
    }

    @Test
    public void testSaveEventsWithCorrectPriorities() throws SyncException {
        Event event1 = new Event("Event 1", LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 25, 11, 0), "Room 101", "Description 1");
        Event event2 = new Event("Event 2", LocalDateTime.of(2025, 3, 26, 14, 0),
                LocalDateTime.of(2025, 3, 26, 15, 0), "Room 102", "Description 2");

        storage.saveEvents(List.of(event1, event2), Priority.getAllPriorities());

        Path filePath = Paths.get(TEST_FILE_PATH);
        assertTrue(Files.exists(filePath), "File should be created");

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line1 = reader.readLine();
            String line2 = reader.readLine();

            assertTrue(line2.contains("LOW"), "Event 2 should have LOW priority");
        } catch (IOException e) {
            fail("Error reading file: " + e.getMessage());
        }
    }

    @Test
    public void testLoadEventsWithValidData() throws SyncException {
        Event event1 = new Event("Event 1", LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 25, 11, 0), "Room 101", "Description 1");
        Event event2 = new Event("Event 2", LocalDateTime.of(2025, 3, 26, 14, 0),
                LocalDateTime.of(2025, 3, 26, 15, 0), "Room 102", "Description 2");
        storage.saveEvents(List.of(event1, event2), Priority.getAllPriorities());

        List<Event> events = storage.loadEvents();

        assertEquals(2, events.size(), "There should be 2 events loaded");
        assertEquals("Event 1", events.get(0).getName(), "Event 1 should be loaded correctly");
        assertEquals("Event 2", events.get(1).getName(), "Event 2 should be loaded correctly");
    }

    @Test
    public void testSyncExceptionOnLoadEvents() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(TEST_FILE_PATH))) {
            writer.write("Invalid Event Data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            storage.loadEvents();
            fail("Expected SyncException to be thrown when loading invalid events");
        } catch (SyncException e) {
            assertTrue(e.getMessage().contains("Skipping corrupted line"), "Expected SyncException for corrupted data");
        }
    }
}
