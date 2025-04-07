package command;

import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Represents an abstract command to be executed in the application.
 * All concrete commands must extend this class and implement the {@code execute} method.
 */
public abstract class Command {

    /**
     * Executes the command using the given managers and UI.
     *
     * @param events the manager handling event data.
     * @param ui the user interface used for displaying messages.
     * @param participantManager the manager handling participant data.
     * @throws SyncException if there is an error during command execution.
     */
    public abstract void execute(EventManager events, UI ui,
                                 ParticipantManager participantManager) throws SyncException;

    /**
     * Indicates whether the command causes the application to terminate.
     *
     * @return {@code true} if the command signals exit, {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
