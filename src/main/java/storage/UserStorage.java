package storage;

import participant.Participant;
import participant.Participant.AccessLevel;
import participant.AvailabilitySlot;
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

/**
 * Handles storage-related operations for managing participants, including their availability slots.
 */
public class UserStorage {
    private final String filePath;
    private final DateTimeFormatter slotFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public UserStorage(String filePath) throws SyncException {
        this.filePath = filePath;
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

    public void saveUsers(List<Participant> participants) throws SyncException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Participant participant : participants) {
                writer.write(formatParticipant(participant));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SyncException("Error saving users: " + e.getMessage());
        }
    }

    public ArrayList<Participant> loadUsers() throws SyncException {
        ArrayList<Participant> participants = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Participant participant = parseParticipant(line);
                    participants.add(participant);
                } catch (Exception e) {
                    throw new SyncException("Skipping corrupted line: " + line);
                }
            }
        } catch (IOException | SyncException e) {
            throw new SyncException("Error reading file: " + e.getMessage());
        }

        return participants;
    }

    private String formatParticipant(Participant participant) {
        // Format basic info
        StringBuilder sb = new StringBuilder();
        sb.append(participant.getName()).append(" | ")
                .append(participant.getAccessLevel().name()).append(" | ")
                .append(participant.getPassword());

        // Format availability slots
        sb.append(" | ");
        if (!participant.getAvailableTimes().isEmpty()) {
            List<String> slotStrings = new ArrayList<>();
            for (AvailabilitySlot slot : participant.getAvailableTimes()) {
                slotStrings.add(
                        slot.getStartTime().format(slotFormatter) + "," +
                                slot.getEndTime().format(slotFormatter)
                );
            }
            sb.append(String.join(";", slotStrings));
        }

        return sb.toString();
    }

    private Participant parseParticipant(String line) throws SyncException {
        String[] parts = line.split("\\s*\\|\\s*", -1); // handles spaces around `|`
        if (parts.length < 3) {
            throw new IllegalArgumentException("Missing required fields");
        }

        Participant participant = new Participant(
                parts[0].trim(),
                parts[2].trim(),
                AccessLevel.valueOf(parts[1].trim().toUpperCase())
        );

        if (parts.length > 3 && !parts[3].isEmpty()) {
            String[] slotEntries = parts[3].split(";");
            for (String entry : slotEntries) {
                try {
                    String[] times = entry.split(",");
                    if (times.length == 2) {
                        LocalDateTime start = LocalDateTime.parse(times[0].trim(), slotFormatter);
                        LocalDateTime end = LocalDateTime.parse(times[1].trim(), slotFormatter);
                        participant.addAvailableTime(start, end);
                    }
                } catch (Exception e) {
                    throw new SyncException(String.format("Invalid slot format '%s': %s", entry, e.getMessage()));

                }
            }
        }
        return participant;
    }

    public Participant findUserByName(List<Participant> participants, String name) {
        return participants.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
