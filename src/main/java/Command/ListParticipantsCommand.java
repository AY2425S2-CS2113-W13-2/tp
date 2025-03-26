package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import ui.UI;

public class ListParticipantsCommand extends Command {
    private final int eventIndex;

    public ListParticipantsCommand(int eventIndex) {
        this.eventIndex = eventIndex;
    }

    @Override
    public void execute(EventManager eventManager, UI ui) throws SyncException {
        Event event = eventManager.getEvent(eventIndex);
        event.listParticipants();  // Safely prints the participants
    }
}
