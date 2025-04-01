package commandFactory;

import command.Command;
import command.ListAllCommand;
import exception.SyncException;
import parser.CommandParser;
import participant.ParticipantManager;

public class ListAllCommandFactory implements CommandFactory{
    private final ParticipantManager participantManager;
    private final ui.UI ui;

    public ListAllCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    @Override
    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can list all events!");
        } else {
            String sortType = CommandParser.readListCommandInput();
            return new ListAllCommand(sortType);
        }
    }
}
