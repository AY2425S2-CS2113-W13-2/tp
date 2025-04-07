package storage;

import command.LoginCommand;
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
import java.util.logging.Logger;

/**
 * Handles storage-related operations for managing participants, including their availability slots.
 * This class provides methods to save and load participant data from a file.
 * It also includes functionality for managing the format of the participant data, including their availability slots.
 */
public class UserStorage {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());


    private final String filePath;
    private final DateTimeFormatter slotFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor that initializes the file path and ensures the storage file exists.
     *
     * @param filePath the file path where participant data is stored
     * @throws SyncException if there is an issue creating or accessing the file
     */
    public UserStorage(String filePath) throws SyncException {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Ensures that the file exists at the specified file path.
     * If the file or directories do not exist, they are created.
     *
     * @throws SyncException if there is an issue creating the file or directories
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
     * Saves a list of participants to the storage file.
     *
     * @param participants a list of participants to be saved
     * @throws SyncException if there is an error while saving the data
     */
    public void saveUsers(List<Participant> participants) throws SyncException {
        assert participants != null;
        LOGGER.info("Attempting save users");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Participant participant : participants) {
                writer.write(formatParticipant(participant));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SyncException("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Loads participants from the storage file.
     *
     * @return a list of participants loaded from the file
     * @throws SyncException if there is an error while reading the data
     */
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

    /**
     * Formats a participant object into a string for saving to the storage file.
     * The string format is as follows: "Name | AccessLevel | Password | AvailabilitySlots".
     *
     * @param participant the participant to be formatted
     * @return the formatted string representation of the participant
     */
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

    /**
     * Parses a string representing a participant from the file into a Participant object.
     *
     * @param line the string representation of the participant
     * @return the parsed Participant object
     * @throws SyncException if there is an error in the data format
     */
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

    /**
     * Finds a participant by name from a list of participants.
     *
     * @param participants the list of participants to search through
     * @param name         the name of the participant to find
     * @return the participant with the specified name, or null if not found
     */
    public Participant findUserByName(List<Participant> participants, String name) {
        return participants.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
