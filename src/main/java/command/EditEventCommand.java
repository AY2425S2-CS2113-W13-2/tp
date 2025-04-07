package command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

/**
 * Represents a command that allows editing an existing event's details.
 */
public class EditEventCommand extends Command {
    private final int index;
    private final ParticipantManager participantManager;

    /**
     * Constructs an {@code EditEventCommand} with the specified event index and participant manager.
     *
     * @param index the index of the event to be edited.
     * @param participantManager the manager handling participant information.
     */
    public EditEventCommand(int index, ParticipantManager participantManager) {
        this.index = index;
        this.participantManager = participantManager;
    }

    /**
     * Executes the event editing process by allowing the user to select specific fields to edit.
     * Only administrators can edit events.
     *
     * @param events the event manager managing the events.
     * @param ui the user interface for input/output.
     * @param participantManager the participant manager for checking access and availability.
     * @throws SyncException if the user is not an admin or input is invalid.
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("You are not currently administrator.");
        }

        Event event = events.getEvent(index);
        assert event != null : "Event to edit should not be null";

        boolean editing = true;

        while (editing) {
            ui.showEditCommandMessageWithOptions(event);

            Integer choice = ui.readInt();
            if (choice == null) {
                ui.showEditCommandCorrectFormat();
                continue;
            }

            switch (choice) {
            case 1:
                editing = editName(event, ui);
                break;
            case 2:
                editing = editStartTime(event, ui);
                break;
            case 3:
                editing = editEndTime(event, ui);
                break;
            case 4:
                editing = editLocation(event, ui);
                break;
            case 5:
                editing = editDescription(event, ui);
                break;
            case 6:
                editing = false;
                ui.showMessage("✅ Event editing completed.");
                break;
            default:
                ui.showEditCommandCorrectFormat();
            }

            events.updateEvent(index, event);
        }
    }

    /**
     * Prompts the user to edit the event name.
     */
    private boolean editName(Event event, UI ui) {
        ui.showEditCommandStep1();
        String newName = ui.readLine().trim();
        if (newName.equalsIgnoreCase("exit")) {
            ui.showMessage("❌ Name editing cancelled.");
            return true;
        }
        event.setName(newName);
        ui.showMessage("✅ Name updated:");
        return true;
    }

    /**
     * Prompts the user to edit the event start time with validations.
     */
    private boolean editStartTime(Event event, UI ui) throws SyncException {
        while (true) {
            LocalDateTime newStart = getValidDateTime(ui, "start");
            if (newStart == null) {
                ui.showMessage("❌ Start time editing cancelled.");
                return true;
            }

            if (newStart.isAfter(event.getEndTime())) {
                ui.showMessage(SyncException.startTimeAfterEndTimeMessage());
                continue;
            }

            if (!checkParticipantAvailability(event, newStart, event.getEndTime(), ui)) {
                ui.showMessage(SyncException.participantConflictMessage());
                continue;
            }

            event.setStartTime(newStart);
            return true;
        }
    }

    /**
     * Prompts the user to edit the event end time with validations.
     */
    private boolean editEndTime(Event event, UI ui) throws SyncException {
        while (true) {
            ui.showEditCommandStep3();
            LocalDateTime newEnd = getValidDateTime(ui, "end");
            if (newEnd == null) {
                ui.showMessage("❌ End time editing cancelled.");
                return true;
            }

            if (newEnd.isBefore(event.getStartTime())) {
                ui.showMessage(SyncException.endTimeBeforeStartTimeMessage());
                continue;
            }

            if (!checkParticipantAvailability(event, event.getStartTime(), newEnd, ui)) {
                ui.showMessage(SyncException.participantConflictMessage());
                continue;
            }

            event.setEndTime(newEnd);
            return true;
        }
    }

    /**
     * Prompts the user to edit the event location.
     */
    private boolean editLocation(Event event, UI ui) {
        ui.showEditCommandStep4();
        String newLocation = ui.readLine().trim();
        if (newLocation.equalsIgnoreCase("exit")) {
            ui.showMessage("❌ Location editing cancelled.");
            return true;
        }
        event.setLocation(newLocation);
        ui.showMessage("✅ Location updated:");
        return true;
    }

    /**
     * Prompts the user to edit the event description.
     */
    private boolean editDescription(Event event, UI ui) {
        ui.showEditCommandStep5();
        String newDesc = ui.readLine().trim();
        if (newDesc.equalsIgnoreCase("exit")) {
            ui.showMessage("❌ Description editing cancelled.");
            return true;
        }
        event.setDescription(newDesc);
        ui.showMessage("✅ Description updated:");
        return true;
    }

    /**
     * Prompts the user for a valid date and time input.
     */
    private LocalDateTime getValidDateTime(UI ui, String type) throws SyncException {
        boolean firstPrompt = true;

        while (true) {
            if (firstPrompt) {
                ui.showMessage("Enter New " + type.substring(0, 1).toUpperCase() + type.substring(1)
                        + " Time (YYYY-MM-DD HH:MM) or type 'exit' to cancel:");
                firstPrompt = false;
            } else {
                ui.showMessage("❌ Invalid format! Please re-enter or type 'exit' to cancel:");
            }

            String input = ui.readLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                return null;
            }

            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                throw new SyncException("cInvalid format! Please re-enter or type 'exit' to cancel:");
            }
        }
    }

    /**
     * Checks if all participants in the event are available during the new time.
     */
    private boolean checkParticipantAvailability(Event event, LocalDateTime newStart,
                                                 LocalDateTime newEnd, UI ui) {
        boolean allAvailable = true;
        for (Participant p : event.getParticipants()) {
            if (!p.isAvailableDuring(newStart, newEnd)) {
                ui.showMessage(SyncException.participantUnavailableDuringEditError(
                        p.getName(), newStart, newEnd));
                ui.showMessage("   📆 Available slots for " + p.getName() + ":");
                int index = 1;
                for (var slot : p.getAvailableTimes()) {
                    ui.showMessage("     [" + index++ + "] " + slot);
                }
                allAvailable = false;
            }
        }
        return allAvailable;
    }
}
