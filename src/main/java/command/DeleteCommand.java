package command;

import event.Event;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        if (eventManager.getEvents().isEmpty()) {
            ui.showMessage("No events available.");
            return;
        }

        if (index < 0 || index >= eventManager.getEvents().size()) {
            throw new SyncException("Invalid event index. Please enter a valid index.");
        }

        Event eventToDelete = eventManager.getEvents().get(index);

        if (ui.confirmDeletion(eventToDelete.getName())) {
            eventManager.deleteEvent(index);
        } else {
            ui.showDeletionCancelledMessage();
        }
    }
}
