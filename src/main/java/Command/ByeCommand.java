package Command;

import Event.EventManager;
import UI.UI;
import Exception.SyncException;

public class ByeCommand extends Command {
    public void execute(EventManager events, UI ui) throws SyncException {
        ui.showByeMessage();
    }

    public boolean isExit() {
        return true;
    }
}