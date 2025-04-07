package commandfactory;

import command.Command;
import command.LogOutCommand;
import exception.SyncException;
import participant.ParticipantManager;

public class LogOutCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;

    public LogOutCommandFactory(ParticipantManager participantManager) {
        this.participantManager = participantManager;
    }

    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to log in first.");
        } else {
            return new LogOutCommand();
        }
    }
}
