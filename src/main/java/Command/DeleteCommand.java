package Command;

import Event.*;
import UI.UI;
import Exception.*;

public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    public void execute(EventManager events, UI ui) throws SyncException {
        events.deleteEvent(index);
    }
}
