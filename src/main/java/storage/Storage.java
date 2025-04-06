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

public class Storage {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final String filePath;
    private final UserStorage userStorage;

    public Storage(String filePath, UserStorage userStorage) throws SyncException {
        this.filePath = filePath;
        this.userStorage = userStorage;
        ensureFileExists();
    }

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

    public void saveEvents(List<Event> events, ArrayList<String> allPriorities) throws SyncException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<String> priorities = Priority.getAllPriorities();
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                String priority = (i < priorities.size()) ? priorities.get(i) : Priority.LOW;
                writer.write(formatEvent(event, priority));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SyncException("Error saving events: " + e.getMessage());
        }
    }

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
                    throw new SyncException("Skipping corrupted line: " + line + " | Error: " + e.getMessage());
                }
            }
            Priority.loadFromStorage(loadedPriorities);
        } catch (IOException | SyncException e) {
            throw new SyncException("Error reading file: " + e.getMessage());
        }
        return events;
    }

    private String formatEvent(Event event, String priority) {
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

    private Event parseEvent(String[] parts, List<Participant> allParticipants) throws SyncException {
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
