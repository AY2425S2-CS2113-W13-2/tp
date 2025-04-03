package command;

import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;

public class CreateUserCommand extends Command {
    private final Participant participant;

    public CreateUserCommand(Participant participant) {
        this.participant = participant;
    }

    public void execute(EventManager events, ui.UI ui, ParticipantManager participantManager) throws SyncException {
        participantManager.addNewUser(participant);
        ui.showSuccessCreateMessage(participant);
    }

}
