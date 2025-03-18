package Command;

import event.EventManager;
import ui.UI;
import exception.SyncException;

public abstract class Command {
    public abstract void execute(EventManager events, UI ui) throws SyncException;

    public boolean isExit() {
        return false;
    }
}
