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
 * Command that allows the administrator to edit event details such as name, start time,
 * end time, location, and description. It checks for participant availability and
 * ensures that no conflicts arise during the editing process.
 */
public class EditEventCommand extends Command {
    private final int index;
    private final ParticipantManager participantManager;

    /**
     * Constructor to initialize the EditEventCommand with an event index and participant manager.
     *
     * @param index The index of the event to edit
     * @param participantManager The participant manager responsible for managing participants
     */
    public EditEventCommand(int index, ParticipantManager participantManager) {
        this.index = index;
        this.participantManager = participantManager;
    }

    /**
     * Executes the edit event command. This method allows an administrator to modify event details.
     * It prompts the user to edit specific event properties, such as name, start time, end time, location,
     * and description, and ensures the consistency of participant availability.
     *
     * @param events The event manager that holds the events
     * @param ui The UI interface to interact with the user
     * @param participantManager The participant manager to handle participant data
     * @throws SyncException If an error occurs
     * during the editing process, such as invalid data or participant unavailability
     */
    @Override
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

    /**
     * Edits the event's name.
     *
     * @param event The event to edit
     * @param ui The UI interface for interaction
     * @return true if the name was successfully updated, false if editing was cancelled
     * @throws SyncException If an error occurs during editing
     */
    private boolean editName(Event event, UI ui) throws SyncException {
        ui.showEditCommandStep1();
        String newName = ui.readLine().trim();
        ui.checkForExit(newName);
        event.setName(newName);
        ui.showMessage("✅ Name updated:");
        return true;
    }

    /**
     * Edits the event's start time.
     *
     * @param event The event to edit
     * @param ui The UI interface for interaction
     * @return true if the start time was successfully updated, false if editing was cancelled
     * @throws SyncException If an error occurs during editing, such as an invalid time or participant unavailability
     */
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

    /**
     * Edits the event's end time.
     *
     * @param event The event to edit
     * @param ui The UI interface for interaction
     * @return true if the end time was successfully updated, false if editing was cancelled
     * @throws SyncException If an error occurs during editing, such as an invalid time or participant unavailability
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

    /**
     * Edits the event's location.
     *
     * @param event The event to edit
     * @param ui The UI interface for interaction
     * @return true if the location was successfully updated, false if editing was cancelled
     * @throws SyncException If an error occurs during editing
     */
    private boolean editLocation(Event event, UI ui) throws SyncException {
        ui.showEditCommandStep4();
        String newLocation = ui.readLine().trim();
        ui.checkForExit(newLocation);
        event.setLocation(newLocation);
        ui.showMessage("✅ Location updated:");
        return true;
    }

    /**
     * Edits the event's description.
     *
     * @param event The event to edit
     * @param ui The UI interface for interaction
     * @return true if the description was successfully updated, false if editing was cancelled
     * @throws SyncException If an error occurs during editing
     */
    private boolean editDescription(Event event, UI ui) throws SyncException {
        ui.showEditCommandStep5();
        String newDesc = ui.readLine().trim();
        ui.checkForExit(newDesc);
        event.setDescription(newDesc);
        ui.showMessage("✅ Description updated:");
        return true;
    }

    /**
     * Prompts the user for a valid date-time input for start or end time.
     *
     * @param ui The UI interface for interaction
     * @param type The type of time ("start" or "end")
     * @return The parsed LocalDateTime if valid, null if user cancels
     * @throws SyncException If an error occurs during the input process
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
            ui.checkForExit(input);

            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                // loop will continue and re-show the re-entry prompt
            }
        }
    }

    /**
     * Checks if all participants are available during the specified time range.
     *
     * @param event The event to check
     * @param newStart The new start time for the event
     * @param newEnd The new end time for the event
     * @param ui The UI interface for interaction
     * @return true if all participants are available, false otherwise
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
