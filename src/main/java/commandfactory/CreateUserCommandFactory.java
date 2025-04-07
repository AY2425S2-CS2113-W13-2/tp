package commandfactory;

import command.Command;
import command.CreateUserCommand;
import parser.CommandParser;
import participant.Participant;
import participant.AvailabilitySlot;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Factory class for creating CreateUserCommand objects.
 * This factory handles the creation of commands that register new participants in the system.
 * It guides users through the process of providing necessary information such as name, password,
 * access level, and availability slots.
 */
public class CreateUserCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(CreateUserCommandFactory.class.getName());
    private final UI ui;
    private final ParticipantManager participantManager;

    /**
     * Constructs a new CreateUserCommandFactory with the specified dependencies.
     *
     * @param ui the user interface for collecting input
     * @param participantManager the manager handling participant data
     */
    public CreateUserCommandFactory(UI ui, ParticipantManager participantManager) {
        assert ui != null : "UI should not be null";
        assert participantManager != null : "ParticipantManager should not be null";

        this.ui = ui;
        this.participantManager = participantManager;
        LOGGER.info("CreateUserCommandFactory initialized");
    }

    /**
     * Creates a CreateUserCommand after collecting and validating all necessary participant information
     * including name, password, access level, and availability slots.
     *
     * @return a new CreateUserCommand containing the participant to be created
     * @throws SyncException if there are issues with the participant data or if a participant
     *                       with the same name already exists
     */
    @Override
    public Command createCommand() throws SyncException {
        LOGGER.info("Creating CreateUserCommand");

        String participantName = askParticipantName();
        assert participantName != null && !participantName.isEmpty() : "Participant name should not be empty";

        if (participantManager.getParticipant(participantName) != null) {
            LOGGER.warning("Participant creation failed: Name already exists: " + participantName);
            throw new SyncException("Participant " + participantName + " already exists. Please enter 'create' " +
                    "and try another name.");
        }

        String password = askPassword();
        assert password != null && !password.isEmpty() : "Password should not be empty";

        Participant.AccessLevel accessLevel = askAccessLevel();
        assert accessLevel != null : "Access level should not be null";

        ArrayList<AvailabilitySlot> availabilitySlots = askAvailabilitySlots();
        assert availabilitySlots != null : "Availability slots should not be null";

        if (availabilitySlots.isEmpty()) {
            LOGGER.warning("Participant creation failed: No valid availability slots provided");
            throw new SyncException("❌ No valid availability slots provided. Cannot create participant." +
                    "Please enter 'create' and try again.");
        }

        LOGGER.log(Level.INFO, "Creating new participant: {0} with access level: {1}",
                new Object[]{participantName, accessLevel});
        Participant participant = new Participant(participantName, password, accessLevel);
        participant.setAvailableTimes(availabilitySlots);

        LOGGER.info("CreateUserCommand created successfully");
        return new CreateUserCommand(participant);
    }

    /**
     * Prompts the user to enter a participant name.
     *
     * @return the participant name entered by the user
     * @throws SyncException if there are issues with the input
     */
    private String askParticipantName() throws SyncException {
        LOGGER.info("Asking for participant name");
        return ui.askParticipantName();
    }

    /**
     * Prompts the user to enter a password.
     *
     * @return the password entered by the user
     * @throws SyncException if there are issues with the input
     */
    private String askPassword() throws SyncException {
        LOGGER.info("Asking for password");
        return ui.askPassword();
    }

    /**
     * Prompts the user to select an access level.
     *
     * @return the access level selected by the user
     * @throws SyncException if there are issues with the input
     */
    private Participant.AccessLevel askAccessLevel() throws SyncException {
        LOGGER.info("Asking for access level");
        return CommandParser.askAccessLevel();
    }

    /**
     * Prompts the user to enter availability slots.
     *
     * @return a list of availability slots entered by the user
     * @throws SyncException if there are issues with the input
     */
    private ArrayList<AvailabilitySlot> askAvailabilitySlots() throws SyncException {
        LOGGER.info("Asking for availability slots");
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        int numSlots = askNumberOfSlots();

        for (int i = 0; i < numSlots; i++) {
            try {
                AvailabilitySlot slot = askAvailabilitySlot(i + 1);
                if (slot != null) {
                    slots.add(slot);
                    LOGGER.log(Level.INFO, "Added availability slot {0}: {1} to {2}",
                            new Object[]{i + 1, slot.getStartTime(), slot.getEndTime()});
                }
            } catch (SyncException e) {
                LOGGER.log(Level.WARNING, "Error adding slot {0}: {1}", new Object[]{i + 1, e.getMessage()});
                throw e;
            }
        }

        return slots;
    }

    /**
     * Prompts the user to enter the number of availability slots they want to add.
     *
     * @return the number of slots to add (between 1 and 10)
     * @throws SyncException if the input is invalid or out of range
     */
    private int askNumberOfSlots() throws SyncException {
        LOGGER.info("Asking for number of availability slots");
        int numSlots;
        ui.showMessage("Enter number of availability slots (maximum 10) (or type 'exit' to cancel): ");
        String input = ui.readLine();
        ui.checkForExit(input);

        try {
            numSlots = Integer.parseInt(input.trim());
            if (numSlots <= 0) {
                LOGGER.warning("Invalid number of slots: " + numSlots + " (must be at least 1)");
                throw new SyncException("❌ Number of availability slots must be at least 1." +
                        "\"Please enter 'create' and try again.");
            } else if (numSlots > 10) {
                LOGGER.info("User entered more than 10 slots, limiting to 10");
                ui.showMessage("You entered more than 10 slots. The number of slots has been set to 10 by default.");
            }
            return Math.min(numSlots, 10);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid input for number of slots: " + input);
            throw new SyncException("❌ Invalid input. Please enter a valid number between 1 and 10. " +
                    "\"Please enter 'create' and try again.");
        }
    }

    /**
     * Prompts the user to enter the start and end times for an availability slot.
     *
     * @param slotIndex the index of the slot being added
     * @return a new AvailabilitySlot with the specified start and end times
     * @throws SyncException if the times are invalid or if the end time is before the start time
     */
    private AvailabilitySlot askAvailabilitySlot(int slotIndex) throws SyncException {
        LOGGER.log(Level.INFO, "Asking for availability slot {0}", slotIndex);
        LocalDateTime start = askStartTime(slotIndex);
        LocalDateTime end = askEndTime(slotIndex);

        assert start != null : "Start time should not be null";
        assert end != null : "End time should not be null";

        if (end.isBefore(start)) {
            LOGGER.warning("Invalid slot: End time is before start time");
            throw new SyncException("❌ End time must be after start time. Please enter 'create' and try again.");
        }

        return new AvailabilitySlot(start, end);
    }

    /**
     * Prompts the user to enter the start time for an availability slot.
     *
     * @param slotIndex the index of the slot being added
     * @return the parsed LocalDateTime representing the start time
     * @throws SyncException if the input cannot be parsed as a valid date and time
     */
    private LocalDateTime askStartTime(int slotIndex) throws SyncException {
        LOGGER.log(Level.INFO, "Asking for start time for slot {0}", slotIndex);
        ui.showMessage("Enter start time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm) (or type 'exit' to cancel): ");
        String startTimeStr = ui.readLine().trim();
        ui.checkForExit(startTimeStr);

        LocalDateTime startTime = CommandParser.parseDateTime(startTimeStr);
        assert startTime != null : "Parsed start time should not be null";

        return startTime;
    }

    /**
     * Prompts the user to enter the end time for an availability slot.
     *
     * @param slotIndex the index of the slot being added
     * @return the parsed LocalDateTime representing the end time
     * @throws SyncException if the input cannot be parsed as a valid date and time
     */
    private LocalDateTime askEndTime(int slotIndex) throws SyncException {
        LOGGER.log(Level.INFO, "Asking for end time for slot {0}", slotIndex);
        ui.showMessage("Enter end time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm) (or type 'exit' to cancel): ");
        String endTimeStr = ui.readLine().trim();
        ui.checkForExit(endTimeStr);

        LocalDateTime endTime = CommandParser.parseDateTime(endTimeStr);
        assert endTime != null : "Parsed end time should not be null";

        return endTime;
    }
}
