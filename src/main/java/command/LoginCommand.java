package command;

import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

public class LoginCommand extends Command {
    public LoginCommand() {

    }
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        participantManager.login();
    }
}
