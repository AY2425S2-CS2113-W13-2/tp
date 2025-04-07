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

            events.save();
            participantManager.save();
        }
    }

    private boolean editName(Event event, UI ui) throws SyncException {
        ui.showEditCommandStep1();
        String newName = ui.readLine().trim();
        ui.checkForExit(newName);
        event.setName(newName);
        ui.showMessage("✅ Name updated:");
        return true;
    }

    private boolean editStartTime(Event event, UI ui) throws SyncException {
        while (true) {

            LocalDateTime newStart = getValidDateTime(ui, "start");
            if (newStart == null) {
                ui.showMessage("❌ Start time editing cancelled.");
                return true; // return to main edit menu
            }

            if (newStart.isAfter(event.getEndTime())) {
                ui.showMessage(SyncException.startTimeAfterEndTimeMessage());
                continue;
            }

            for (Participant p : event.getParticipants()) {
                p.unassignEventTime(event.getStartTime(), event.getEndTime());
            }

            for (Participant p : event.getParticipants()) {
                if (!p.isAvailableDuring(newStart, event.getEndTime())) {
                    for (Participant recover : event.getParticipants()) {
                        recover.assignEventTime(event.getStartTime(), event.getEndTime());
                        participantManager.save(recover);
                    }
                    throw new SyncException(SyncException.participantUnavailableDuringEditError(
                            p.getName(), event.getStartTime(), event.getEndTime()));
                }
            }

            for (Participant p : event.getParticipants()) {
                p.assignEventTime(newStart, event.getEndTime());
                participantManager.save(p);
            }

            event.setStartTime(newStart);
            return true;
        }
    }

    private boolean editEndTime(Event event, UI ui) throws SyncException{
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

            for (Participant p : event.getParticipants()) {
                p.unassignEventTime(event.getStartTime(), event.getEndTime());
            }

            for (Participant p : event.getParticipants()) {
                if (!p.isAvailableDuring(event.getStartTime(), newEnd)) {
                    for (Participant recover : event.getParticipants()) {
                        recover.assignEventTime(event.getStartTime(), event.getEndTime());
                        participantManager.save();
                    }
                    throw new SyncException(SyncException.participantUnavailableDuringEditError(
                            p.getName(), event.getStartTime(), event.getEndTime()));
                }
            }
            for (Participant p : event.getParticipants()) {
                p.assignEventTime(event.getStartTime(), newEnd);
                participantManager.save();
            }

            event.setEndTime(newEnd);
            return true;
        }
    }

    private boolean editLocation(Event event, UI ui) throws SyncException {
        ui.showEditCommandStep4();
        String newLocation = ui.readLine().trim();
        ui.checkForExit(newLocation);
        event.setLocation(newLocation);
        ui.showMessage("✅ Location updated:");
        return true;
    }

    private boolean editDescription(Event event, UI ui) throws SyncException {
        ui.showEditCommandStep5();
        String newDesc = ui.readLine().trim();
        ui.checkForExit(newDesc);
        event.setDescription(newDesc);
        ui.showMessage("✅ Description updated:");
        return true;
    }

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
            ui.checkForExit(input);

            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                // loop will continue and re-show the re-entry prompt
            }
        }
    }

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
