package commandfactory;

import command.ByeCommand;
import command.Command;
import exception.SyncException;
import participant.ParticipantManager;

/**
 * Factory class for creating ByeCommand instances.
 * This class is responsible for creating a ByeCommand when invoked.
 */
public class ByeCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final ui.UI ui;

    /**
     * Constructs a ByeCommandFactory with the specified ParticipantManager and UI.
     *
     * @param participantManager the ParticipantManager instance used to manage participants
     * @param ui the UI instance used to interact with the user
     */
    public ByeCommandFactory(ParticipantManager participantManager, ui.UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates and returns a ByeCommand that can be executed to exit the system.
     *
     * @return a ByeCommand that ends the session or application
     * @throws SyncException if there is an error during command creation (although not expected in this case)
     */
    @Override
    public Command createCommand() throws SyncException {
        return new ByeCommand();
    }
}
