package commandfactory;

import command.ByeCommand;
import command.Command;
import exception.SyncException;
import participant.ParticipantManager;

public class ByeCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final ui.UI ui;

    public ByeCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    @Override
    public Command createCommand() throws SyncException {
        return new ByeCommand();
    }
}
