package command;

import event.EventManager;
import ui.UI;
import exception.SyncException;

public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    public void execute(EventManager events, UI ui) throws SyncException {
        events.deleteEvent(index);
    }
}
