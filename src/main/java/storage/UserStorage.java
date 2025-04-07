package storage;

import participant.Participant;
import participant.Participant.AccessLevel;
import participant.AvailabilitySlot;
import exception.SyncException;
import logger.EventSyncLogger;

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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Handles persistent storage operations for participant data including their availability slots.
 * <p>
 * This class manages reading from and writing to a file-based storage system,
 * ensuring data integrity and proper error handling.
 * </p>
 *
 * @author Your Name
 * @version 1.1
 */
public final class UserStorage {
    private static final DateTimeFormatter SLOT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String FIELD_DELIMITER = " | ";
    private static final String SLOT_DELIMITER = ";";
    private static final String TIME_DELIMITER = ",";

    private final String filePath;

    /**
     * Constructs a UserStorage instance with the specified file path.
     *
     * @param filePath the path to the storage file (must not be null or empty)
     * @throws SyncException if the file cannot be created or accessed
     * @throws IllegalArgumentException if filePath is empty
     * @throws NullPointerException if filePath is null
     */
    public UserStorage(String filePath) throws SyncException {
        validateFilePath(filePath);
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Validates that the file path is not null or empty.
     *
     * @param filePath the file path to validate
     * @throws IllegalArgumentException if file path is empty
     * @throws NullPointerException if file path is null
     */
    private void validateFilePath(String filePath) {
        Objects.requireNonNull(filePath, "File path cannot be null");
        if (filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
    }

    /**
     * Ensures the storage file exists, creating it if necessary.
     *
     * @throws SyncException if the file cannot be created
     */
    private void ensureFileExists() throws SyncException {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                EventSyncLogger.getLogger().info("Created new user storage file: " + filePath);
            }
        } catch (IOException e) {
            String errorMsg = "Cannot create storage file: " + filePath;
            EventSyncLogger.getLogger().log(Level.SEVERE, errorMsg, e);
            throw new SyncException(errorMsg);
        }
    }

    /**
     * Saves the list of participants to the storage file.
     *
     * @param participants the list of participants to save (must not be null)
     * @throws SyncException if an I/O error occurs
     * @throws NullPointerException if participants is null
     */
    public void saveUsers(List<Participant> participants) throws SyncException {
        Objects.requireNonNull(participants, "Participants list cannot be null");

        EventSyncLogger.getLogger().fine("Saving " + participants.size() + " participants to storage");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Participant participant : participants) {
                writer.write(formatParticipant(participant));
                writer.newLine();
            }
            EventSyncLogger.getLogger().info("Successfully saved participants to " + filePath);
        } catch (IOException e) {
            String errorMsg = "Error saving users to " + filePath;
            EventSyncLogger.getLogger().log(Level.SEVERE, errorMsg, e);
            throw new SyncException(errorMsg);
        }
    }

    /**
     * Loads participants from the storage file.
     *
     * @return a list of participants loaded from storage
     * @throws SyncException if the file cannot be read or contains invalid data
     */
    public ArrayList<Participant> loadUsers() throws SyncException {
        ArrayList<Participant> participants = new ArrayList<>();
        int lineNumber = 0;

        EventSyncLogger.getLogger().fine("Loading participants from " + filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    if (!line.trim().isEmpty()) {
                        Participant participant = parseParticipant(line);
                        participants.add(participant);
                    } else {
                        throw new SyncException("Empty participant");
                    }
                } catch (SyncException e) {
                    throw new SyncException(e.getMessage());
                }
            }
            EventSyncLogger.getLogger().info("Successfully loaded " + participants.size() + " participants");
        } catch (IOException e) {
            String errorMsg = "Error reading user storage file: " + filePath;
            EventSyncLogger.getLogger().log(Level.SEVERE, errorMsg, e);
            throw new SyncException(errorMsg);
        }

        return participants;
    }

    /**
     * Formats a participant into a storage string representation.
     *
     * @param participant the participant to format (must not be null)
     * @return the formatted string
     * @throws NullPointerException if participant is null
     */
    private String formatParticipant(Participant participant) {
        Objects.requireNonNull(participant, "Participant cannot be null");

        StringBuilder sb = new StringBuilder();
        // Format basic info
        sb.append(participant.getName()).append(FIELD_DELIMITER)
                .append(participant.getAccessLevel().name()).append(FIELD_DELIMITER)
                .append(participant.getPassword());

        // Format availability slots
        if (!participant.getAvailableTimes().isEmpty()) {
            sb.append(FIELD_DELIMITER);
            List<String> slotStrings = new ArrayList<>();
            for (AvailabilitySlot slot : participant.getAvailableTimes()) {
                slotStrings.add(
                        slot.getStartTime().format(SLOT_FORMATTER) + TIME_DELIMITER +
                                slot.getEndTime().format(SLOT_FORMATTER)
                );
            }
            sb.append(String.join(SLOT_DELIMITER, slotStrings));
        }

        return sb.toString();
    }

    /**
     * Parses a participant from a storage string representation.
     *
     * @param line the line to parse (must not be null or empty)
     * @return the parsed Participant
     * @throws SyncException if the line has invalid format
     */
    private Participant parseParticipant(String line) throws SyncException {
        Objects.requireNonNull(line, "Line cannot be null");
        if (line.trim().isEmpty()) {
            throw new SyncException("Empty line cannot be parsed");
        }

        String[] parts = line.split("\\s*\\|\\s*", -1); // handles spaces around delimiter
        if (parts.length < 3) {
            throw new SyncException("Missing required fields in participant data");
        }

        try {
            Participant participant = new Participant(
                    parts[0].trim(),
                    parts[2].trim(),
                    AccessLevel.valueOf(parts[1].trim().toUpperCase())
            );

            if (parts.length > 3 && !parts[3].isEmpty()) {
                parseAvailabilitySlots(participant, parts[3]);
            }
            return participant;
        } catch (IllegalArgumentException e) {
            throw new SyncException("Invalid participant data: " + e.getMessage());
        }
    }

    /**
     * Parses and adds availability slots to a participant.
     *
     * @param participant the participant to add slots to
     * @param slotsData the string containing slot data
     * @throws SyncException if slot data is invalid
     */
    private void parseAvailabilitySlots(Participant participant, String slotsData) throws SyncException {
        String[] slotEntries = slotsData.split(SLOT_DELIMITER);
        for (String entry : slotEntries) {
            try {
                String[] times = entry.split(TIME_DELIMITER);
                if (times.length != 2) {
                    throw new SyncException("Invalid time slot format: " + entry);
                }

                LocalDateTime start = LocalDateTime.parse(times[0].trim(), SLOT_FORMATTER);
                LocalDateTime end = LocalDateTime.parse(times[1].trim(), SLOT_FORMATTER);

                if (!end.isAfter(start)) {
                    throw new SyncException("End time must be after start time in slot: " + entry);
                }

                participant.addAvailableTime(start, end);
            } catch (DateTimeParseException e) {
                throw new SyncException("Invalid date/time format in slot: " + entry);
            }
        }
    }

    /**
     * Finds a participant by name in the given list.
     *
     * @param participants the list of participants to search (must not be null)
     * @param name the name to search for (must not be null)
     * @return the found participant or null if not found
     * @throws NullPointerException if either parameter is null
     */
    public Participant findUserByName(List<Participant> participants, String name) {
        Objects.requireNonNull(participants, "Participants list cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");

        return participants.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
