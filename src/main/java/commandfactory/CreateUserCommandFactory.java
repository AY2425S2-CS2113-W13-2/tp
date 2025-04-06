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
            throw new SyncException("Participant " + participantName + " already exists");
        }
        String password = askPassword();
        Participant.AccessLevel accessLevel = askAccessLevel();
        ArrayList<AvailabilitySlot> availabilitySlots = askAvailabilitySlots();

        if (availabilitySlots.isEmpty()) {
            throw new SyncException("❌ No valid availability slots provided. Cannot create participant.");
        }

        Participant participant = new Participant(participantName, password, accessLevel);
        participant.setAvailableTimes(availabilitySlots);
        return new CreateUserCommand(participant);
    }

    private String askParticipantName() {
        return ui.askParticipantName();
    }

    private String askPassword() {
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
        ui.showMessage("Enter number of availability slots (maximum 10): ");

        try {
            numSlots = Integer.parseInt(ui.readLine().trim());
            if (numSlots <= 0) {
                throw new SyncException("❌ Number of availability slots must be at least 1.");
            }
            return Math.min(numSlots, 10);
        } catch (NumberFormatException e) {
            throw new SyncException("❌ Invalid input. Please enter a valid number between 1 and 10.");
        }
    }

    private AvailabilitySlot askAvailabilitySlot(int slotIndex) throws SyncException {
        try {
            LocalDateTime start = askStartTime(slotIndex);
            LocalDateTime end = askEndTime(slotIndex);

            if (end.isBefore(start)) {
                ui.showMessage("❌ End time must be after start time. Skipping this slot.");
                return null;
            }
            return new AvailabilitySlot(start, end);
        } catch (SyncException e) {
            throw new SyncException("❌ " + e.getMessage() + " Skipping this slot.");
        }
    }

    private LocalDateTime askStartTime(int slotIndex) throws SyncException {
        ui.showMessage("Enter start time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm): ");
        String startTimeStr = ui.readLine().trim();
        return CommandParser.parseDateTime(startTimeStr);
    }

    private LocalDateTime askEndTime(int slotIndex) throws SyncException {
        ui.showMessage("Enter end time for availability slot " + slotIndex + " (in format yyyy-MM-dd HH:mm): ");
        String endTimeStr = ui.readLine().trim();
        return CommandParser.parseDateTime(endTimeStr);
    }
}
