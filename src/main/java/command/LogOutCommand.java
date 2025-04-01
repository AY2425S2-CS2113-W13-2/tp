package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

public class LogOutCommand extends Command {

    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        participants.logout();
        ui.showLogOutMessage();
    }
}