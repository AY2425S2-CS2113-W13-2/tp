package commandfactory;

import command.Command;
import command.LoginCommand;
import exception.SyncException;
import participant.ParticipantManager;

public class LoginCommandFactory implements CommandFactory{
    private final ParticipantManager participantManager;

    public LoginCommandFactory(ParticipantManager participantManager) {
        this.participantManager = participantManager;
    }

    public Command createCommand() throws SyncException {
        if(participantManager.getCurrentUser() == null) {
            return new LoginCommand();
        } else {
            throw new SyncException("You are already logged in. Please enter 'logout' to log out first.");
        }

    }
}
