package command;

import event.Event;
import event.EventManager;
import participant.Participant;
import exception.SyncException;
import ui.UI;
import label.Priority;

public class AddParticipantCommand extends Command {
    private final int eventIndex;
    private final String participantName;
    private final Participant.AccessLevel accessLevel;

    public AddParticipantCommand(int eventIndex, String participantName, Participant.AccessLevel accessLevel) {
        this.eventIndex = eventIndex;
        this.participantName = participantName;
        this.accessLevel = accessLevel;
    }

    @Override
    public void execute(EventManager eventManager, UI ui) throws SyncException {
        Event event = eventManager.getEvent(eventIndex);
        Participant participant = new Participant(participantName, accessLevel);
        event.addParticipant(participant);
        ui.showMessage("Participant added: " + participant);

        // Persist updated event list to storage
        eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());
    }
}
