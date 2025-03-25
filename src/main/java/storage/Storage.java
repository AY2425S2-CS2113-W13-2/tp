package storage;

import event.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles storage-related operations such as saving and loading events to a file.
 */
public class Storage {
    private final String filePath;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructs an EventStorage instance and ensures the file exists.
     *
     * @param filePath The path to the file used for storage.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Ensures that the storage file exists, creating it if necessary.
     */
    private void ensureFileExists() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    /**
     * Saves the list of events to the storage file.
     *
     * @param events The list of events to be saved.
     */
    public void saveEvents(List<Event> events) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Event event : events) {
                writer.write(formatEvent(event));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving events: " + e.getMessage());
        }
    }

    /**
     * Loads events from the storage file.
     *
     * @return A list of events loaded from the file.
     */
    public ArrayList<Event> loadEvents() {
        ArrayList<Event> events = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    events.add(parseEvent(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping corrupted line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return events;
    }

    /**
     * Converts an Event object into a formatted string for file storage.
     */
    private String formatEvent(Event event) {
        return String.format("%s | %s | %s | %s | %s",
                event.getName(),
                event.getStartTime().format(formatter),
                event.getEndTime().format(formatter),
                event.getLocation(),
                event.getDescription());
    }

    /**
     * Parses an event from a line of text.
     *
     * @param line The line containing event data.
     * @return The parsed Event object.
     * @throws IllegalArgumentException If the format is invalid.
     */
    private Event parseEvent(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid event format");
        }
        return new Event(
                parts[0],
                LocalDateTime.parse(parts[1], formatter),
                LocalDateTime.parse(parts[2], formatter),
                parts[3],
                parts[4]
        );
    }
}
