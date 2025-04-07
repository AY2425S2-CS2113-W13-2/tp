package ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PrintMatchingEventsTest {

    private UI ui;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        ui = new UI();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testPrintMatchingEvents_EmptyList() {
        ArrayList<Event> events = new ArrayList<>();
        ui.printMatchingEvents(events);
        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.contains("No matching events found."));
    }

    @Test
    public void testPrintMatchingEvents_NonEmptyList() {
        ArrayList<Event> events = new ArrayList<>();

        // Parse String to LocalDateTime using DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime1 = LocalDateTime.parse("2023-10-26 10:00", formatter);
        LocalDateTime endTime1 = LocalDateTime.parse("2023-10-26 12:00", formatter);
        LocalDateTime startTime2 = LocalDateTime.parse("2023-10-27 14:00", formatter);
        LocalDateTime endTime2 = LocalDateTime.parse("2023-10-27 16:00", formatter);

        events.add(new Event("Event 1", startTime1, endTime1, "Location 1", "Description 1"));
        events.add(new Event("Event 2", startTime2, endTime2, "Location 2", "Description 2"));

        ui.printMatchingEvents(events);
        String output = outputStreamCaptor.toString().trim();

        assertTrue(output.contains("Found 2 matching events."));
        assertTrue(output.contains("1. +----------------------+--------------------------------+"));
        assertTrue(output.contains("2. +----------------------+--------------------------------+"));
    }
}