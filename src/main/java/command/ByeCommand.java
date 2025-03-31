package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

public class ByeCommand extends Command {

    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        ui.showByeMessage();
    }

    public boolean isExit() {
        return true;
    }
}
