package command;

import event.*;
import ui.UI;
import exception.*;

public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    public void execute(EventManager events, UI ui) throws SyncException {
        events.deleteEvent(index);
    }
}
