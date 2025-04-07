package command;

import event.EventManager;
import event.Event;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Represents a command to duplicate an existing event with a new name.
 */
public class DuplicateCommand extends Command {
    private final Event eventToDuplicate;
    private final String newName;

    /**
     * Constructs a {@code DuplicateCommand} with the event to duplicate and the new event name.
     *
     * @param eventToDuplicate the event to be duplicated.
     * @param newName the name of the duplicated event.
     */
    public DuplicateCommand(Event eventToDuplicate, String newName) {
        this.eventToDuplicate = eventToDuplicate;
        this.newName = newName;
    }

    /**
     * Executes the command to duplicate the specified event and displays a confirmation message.
     *
     * @param events the event manager used to manage events.
     * @param ui the user interface used to display the result.
     * @param participantManager the participant manager (not used in this command).
     * @throws SyncException if an error occurs during the duplication process.
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        events.duplicateEvent(eventToDuplicate, newName);
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        ui.showMessage("Event duplicated: " + duplicatedEvent.toString());
    }
}
