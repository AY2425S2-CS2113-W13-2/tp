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

public class CreateUserCommandFactory implements CommandFactory {
    private final UI ui;
    private final ParticipantManager participantManager;

    public CreateUserCommandFactory(UI ui, ParticipantManager participantManager) {
        this.ui = ui;
        this.participantManager = participantManager;
    }

    @Override
    public Command createCommand() throws SyncException {
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

    private String askParticipantName() throws SyncException {
        return ui.askParticipantName();
    }

    private String askPassword() throws SyncException {
        return ui.askPassword();
    }

    private Participant.AccessLevel askAccessLevel() throws SyncException {
        return CommandParser.askAccessLevel();
    }

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

    private int askNumberOfSlots() throws SyncException {
        int numSlots;
        ui.showMessage("Enter number of availability slots (maximum 10) (or type 'exit' to cancel): ");
        String input = ui.readLine();
        ui.checkForExit(input);

        try {
            numSlots = Integer.parseInt(input.trim());
            if (numSlots <= 0) {
                throw new SyncException("❌ Number of availability slots must be at least 1." +
                        "\"Please enter 'create' and try again.");
            } else if (numSlots > 10) {
                ui.showMessage("You entered more than 10 slots. The number of slots has been set to 10 by default.");
            }
            return Math.min(numSlots, 10);
        } catch (NumberFormatException e) {
            throw new SyncException("❌ Invalid input. Please enter a valid number between 1 and 10. " +
                    "\"Please enter 'create' and try again.");
        }
    }

    private AvailabilitySlot askAvailabilitySlot(int slotIndex) throws SyncException {
        LocalDateTime start = askStartTime(slotIndex);
        LocalDateTime end = askEndTime(slotIndex);

        if (end.isBefore(start)) {
            throw new SyncException("❌ End time must be after start time. Please enter 'create' and try again.");
        }

        return new AvailabilitySlot(start, end);
    }


    private LocalDateTime askStartTime(int slotIndex) throws SyncException {
        ui.showMessage("Enter start time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm) (or type 'exit' to cancel): ");
        String startTimeStr = ui.readLine().trim();
        ui.checkForExit(startTimeStr);
        return CommandParser.parseDateTime(startTimeStr);
    }

    private LocalDateTime askEndTime(int slotIndex) throws SyncException {
        ui.showMessage("Enter end time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm) (or type 'exit' to cancel): ");
        String endTimeStr = ui.readLine().trim();
        ui.checkForExit(endTimeStr);
        return CommandParser.parseDateTime(endTimeStr);
    }
}
