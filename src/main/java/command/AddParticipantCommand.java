package command;

import commandfactory.CommandFactory;
import commandfactory.CreateUserCommandFactory;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import ui.UI;
import label.Priority;

public class AddParticipantCommand extends Command {
    private final int eventIndex;
    private final String participantName;
    private final UI ui;
    private final ParticipantManager participantManager;

    public AddParticipantCommand(int eventIndex, String participantName, UI ui, ParticipantManager participantManager) {
        assert eventIndex >= 0 : "Event index cannot be negative";
        assert participantName != null && !participantName.isBlank() : "Participant name cannot be null or blank";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        this.eventIndex = eventIndex;
        this.participantName = participantName;
        this.ui = ui;
        this.participantManager = participantManager;
    }

    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        assert eventManager != null : "EventManager should not be null";
        assert ui != null : "UI should not be null";
        assert participantManager != null : "ParticipantManager should not be null";

        Event event = eventManager.getEvent(eventIndex);
        assert event != null : "Retrieved event should not be null";

        ui.showMessage("Event Index: " + (eventIndex + 1));
        ui.showMessage("Event Start Time: " + event.getStartTime());
        ui.showMessage("Event End Time: " + event.getEndTime());

        Participant participant = participantManager.getParticipant(participantName);

        if (participant == null) {
            boolean shouldCreate = ui.askConfirmation(
                    "Participant '" + participantName + "' does not exist. Create a new one? (Y/N)"
            );

            if (!shouldCreate) {
                ui.showMessage("Operation cancelled.");
                return;
            }

            CommandFactory factory = new CreateUserCommandFactory(this.ui, this.participantManager);
            Command cmd = factory.createCommand();
            cmd.execute(eventManager, ui, participantManager);

            // RELOAD the participant object from the manager
            participant = participantManager.getParticipant(participantName);
            assert participant != null : "Participant should exist after successful creation";
        }

        boolean isAvailable = participantManager.checkParticipantAvailability(event, participant);
        if (isAvailable) {
            boolean assigned = participantManager.assignParticipant(event, participant);
            if (assigned) {
                event.addParticipant(participant);
                ui.showMessage("✅ Participant " + participant.getName() + " has been added.");
                participantManager.save(); // Ensure participant availability updates are saved
            } else {
                ui.showMessage("❌ Failed to assign time slot. Enter 'addparticipant' to try again.");
            }
        } else {
            ui.showMessage("❌ Participant " + participant.getName() +
                    " is unavailable during the event. Enter 'addparticipant' to try again or try other features.");
        }

        // Persist updated event list to storage
        eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());
    }

    public String getParticipantName() {
        return participantName;
    }

    public int getEventIndex() {
        return eventIndex;
    }
}
