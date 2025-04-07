package commandfactory;

import command.ByeCommand;
import command.Command;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;
import java.util.logging.Logger;

/**
 * Factory class for creating ByeCommand objects.
 * This factory handles the creation of commands that terminate the application.
 */
public class ByeCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(ByeCommandFactory.class.getName());
    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructs a new ByeCommandFactory with the specified dependencies.
     *
     * @param participantManager the manager handling participant data
     * @param ui the user interface for interaction
     */
    public ByeCommandFactory(ParticipantManager participantManager, UI ui) {
        assert participantManager != null : "ParticipantManager should not be null";
        assert ui != null : "UI should not be null";

        this.participantManager = participantManager;
        this.ui = ui;
        LOGGER.info("ByeCommandFactory initialized");
    }

    /**
     * Creates a ByeCommand which will terminate the application.
     *
     * @return a new ByeCommand instance
     * @throws SyncException if there are issues creating the command
     */
    @Override
    public Command createCommand() throws SyncException {
        LOGGER.info("Creating ByeCommand");
        return new ByeCommand();
    }
}
