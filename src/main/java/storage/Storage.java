package storage;

import event.Event;
import label.Priority;
import participant.Participant;
import participant.Participant.AccessLevel;
import exception.SyncException;
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
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final String filePath;
    private final UserStorage userStorage;

    /**
     * Constructs a new Storage object.
     *
     * @param filePath Path to the storage file.
     * @param userStorage UserStorage instance for loading users.
     * @throws SyncException If an error occurs while ensuring file existence.
     */
    public Storage(String filePath, UserStorage userStorage) throws SyncException {
        this.filePath = filePath;
        this.userStorage = userStorage;
        ensureFileExists();
    }

    /**
     * Ensures that the storage file exists. If it doesn't, creates it.
     *
     * @throws SyncException If an error occurs while ensuring file existence.
     */
    private void ensureFileExists() throws SyncException {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            logger.info("Storage file exists at: " + filePath);
        } catch (IOException e) {
            throw new SyncException("Cannot create storage file: " + filePath);
        }
    }

    /**
     * Saves a list of events and their corresponding priorities to the storage file.
     *
     * @param events List of events to save.
     * @param allPriorities List of priorities corresponding to the events.
     * @throws SyncException If an error occurs while saving the events.
     */
    public void saveEvents(List<Event> events, ArrayList<String> allPriorities) throws SyncException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                String priority = (i < allPriorities.size()) ? allPriorities.get(i) : Priority.LOW;
                writer.write(formatEvent(event, priority));
                writer.newLine();
            }
            logger.info("Events saved successfully to " + filePath);
        } catch (IOException e) {
            throw new SyncException("Error saving events: " + e.getMessage());
        }
    }

    /**
     * Loads events from the storage file and returns them.
     *
     * @return List of events loaded from the file.
     * @throws SyncException If an error occurs while loading the events.
     */
    public ArrayList<Event> loadEvents() throws SyncException {
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<String> loadedPriorities = new ArrayList<>();
        List<Participant> allParticipants = userStorage.loadUsers();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = parseEventLine(line.trim());
                    Event event = parseEvent(parts, allParticipants);
                    String priority = parts[5];
                    loadedPriorities.add(priority);
                    events.add(event);
                } catch (Exception e) {
                    logger.warning("Skipping corrupted line: " + line + " | Error: " + e.getMessage());
                    throw new SyncException("Skipping corrupted line: " + line + " | Error: " + e.getMessage());
                }
            }
            Priority.loadFromStorage(loadedPriorities);
            logger.info("Events loaded successfully from " + filePath);
        } catch (IOException | SyncException e) {
            throw new SyncException("Error reading file: " + e.getMessage());
        }
        return events;
    }

    /**
     * Formats an event as a string for saving to the file.
     *
     * @param event The event to format.
     * @param priority The priority of the event.
     * @return A formatted string representing the event.
     */
    private String formatEvent(Event event, String priority) {
        assert event != null : "Event cannot be null";
        assert priority != null : "Priority cannot be null";

        String participantsStr = event.getParticipants().stream()
                .map(p -> p.getName() + ":" + p.getAccessLevel())
                .collect(Collectors.joining(","));

        return String.join(" | ",
                event.getName(),
                event.getStartTime().format(formatter),
                event.getEndTime().format(formatter),
                event.getLocation(),
                event.getDescription(),
                priority,
                participantsStr);
    }

    /**
     * Parses a line from the file and splits it into components.
     *
     * @param line The line to parse.
     * @return An array of strings representing the components of the line.
     * @throws SyncException If the line is improperly formatted.
     */
    private String[] parseEventLine(String line) throws SyncException {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 5) {
            throw new SyncException("Missing required fields");
        }

        String[] normalized = new String[7];
        System.arraycopy(parts, 0, normalized, 0, Math.min(parts.length, 5));

        normalized[5] = (parts.length > 5 && !parts[5].isEmpty()) ? parts[5] : "LOW";
        normalized[6] = (parts.length > 6) ? parts[6] : "";

        return normalized;
    }

    /**
     * Parses an event from the given components and adds participants.
     *
     * @param parts The components representing the event.
     * @param allParticipants The list of all participants to assign to the event.
     * @return The parsed event.
     * @throws SyncException If the event is improperly formatted.
     */
    private Event parseEvent(String[] parts, List<Participant> allParticipants) throws SyncException {
        assert parts != null : "Event parts cannot be null";
        assert allParticipants != null : "All participants cannot be null";

        try {
            if (parts[0] == null || parts[1] == null || parts[2] == null) {
                throw new SyncException("Missing required fields");
            }

            Event event = new Event(
                    parts[0],
                    LocalDateTime.parse(parts[1], formatter),
                    LocalDateTime.parse(parts[2], formatter),
                    parts[3],
                    parts[4]
            );

            if (!parts[6].isEmpty()) {
                String[] participantEntries = parts[6].split("\\s*,\\s*");
                for (String entry : participantEntries) {
                    try {
                        String[] participantData = entry.split("\\s*:\\s*");
                        if (participantData.length != 2) {
                            throw new SyncException("Invalid participant format: " + entry);
                        }

                        String name = participantData[0];
                        AccessLevel accessLevel = AccessLevel.valueOf(participantData[1].toUpperCase());

                        Participant participant = allParticipants.stream()
                                .filter(p -> p.getName().equalsIgnoreCase(name))
                                .findFirst()
                                .orElseThrow(() -> new SyncException("Participant not found: " + name));

                        event.addParticipant(participant);
                    } catch (IllegalArgumentException e) {
                        throw new SyncException("Invalid participant data: " + entry);
                    }
                }
            }
            return event;
        } catch (Exception e) {
            throw new SyncException("Failed to parse event: " + e.getMessage());
        }
    }
}
