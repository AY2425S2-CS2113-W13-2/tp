package command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

public class EditEventCommand extends Command {
    private final int index;
    private final ParticipantManager participantManager;

    public EditEventCommand(int index, ParticipantManager participantManager) {
        this.index = index;
        this.participantManager = participantManager;
    }

    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("You are not currently administrator.");
        }

        Event event = events.getEvent(index);
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

    private boolean editName(Event event, UI ui) {
        ui.showEditCommandStep1();
        String newName = ui.readLine().trim();
        if (newName.equalsIgnoreCase("exit")) {
            return false;
        }
        event.setName(newName);
        return true;
    }

    private boolean editStartTime(Event event, UI ui) {
        ui.showEditCommandStep2();
        try {
            LocalDateTime newStart = getValidDateTime(ui, "start");
            if (newStart == null) {
                return true;
            }

            if (newStart.isAfter(event.getEndTime())) {
                throw new SyncException(SyncException.startTimeAfterEndTimeMessage());
            }

            if (!checkParticipantAvailability(event, newStart, event.getEndTime(), ui)) {
                throw new SyncException(SyncException.participantConflictMessage());
            }

            event.setStartTime(newStart);
        } catch (SyncException e) {
            ui.showMessage(e.getMessage());
        }
        return true;
    }

    private boolean editEndTime(Event event, UI ui) {
        ui.showEditCommandStep3();
        try {
            LocalDateTime newEnd = getValidDateTime(ui, "end");
            if (newEnd == null) {
                return true;
            }

            if (newEnd.isBefore(event.getStartTime())) {
                throw new SyncException(SyncException.endTimeBeforeStartTimeMessage());
            }

            if (!participantManager.checkCurrentParticipantAvailability(event)) {
                throw new SyncException(SyncException.participantConflictMessage());
            }

            event.setEndTime(newEnd);
        } catch (SyncException e) {
            ui.showMessage(e.getMessage());
        }
        return true;
    }

    private boolean editLocation(Event event, UI ui) {
        ui.showEditCommandStep4();
        String newLocation = ui.readLine().trim();
        if (newLocation.equalsIgnoreCase("exit")) {
            return false;
        }
        event.setLocation(newLocation);
        return true;
    }

    private boolean editDescription(Event event, UI ui) {
        ui.showEditCommandStep5();
        String newDesc = ui.readLine().trim();
        if (newDesc.equalsIgnoreCase("exit")) {
            return false;
        }
        event.setDescription(newDesc);
        return true;
    }

    private LocalDateTime getValidDateTime(UI ui, String type) {
        while (true) {
            String input = ui.readLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                return null;
            }

            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                ui.showMessage(SyncException.invalidDateTimeFormatMessage(type));
                ui.showMessage("Enter " + type + " time again or type 'exit': ");
            }
        }
    }

    private boolean checkParticipantAvailability(Event event, LocalDateTime newStart,
                                                 LocalDateTime newEnd, UI ui) {
        for (Participant p : event.getParticipants()) {
            if (!p.isAvailableDuring(newStart, newEnd)) {
                ui.showMessage(SyncException.participantUnavailableDuringEditError(
                        p.getName(), newStart, newEnd));
                return false;
            }
        }
        return true;
    }
}
