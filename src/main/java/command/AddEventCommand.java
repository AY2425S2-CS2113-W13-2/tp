package command;

import event.Event;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to add a new event to the event manager.
 * This command is used to create and store new events in the system.
 */
public class AddEventCommand extends Command {
    private final Event event;
    private static final Logger LOGGER = Logger.getLogger(AddEventCommand.class.getName());

    /**
     * Constructs a new AddEventCommand with the specified event.
     *
     * @param event The event to be added to the system
     * @throws AssertionError if the event is null
     */
    public AddEventCommand(Event event) {
        this.event = event;
        assert event != null : "Event cannot be null";
        LOGGER.info("AddEventCommand created with event: " + event);
    }

    /**
     * Executes the command to add the event to the event manager.
     *
     * @param events The event manager that will store the event
     * @param ui The user interface to display results
     * @param participantManager The participant manager to verify participant information
     * @throws SyncException if there is a synchronization error when adding the event
     * @throws AssertionError if any of the parameters are null
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing AddEventCommand");
        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            events.addEvent(event, participantManager);
            LOGGER.info("Event successfully added: " + event);
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "Failed to add event: " + event, e);
            throw e;
        }
    }

    /**
     * Returns the event associated with this command.
     *
     * @return The event to be added
     */
    public Event getEvent() {
        return this.event;
    }
}
