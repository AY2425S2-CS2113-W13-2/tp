package commandfactory;

import command.Command;
import command.CreateUserCommand;
import command.LoginCommand;
import exception.SyncException;
import participant.Participant;
import participant.AvailabilitySlot;
import participant.ParticipantManager;
import ui.UI;
import parser.CommandParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Factory class responsible for creating a CreateUserCommand.
 * This factory gathers all necessary information from the user to create a new participant.
 */
public class CreateUserCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    private final UI ui;
    private final ParticipantManager participantManager;

    /**
     * Constructor to initialize the factory with the UI and participant manager.
     *
     * @param ui The UI used to interact with the user
     * @param participantManager The participant manager to handle participant data
     */
    public CreateUserCommandFactory(UI ui, ParticipantManager participantManager) {
        this.ui = ui;
        this.participantManager = participantManager;
    }

    /**
     * Creates a CreateUserCommand based on the user's input.
     * This method will ask the user for participant details such as name, password,
     * access level, and availability slots, and then create a new participant.
     *
     * @return A new CreateUserCommand
     * @throws SyncException If an error occurs during the command creation
     */
    @Override
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting CreateUserCommandFactory");
        String participantName = askParticipantName();
        if (participantManager.getParticipant(participantName) != null) {
            throw new SyncException("Participant " + participantName + " already exists. Please enter 'create' " +
                    "and try another name.");
        }
        String password = askPassword();
        Participant.AccessLevel accessLevel = askAccessLevel();
        ArrayList<AvailabilitySlot> availabilitySlots = askAvailabilitySlots();

        if (availabilitySlots.isEmpty()) {
            throw new SyncException("❌ No valid availability slots provided. Cannot create participant." +
                    "Please enter 'create' and try again.");
        }

        Participant participant = new Participant(participantName, password, accessLevel);
        participant.setAvailableTimes(availabilitySlots);
        return new CreateUserCommand(participant);
    }

    /**
     * Prompts the user for the participant's name.
     *
     * @return the participant name entered by the user
     * @throws SyncException if there is an error while asking the name
     */
    private String askParticipantName() throws SyncException {
        return ui.askParticipantName();
    }

    /**
     * Prompts the user for the participant's password.
     *
     * @return the password entered by the user
     * @throws SyncException if there is an error while asking the password
     */
    private String askPassword() throws SyncException {
        return ui.askPassword();
    }

    /**
     * Prompts the user for the participant's access level.
     *
     * @return the access level entered by the user
     * @throws SyncException if there is an error while asking the access level
     */
    private Participant.AccessLevel askAccessLevel() throws SyncException {
        return CommandParser.askAccessLevel();
    }

    /**
     * Prompts the user for availability slots and collects them.
     *
     * @return a list of availability slots entered by the user
     * @throws SyncException if there is an error while asking for availability slots
     */
    private ArrayList<AvailabilitySlot> askAvailabilitySlots() throws SyncException {
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        int numSlots = askNumberOfSlots();

        for (int i = 0; i < numSlots; i++) {
            AvailabilitySlot slot = askAvailabilitySlot(i + 1);
            if (slot != null) {
                slots.add(slot);
            }
        }

        return slots;
    }

    /**
     * Prompts the user to enter the number of availability slots.
     *
     * @return the number of availability slots
     * @throws SyncException if the user input is invalid
     */
    private int askNumberOfSlots() throws SyncException {
        ui.showMessage("Enter number of availability slots (maximum 10) (or type 'exit' to cancel): ");
        String input = ui.readLine().trim(); // Only call readLine ONCE
        ui.checkForExit(input);

        try {
            int numSlots = Integer.parseInt(input);
            if (numSlots <= 0) {
                throw new SyncException("❌ Number of availability slots must be at least 1. " +
                        "Please enter 'create' and try again.");
            } else if (numSlots > 10) {
                ui.showMessage("You entered more than 10 slots. The number of slots has been set to 10 by default.");
            }
            return Math.min(numSlots, 10);
        } catch (NumberFormatException e) {
            throw new SyncException("❌ Invalid input. Please enter a valid number between 1 and 10. " +
                    "Please enter 'create' and try again.");
        }
    }

    /**
     * Prompts the user to enter the details for a specific availability slot.
     *
     * @param slotIndex the index of the slot being asked for
     * @return an AvailabilitySlot object containing the start and end times
     * @throws SyncException if there is an error while asking for slot details
     */
    private AvailabilitySlot askAvailabilitySlot(int slotIndex) throws SyncException {
        LocalDateTime start = askStartTime(slotIndex);
        LocalDateTime end = askEndTime(slotIndex);

        if (end.isBefore(start)) {
            throw new SyncException("❌ End time must be after start time. Please enter 'create' and try again.");
        }

        return new AvailabilitySlot(start, end);
    }

    /**
     * Prompts the user to enter the start time for an availability slot.
     *
     * @param slotIndex the index of the slot being asked for
     * @return the start time as a LocalDateTime
     * @throws SyncException if the user input is invalid
     */
    private LocalDateTime askStartTime(int slotIndex) throws SyncException {
        ui.showMessage("Enter start time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm) " +
                "(or type 'exit' to cancel): ");
        String startTimeStr = ui.readLine().trim();
        ui.checkForExit(startTimeStr);
        return CommandParser.parseDateTime(startTimeStr);
    }

    /**
     * Prompts the user to enter the end time for an availability slot.
     *
     * @param slotIndex the index of the slot being asked for
     * @return the end time as a LocalDateTime
     * @throws SyncException if the user input is invalid
     */
    private LocalDateTime askEndTime(int slotIndex) throws SyncException {
        ui.showMessage("Enter end time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm)" +
                " (or type 'exit' to cancel): ");
        String endTimeStr = ui.readLine().trim();
        ui.checkForExit(endTimeStr);
        return CommandParser.parseDateTime(endTimeStr);
    }
}
