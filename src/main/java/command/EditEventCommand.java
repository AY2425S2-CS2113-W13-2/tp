package command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.logging.Level;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

/**
 * Represents a command to edit an existing event.
 * This command allows administrators to modify event details including name,
 * start time, end time, location, and description.
 */
public class EditEventCommand extends Command {
    private final int index;
    private final ParticipantManager participantManager;
    private static final Logger LOGGER = Logger.getLogger(EditEventCommand.class.getName());

    /**
     * Constructs a new EditEventCommand with the specified event index and participant manager.
     *
     * @param index The index of the event to be edited
     * @param participantManager The participant manager for checking permissions
     * @throws AssertionError if index is negative or participantManager is null
     */
    public EditEventCommand(int index, ParticipantManager participantManager) {
        this.index = index;
        this.participantManager = participantManager;

        assert index >= 0 : "Event index cannot be negative";
        assert participantManager != null : "ParticipantManager cannot be null";

        LOGGER.info("EditEventCommand created for event index: " + index);
    }

    /**
     * Executes the command to edit an event.
     * Provides an interactive menu for the user to select which aspects of the event to edit.
     *
     * @param events The event manager containing the events
     * @param ui The user interface for interaction
     * @param participantManager The participant manager for permissions and availability
     * @throws SyncException if the user is not an admin or there are scheduling conflicts
     * @throws AssertionError if any of the parameters are null
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing EditEventCommand for event index: " + index);

        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            if (!participantManager.isCurrentUserAdmin()) {
                LOGGER.warning("Non-admin user attempted to edit event at index: " + index);
                throw new SyncException("You are not currently administrator.");
            }

            Event event = events.getEvent(index);
            assert event != null : "Event to edit should not be null";
            LOGGER.info("Retrieved event for editing: " + event.getName());

            boolean editing = true;

            while (editing) {
                ui.showEditCommandMessageWithOptions(event);

                Integer choice = ui.readInt();
                if (choice == null) {
                    LOGGER.warning("Invalid edit option format provided");
                    ui.showEditCommandCorrectFormat();
                    continue;
                }

                LOGGER.info("User selected edit option: " + choice);
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
                    LOGGER.info("Event editing completed for: " + event.getName());
                    ui.showMessage("✅ Event editing completed.");
                    break;
                default:
                    LOGGER.warning("Invalid edit option selected: " + choice);
                    ui.showEditCommandCorrectFormat();
                }

                events.save();
                participantManager.save();
                LOGGER.info("Changes saved to storage");
            }
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException during event editing", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during event editing", e);
            throw new SyncException("Error editing event: " + e.getMessage());
        }
    }

    /**
     * Edits the name of the event.
     *
     * @param event The event to modify
     * @param ui The user interface for interaction
     * @return true to continue editing, false to exit editing
     * @throws SyncException if there is an error during editing
     */
    private boolean editName(Event event, UI ui) throws SyncException {
        LOGGER.info("Editing name for event: " + event.getName());
        ui.showEditCommandStep1();
        String newName = ui.readLine().trim();
        ui.checkForExit(newName);
        event.setName(newName);
        LOGGER.info("Name updated to: " + newName);
        ui.showMessage("✅ Name updated:");
        return true;
    }

    /**
     * Edits the start time of the event, checking for participant availability.
     *
     * @param event The event to modify
     * @param ui The user interface for interaction
     * @return true to continue editing, false to exit editing
     * @throws SyncException if there is a scheduling conflict or validation error
     */
    private boolean editStartTime(Event event, UI ui) throws SyncException {
        LOGGER.info("Editing start time for event: " + event.getName());
        while (true) {
            LocalDateTime newStart = getValidDateTime(ui, "start");
            if (newStart == null) {
                LOGGER.info("Start time editing cancelled");
                ui.showMessage("❌ Start time editing cancelled.");
                return true; // return to main edit menu
            }

            if (newStart.isAfter(event.getEndTime())) {
                LOGGER.warning("Invalid start time: after end time");
                ui.showMessage(SyncException.startTimeAfterEndTimeMessage());
                continue;
            }

            LOGGER.info("Unassigning current event time for all participants");
            for (Participant p : event.getParticipants()) {
                p.unassignEventTime(event.getStartTime(), event.getEndTime());
            }

            LOGGER.info("Checking participant availability for new start time");
            boolean conflict = false;
            for (Participant p : event.getParticipants()) {
                if (!p.isAvailableDuring(newStart, event.getEndTime())) {
                    LOGGER.warning("Participant " + p.getName() + " unavailable during proposed time");
                    conflict = true;

                    for (Participant recover : event.getParticipants()) {
                        recover.assignEventTime(event.getStartTime(), event.getEndTime());
                        participantManager.save(recover);
                    }
                    throw new SyncException(SyncException.participantUnavailableDuringEditError(
                            p.getName(), event.getStartTime(), event.getEndTime()));
                }
            }

            if (!conflict) {
                LOGGER.info("Assigning new event time for all participants");
                for (Participant p : event.getParticipants()) {
                    p.assignEventTime(newStart, event.getEndTime());
                    participantManager.save(p);
                }

                LocalDateTime oldStartTime = event.getStartTime();
                event.setStartTime(newStart);
                LOGGER.info("Start time updated from " + oldStartTime + " to " + newStart);
                return true;
            }
        }
    }

    /**
     * Edits the end time of the event, checking for participant availability.
     *
     * @param event The event to modify
     * @param ui The user interface for interaction
     * @return true to continue editing, false to exit editing
     * @throws SyncException if there is a scheduling conflict or validation error
     */
    private boolean editEndTime(Event event, UI ui) throws SyncException{
        LOGGER.info("Editing end time for event: " + event.getName());
        while (true) {
            ui.showEditCommandStep3();
            LocalDateTime newEnd = getValidDateTime(ui, "end");
            if (newEnd == null) {
                LOGGER.info("End time editing cancelled");
                ui.showMessage("❌ End time editing cancelled.");
                return true;
            }

            if (newEnd.isBefore(event.getStartTime())) {
                LOGGER.warning("Invalid end time: before start time");
                ui.showMessage(SyncException.endTimeBeforeStartTimeMessage());
                continue;
            }

            LOGGER.info("Unassigning current event time for all participants");
            for (Participant p : event.getParticipants()) {
                p.unassignEventTime(event.getStartTime(), event.getEndTime());
            }

            LOGGER.info("Checking participant availability for new end time");
            boolean conflict = false;
            for (Participant p : event.getParticipants()) {
                if (!p.isAvailableDuring(event.getStartTime(), newEnd)) {
                    LOGGER.warning("Participant " + p.getName() + " unavailable during proposed time");
                    conflict = true;

                    for (Participant recover : event.getParticipants()) {
                        recover.assignEventTime(event.getStartTime(), event.getEndTime());
                        participantManager.save();
                    }
                    throw new SyncException(SyncException.participantUnavailableDuringEditError(
                            p.getName(), event.getStartTime(), event.getEndTime()));
                }
            }

            if (!conflict) {
                LOGGER.info("Assigning new event time for all participants");
                for (Participant p : event.getParticipants()) {
                    p.assignEventTime(event.getStartTime(), newEnd);
                    participantManager.save();
                }

                LocalDateTime oldEndTime = event.getEndTime();
                event.setEndTime(newEnd);
                LOGGER.info("End time updated from " + oldEndTime + " to " + newEnd);
                return true;
            }
        }
    }

    /**
     * Edits the location of the event.
     *
     * @param event The event to modify
     * @param ui The user interface for interaction
     * @return true to continue editing, false to exit editing
     * @throws SyncException if there is an error during editing
     */
    private boolean editLocation(Event event, UI ui) throws SyncException {
        LOGGER.info("Editing location for event: " + event.getName());
        ui.showEditCommandStep4();
        String newLocation = ui.readLine().trim();
        ui.checkForExit(newLocation);

        String oldLocation = event.getLocation();
        event.setLocation(newLocation);
        LOGGER.info("Location updated from '" + oldLocation + "' to '" + newLocation + "'");
        ui.showMessage("✅ Location updated:");
        return true;
    }

    /**
     * Edits the description of the event.
     *
     * @param event The event to modify
     * @param ui The user interface for interaction
     * @return true to continue editing, false to exit editing
     * @throws SyncException if there is an error during editing
     */
    private boolean editDescription(Event event, UI ui) throws SyncException {
        LOGGER.info("Editing description for event: " + event.getName());
        ui.showEditCommandStep5();
        String newDesc = ui.readLine().trim();
        ui.checkForExit(newDesc);

        String oldDesc = event.getDescription();
        event.setDescription(newDesc);
        LOGGER.info("Description updated from '" + oldDesc + "' to '" + newDesc + "'");
        ui.showMessage("✅ Description updated:");
        return true;
    }

    /**
     * Gets a valid date-time from user input with retry logic.
     *
     * @param ui The user interface for interaction
     * @param type The type of time (start or end)
     * @return The parsed LocalDateTime, or null if canceled
     * @throws SyncException if there is an error during input
     */
    private LocalDateTime getValidDateTime(UI ui, String type) throws SyncException {
        LOGGER.info("Requesting " + type + " time input from user");
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
                LOGGER.info("User cancelled " + type + " time input");
                return null;
            }

            ui.checkForExit(input);

            try {
                LocalDateTime dateTime = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LOGGER.info("Valid " + type + " time entered: " + dateTime);
                return dateTime;
            } catch (Exception e) {
                LOGGER.warning("Invalid date-time format entered: " + input);
                // loop will continue and re-show the re-entry prompt
            }
        }
    }

    /**
     * Checks if all participants are available during the specified time period.
     *
     * @param event The event being edited
     * @param newStart The proposed new start time
     * @param newEnd The proposed new end time
     * @param ui The user interface for displaying availability information
     * @return true if all participants are available, false otherwise
     */
    private boolean checkParticipantAvailability(Event event, LocalDateTime newStart,
                                                 LocalDateTime newEnd, UI ui) {
        LOGGER.info("Checking availability for all participants");
        boolean allAvailable = true;
        for (Participant p : event.getParticipants()) {
            if (!p.isAvailableDuring(newStart, newEnd)) {
                LOGGER.warning("Participant " + p.getName() + " unavailable during proposed time");
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
        LOGGER.info("Availability check result: " + (allAvailable ? "All available" : "Conflicts found"));
        return allAvailable;
    }
}
