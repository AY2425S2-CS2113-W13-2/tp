package command;

import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

public class HelpCommand extends Command {
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        ui.showMenu();
    }
}
