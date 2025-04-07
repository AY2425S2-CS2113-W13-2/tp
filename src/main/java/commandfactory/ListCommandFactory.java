package commandfactory;

import command.Command;
import command.ListCommand;
import exception.SyncException;
import participant.ParticipantManager;

public class ListCommandFactory implements CommandFactory{
    private final ParticipantManager participantManager;
    private final ui.UI ui;

    public ListCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    @Override
    public Command createCommand() throws SyncException {
        String sortType = ui.readListCommandInput();
        if (sortType.equals("")) {
            throw new SyncException("List sort type is empty. Please enter 'list' and try again.");
        }
        return new ListCommand(sortType);
    }
}
