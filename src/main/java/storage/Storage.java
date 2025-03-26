package storage;

import event.Event;
import exception.SyncException;
import label.Priority;
import participant.Participant;
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
    public Storage(String filePath) throws SyncException {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Ensures that the storage file exists, creating it if necessary.
     */
    private void ensureFileExists() throws SyncException {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new SyncException("Cannot create storage file: " + filePath);
        }
    }

    /**
     * Saves the list of events to the storage file.
     *
     * @param events        The list of events to be saved.
     * @param allPriorities
     */
    public void saveEvents(List<Event> events, ArrayList<String> allPriorities) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<String> priorities = Priority.getAllPriorities();
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                String priority = (i < priorities.size()) ? priorities.get(i) : Priority.LOW;
                writer.write(formatEvent(event, priority));
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
        ArrayList<String> loadedPriorities = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = parseEventWithPriority(line);
                    Event event = parseEvent(parts);
                    String priority = parts[5];
                    loadedPriorities.add(priority);
                    events.add(event);
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping corrupted line: " + line);
                }
            }
            Priority.loadFromStorage(loadedPriorities);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return events;
    }

    /**
     * Converts an Event object into a formatted string for file storage.
     */
    private String formatEvent(Event event, String priority) {
        StringBuilder participantsBuilder = new StringBuilder();
        for (Participant p : event.getParticipants()) {
            participantsBuilder.append(p.getName()).append(":").append(p.getAccessLevel()).append(",");
        }
        // Remove trailing comma if present
        String participantsString = participantsBuilder.length() > 0
                ? participantsBuilder.substring(0, participantsBuilder.length() - 1)
                : "";

        return String.format("%s | %s | %s | %s | %s | %s | %s",
                event.getName(),
                event.getStartTime().format(formatter),
                event.getEndTime().format(formatter),
                event.getLocation(),
                event.getDescription(),
                priority,
                participantsString);  // ✅ Save participants
    }


    private String[] parseEventWithPriority(String line) {
        String[] parts = line.split(" \\| ", -1);  // -1 to keep trailing empty participant field
        if (parts.length == 5) {
            // Old format without priority and participants
            String[] extended = new String[7];
            System.arraycopy(parts, 0, extended, 0, 5);
            extended[5] = "LOW";
            extended[6] = "";
            return extended;
        } else if (parts.length == 6) {
            // Old format with priority but no participants
            String[] extended = new String[7];
            System.arraycopy(parts, 0, extended, 0, 6);
            extended[6] = "";
            return extended;
        } else if (parts.length == 7) {
            return parts;
        } else {
            throw new IllegalArgumentException("Invalid event format: " + line);
        }
    }

    /**
     * Creates an Event object from parsed parts (excluding priority).
     */
    private Event parseEvent(String[] parts) {
        Event event = new Event(
                parts[0],
                LocalDateTime.parse(parts[1], formatter),
                LocalDateTime.parse(parts[2], formatter),
                parts[3],
                parts[4]
        );

        // Load participants if present
        if (parts.length == 7 && !parts[6].isEmpty()) {
            String[] participantEntries = parts[6].split(",");
            for (String entry : participantEntries) {
                String[] participantData = entry.split(":");
                if (participantData.length == 2) {
                    String name = participantData[0];
                    Participant.AccessLevel accessLevel = Participant.AccessLevel.valueOf(participantData[1]);
                    event.addParticipant(new Participant(name, accessLevel));
                }
            }
        }
        return event;
    }

}
