package commandfactory;

import command.Command;
import command.ListAllCommand;
import exception.SyncException;
import ui.UI;
import participant.ParticipantManager;

public class ListAllCommandFactory implements CommandFactory{
    private final ParticipantManager participantManager;
    private final UI ui;

    public ListAllCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    @Override
    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Sorry, you need to be an ADMIN to access all events.");
        } else {
            String sort = ui.readListCommandInput();

            assert sort != null : "Input sort type should not be null";
            assert !sort.trim().isEmpty() : "Input string should not be empty";

            return new ListAllCommand(sort, ui);
        }
    }
}
