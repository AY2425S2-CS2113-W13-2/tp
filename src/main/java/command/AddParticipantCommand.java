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
        this.eventIndex = eventIndex;
        this.participantName = participantName;
        this.ui = ui;
        this.participantManager = participantManager;
    }

    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        Event event = eventManager.getEvent(eventIndex);
        ui.showMessage("Event Index: " + eventIndex);
        ui.showMessage("Event Start Time: " + event.getStartTime());
        ui.showMessage("Event End Time: " + event.getEndTime());

        Participant participant = participantManager.getCurrentUser();

        boolean isAvailable = participantManager.checkParticipantAvailability(event, participant);
        if (isAvailable) {
            boolean assigned = participantManager.assignParticipant(event, participant);
            if (assigned) {
                event.addParticipant(participant);
                ui.showMessage("Participant " + participant.getName() + " has been added.");
            } else {
                ui.showMessage("Failed to assign time slot. Enter 'addparticipant' to try again.");
            }
        } else {
            ui.showMessage("Participant " + participant.getName() + " is unavailable during the event." +
                    "Enter 'addparticipant' to try again or try other features.");
        }


        // Persist updated event list to storage
        eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());
    }
}
