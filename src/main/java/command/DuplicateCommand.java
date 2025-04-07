package command;

import event.EventManager;
import event.Event;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to duplicate an existing event.
 * This command creates a copy of an event with a new name while preserving its other attributes.
 */
public class DuplicateCommand extends Command {
    private final Event eventToDuplicate;
    private final String newName;
    private static final Logger LOGGER = Logger.getLogger(DuplicateCommand.class.getName());

    /**
     * Constructs a new DuplicateCommand with the specified event and new name.
     *
     * @param eventToDuplicate The event to be duplicated
     * @param newName The name for the duplicated event
     * @throws AssertionError if the event is null or new name is null or empty
     */
    public DuplicateCommand(Event eventToDuplicate, String newName) {
        this.eventToDuplicate = eventToDuplicate;
        this.newName = newName;

        assert eventToDuplicate != null : "Event to duplicate cannot be null";
        assert newName != null && !newName.isEmpty() : "New event name cannot be null or empty";

        LOGGER.info("DuplicateCommand created for event: " + eventToDuplicate.getName() +
                " with new name: " + newName);
    }

    /**
     * Executes the command to duplicate an event.
     * Creates a copy of the event with a new name and adds it to the event manager.
     *
     * @param events The event manager to store the duplicated event
     * @param ui The user interface to display messages
     * @param participantManager The participant manager
     * @throws SyncException if there is a synchronization error when duplicating the event
     * @throws AssertionError if any of the parameters are null
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing DuplicateCommand for event: " + eventToDuplicate.getName());

        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            events.duplicateEvent(eventToDuplicate, newName);
            Event duplicatedEvent = eventToDuplicate.duplicate(newName);

            LOGGER.info("Successfully duplicated event: " + eventToDuplicate.getName() +
                    " to: " + duplicatedEvent.getName());

            ui.showMessage("Event duplicated: " + duplicatedEvent.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error duplicating event: " + eventToDuplicate.getName(), e);
            throw e;
        }
    }
}
