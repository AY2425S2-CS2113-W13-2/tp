package command;

import event.EventManager;
import ui.UI;
import exception.SyncException;

public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(EventManager eventManager, UI ui) throws SyncException {
        if (eventManager.getEvents().isEmpty()) {
            ui.showMessage("No events available.");
            return;
        }

        if (index < 0 || index >= eventManager.getEvents().size()) {
            throw new SyncException("Invalid event index. Please enter a valid index.");
        }

        String eventName = eventManager.getEvents().get(index).getName();

        if (ui.confirmDeletion(eventName)) {
            eventManager.deleteEvent(index);
            ui.showDeletedMessage(eventManager.getEvents().get(index));
        } else {
            ui.showDeletionCancelledMessage();
        }
    }
}

