package command;

import java.util.logging.Logger;

import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;

/**
 * Represents a command to create a new user in the system.
 */
public class CreateUserCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(CreateUserCommand.class.getName());

    private final Participant participant;

    /**
     * Constructs a {@code CreateUserCommand} with the specified participant.
     *
     * @param participant the participant to be created.
     */
    public CreateUserCommand(Participant participant) {
        this.participant = participant;
    }

    /**
     * Executes the command to add the new participant to the participant manager.
     *
     * @param events the event manager (unused in this command).
     * @param ui the user interface used to display messages.
     * @param participantManager the participant manager to which the new user is added.
     * @throws SyncException if there is an error during user creation.
     */
    public void execute(EventManager events, ui.UI ui, ParticipantManager participantManager) throws SyncException {
        assert participant != null : "Participant cannot be null";
        LOGGER.info("Attempting to create CreateUserCommand");
        participantManager.addNewUser(participant);
        ui.showSuccessCreateMessage(participant);
    }

    /**
     * Returns the participant associated with this command.
     *
     * @return the participant to be created.
     */
    public Participant getParticipant() {
        return participant;
    }
}
