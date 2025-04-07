package command;

import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to create a new participant in the system.
 * This command handles the creation and storage of a new participant.
 */
public class CreateUserCommand extends Command {
    private final Participant participant;
    private static final Logger LOGGER = Logger.getLogger(CreateUserCommand.class.getName());

    /**
     * Constructs a new CreateUserCommand with the specified participant.
     *
     * @param participant The participant to be created in the system
     * @throws AssertionError if the participant is null
     */
    public CreateUserCommand(Participant participant) {
        this.participant = participant;
        assert participant != null : "Participant cannot be null";
        LOGGER.info("CreateUserCommand created for participant: " + participant.getName());
    }

    /**
     * Executes the command to create a new participant.
     * Adds the participant to the participant manager and displays a success message.
     *
     * @param events The event manager
     * @param ui The user interface to display messages
     * @param participantManager The participant manager to store the new participant
     * @throws SyncException if there is a synchronization error when adding the participant
     * @throws AssertionError if any of the parameters are null
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing CreateUserCommand for participant: " + participant.getName());
        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            participantManager.addNewUser(participant);
            LOGGER.info("Successfully added new participant: " + participant.getName());
            ui.showSuccessCreateMessage(participant);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating participant: " + participant.getName(), e);
            throw e;
        }
    }

    /**
     * Returns the participant associated with this command.
     *
     * @return The participant to be created
     */
    public Participant getParticipant() {
        return participant;
    }
}
