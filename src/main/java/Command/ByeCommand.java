package command;

import event.EventManager;
import ui.UI;
import exception.SyncException;

public class ByeCommand extends Command {
    public void execute(EventManager events, UI ui) throws SyncException {
        ui.showByeMessage();
    }

    public boolean isExit() {
        return true;
    }
}
