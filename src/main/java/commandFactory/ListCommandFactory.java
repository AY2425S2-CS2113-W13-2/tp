package commandFactory;

import command.Command;
import command.ListCommand;
import exception.SyncException;
import parser.CommandParser;
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
        String sortType = CommandParser.readListCommandInput();
        return new ListCommand(sortType);
    }
}
