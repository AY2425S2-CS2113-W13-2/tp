package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import event.Event;
import participant.Participant;


public class UITest {

    private UI ui;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        ui = new UI();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testShowMessage() {
        String message = "Test message";
        ui.showMessage(message);

        assertTrue(outputStreamCaptor.toString().contains(message));
    }

    @Test
    public void testShowMenu() {
        ui.showMenu();
        // Check if the menu contains specific expected strings
        assertTrue(outputStreamCaptor.toString().contains("╔═════════════════════════════════════════╗"));
        assertTrue(outputStreamCaptor.toString().contains("║          EVENT SYNC COMMAND MENU        ║"));
        assertTrue(outputStreamCaptor.toString().contains("║  === Event Management Commands ===      ║"));
    }

    @Test
    public void testReadLine() {
        String simulatedInput = "TestInput\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
        String result = ui.readLine();

        assertEquals(simulatedInput, result + "\n");
    }

    @Test
    public void testReadInt() {
        String simulatedInput = "123\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
        Integer result = ui.readInt();

        assertEquals(123, result);
    }

    @Test
    public void testReadIntInvalid() {
        String simulatedInput = "abc\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);
        Integer result = ui.readInt();

        assertNull(result, "Input should be invalid and return null");
    }

    @Test
    public void testReadFilterInputValid() {
        String simulatedInput = "4 5\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readFilterInput();
        assertEquals("4 5", result);
    }

    @Test
    public void testReadFilterInputInvalid() {
        String simulatedInput = "invalid\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readFilterInput();
        assertEquals("invalid", result);
    }

    @Test
    public void testReadListCommandInputValidAll() {
        String simulatedInput = "all\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readListCommandInput();
        assertEquals("all", result);
    }

    @Test
    public void testReadListCommandInputValidParticipants() {
        String simulatedInput = "participants EventName\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readListCommandInput();
        assertEquals("participants EventName", result);
    }

    @Test
    public void testReadListCommandInputInvalid() {
        String simulatedInput = "invalid\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readListCommandInput();
        assertEquals("invalid", result);
    }

    @Test
    public void testShowAddFormat() {
        ui.showAddFormat();
        assertTrue(outputStreamCaptor.toString().contains("Enter event " +
                "details (format: Event Name | Start Date | End Date | Location | Description):"));
    }

    @Test
    public void testShowAddedMessage() {
        Event event = new Event("Sample Event",
                LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 25, 11, 0),
                "Room 101", "Sample Description");
        ui.showAddedMessage(event);

        assertTrue(outputStreamCaptor.toString().contains("has been added to the list."));
    }

    @Test
    public void testConfirmDeletion() {
        String simulatedInput = "yes\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        boolean result = ui.confirmDeletion("Test Event");

        assertTrue(result, "The confirmation input should return true.");
    }

    @Test
    public void testConfirmDeletionNegative() {
        String simulatedInput = "no\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        boolean result = ui.confirmDeletion("Test Event");

        assertFalse(result, "The confirmation input should return false.");
    }

    @Test
    public void testAskConfirmation() {
        String simulatedInput = "y\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        boolean result = ui.askConfirmation("Are you sure?");
        assertTrue(result);
    }

    @Test
    public void testShowSuccessCreateMessage() {
        Participant participant = new Participant("John Doe", "password123",
                Participant.AccessLevel.ADMIN);
        ui.showSuccessCreateMessage(participant);

        assertTrue(outputStreamCaptor.toString().contains("Successfully created: John Doe"));
    }

    @Test
    public void testShowSuccessLoginMessage() {
        ui.showSuccessLoginMessage();
        assertTrue(outputStreamCaptor.toString().contains("Successfully logged in."));
    }

    @Test
    public void testShowLogOutMessage() {
        ui.showLogOutMessage();
        assertTrue(outputStreamCaptor.toString().contains("Bye! Press 'login' to log in or 'create' to create a new user."));
    }

    @Test
    public void testReadAddCommandInputValid() {
        String simulatedInput = "Event Name | 2025-03-25 10:00 | 2025-03-25 11:00 | Room 101 | Description\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readAddCommandInput();
        assertEquals(simulatedInput.trim(), result);
    }

    @Test
    public void testReadAddCommandInputInvalid() {
        String simulatedInput = "Invalid Input\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        ui.setScanner(testScanner);

        String result = ui.readAddCommandInput();
        assertEquals(simulatedInput.trim(), result);
    }

    @Test
    public void testShowCollisionWarning() {
        Event newEvent = new Event("New Event", LocalDateTime.of(2025, 3, 25, 10, 0), LocalDateTime.of(2025, 3, 25, 11, 0), "Room 101", "Description");
        ArrayList<Event> collisions = new ArrayList<>();
        collisions.add(new Event("Existing Event", LocalDateTime.of(2025, 3, 25, 10, 0), LocalDateTime.of(2025, 3, 25, 11, 0), "Room 102", "Description"));
        ui.showCollisionWarning(newEvent, collisions);

        assertTrue(outputStreamCaptor.toString().contains("Warning: Scheduling Conflict"));
    }

    @Test
    public void testShowParticipantSlotCollisionWarning() {
        Event event = new Event("Event with Conflict", LocalDateTime.of(2025, 3, 25, 10, 0), LocalDateTime.of(2025, 3, 25, 11, 0), "Room 101", "Description");
        ArrayList<Event> collisions = new ArrayList<>();
        collisions.add(new Event("Conflicting Event", LocalDateTime.of(2025, 3, 25, 10, 0), LocalDateTime.of(2025, 3, 25, 11, 0), "Room 102", "Description"));
        ui.showParticipantSlotCollisionWarning(event, collisions);

        assertTrue(outputStreamCaptor.toString().contains("Warning: Scheduling Conflict"));
    }

    @Test
    public void testShowByeMessage() {
        ui.showByeMessage();
        assertTrue(outputStreamCaptor.toString().contains("Bye!"));
    }
}

