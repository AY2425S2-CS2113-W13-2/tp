package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to list all participants of a specific event.
 * This command displays all participants assigned to the event at the given index.
 */
public class ListParticipantsCommand extends Command {
    private final int eventIndex;
    private static final Logger LOGGER = Logger.getLogger(ListParticipantsCommand.class.getName());

    /**
     * Constructs a new ListParticipantsCommand with the specified event index.
     *
     * @param eventIndex The index of the event whose participants will be listed
     * @throws AssertionError if the eventIndex is negative
     */
    public ListParticipantsCommand(int eventIndex) {
        this.eventIndex = eventIndex;

        assert eventIndex >= 0 : "Event index cannot be negative";

        LOGGER.info("ListParticipantsCommand created for event index: " + eventIndex);
    }

    /**
     * Executes the command to list all participants of the specified event.
     * Retrieves the event at the given index and displays its participants.
     *
     * @param eventManager The event manager containing the events
     * @param ui The user interface for displaying messages
     * @param participantManager The participant manager (not used in this command)
     * @throws SyncException if the event index is invalid or other errors occur
     * @throws AssertionError if eventManager is null
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing ListParticipantsCommand for event index: " + eventIndex);

        assert eventManager != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";

        try {
            if (eventIndex >= eventManager.size()) {
                LOGGER.warning("Invalid event index: " + eventIndex + ", maximum index is " + (eventManager.size() - 1));
                throw new SyncException("Invalid event index. Please enter a valid index.");
            }

            Event event = eventManager.getEvent(eventIndex);

            if (event == null) {
                LOGGER.severe("Event at index " + eventIndex + " is null");
                throw new SyncException("Event not found at index: " + eventIndex);
            }

            LOGGER.info("Listing participants for event: " + event.getName());
            event.listParticipants();  // Safely prints the participants
            LOGGER.info("Participants listed successfully");
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException occurred while listing participants", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while listing participants", e);
            throw new SyncException("Error listing participants: " + e.getMessage());
        }
    }
}
