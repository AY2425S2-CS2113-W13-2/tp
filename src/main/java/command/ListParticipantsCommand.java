package command;

import java.util.logging.Logger;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Command to list the participants of a specific event identified by the event index.
 */
public class ListParticipantsCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ListParticipantsCommand.class.getName());
    private final int eventIndex;

    /**
     * Constructs a ListParticipantsCommand with the specified event index.
     *
     * @param eventIndex the index of the event for which participants will be listed
     */
    public ListParticipantsCommand(int eventIndex) {
        this.eventIndex = eventIndex;
    }

    /**
     * Executes the command to list the participants of the event identified by the event index.
     *
     * @param eventManager the EventManager instance that manages the events
     * @param ui the UI instance used to display messages to the user
     * @param participantManager the ParticipantManager instance that manages the participants
     * @throws SyncException if there is an issue accessing the event or listing participants
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        assert eventIndex >= 0 : "Event index must be non-negative";
        LOGGER.info("Listing participants for event at index: " + eventIndex);
        Event event = eventManager.getEvent(eventIndex);
        event.listParticipants();
    }
}
