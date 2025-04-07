package command;

import event.Event;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Represents a command to add an event.
 * An {@code AddEventCommand} object encapsulates a single {@code Event}
 * to be added to the system.
 */
public class AddEventCommand extends Command {
    private final Event event;

    /**
     * Constructs an {@code AddEventCommand} with the specified event.
     *
     * @param event the event to be added.
     */
    public AddEventCommand(Event event) {
        this.event = event;
    }

    /**
     * Adds the event to the event manager under the current user.
     *
     * @param events the event manager to add the event to.
     * @param ui the user interface used for interaction.
     * @param participantManager the participant manager for accessing the current user.
     * @throws SyncException if the current user is not set or another sync error occurs.
     */
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        events.addEvent(event, participantManager.getCurrentUser());
    }

    /**
     * Returns the event associated with this command.
     *
     * @return the event to be added.
     */
    public Event getEvent() {
        return this.event;
    }
}
