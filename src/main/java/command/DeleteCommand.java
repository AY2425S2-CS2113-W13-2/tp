package command;

import event.Event;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to delete an existing event from the system.
 * This command handles the removal of events after confirming with the user.
 */
public class DeleteCommand extends Command {
    private final int index;
    private static final Logger LOGGER = Logger.getLogger(DeleteCommand.class.getName());

    /**
     * Constructs a new DeleteCommand with the specified event index.
     *
     * @param index The index of the event to be deleted
     * @throws AssertionError if the index is negative
     */
    public DeleteCommand(int index) {
        this.index = index;
        LOGGER.info("DeleteCommand created for event index: " + index);
    }

    /**
     * Executes the command to delete an event from the event manager.
     * Validates the index, confirms with the user, and performs the deletion if confirmed.
     *
     * @param eventManager The event manager containing the events
     * @param ui The user interface for displaying messages and confirming deletion
     * @param participantManager The participant manager
     * @throws SyncException if there is an invalid event index or synchronization error
     * @throws AssertionError if any of the parameters are null
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing DeleteCommand for event index: " + index);
        assert eventManager != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            if (eventManager.getEvents().isEmpty()) {
                LOGGER.warning("Attempted to delete from an empty event list");
                ui.showMessage("No events available.");
                throw new SyncException("No events available.");
            }

            if (index < 0 || index >= eventManager.getEvents().size()) {
                LOGGER.warning("Invalid event index: " + index + ", valid range is 0-" +
                        (eventManager.getEvents().size() - 1));
                throw new SyncException("Invalid event index. Please enter a valid index.");
            }

            Event eventToDelete = eventManager.getEvents().get(index);
            LOGGER.info("Found event to delete: " + eventToDelete.getName());

            if (ui.confirmDeletion(eventToDelete.getName())) {
                eventManager.deleteEvent(index);
                LOGGER.info("Event successfully deleted: " + eventToDelete.getName());
            } else {
                LOGGER.info("User cancelled deletion of event: " + eventToDelete.getName());
                ui.showDeletionCancelledMessage();
            }
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException occurred during event deletion", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during event deletion", e);
            throw new SyncException("An error occurred while deleting the event: " + e.getMessage());
        }
    }

    /**
     * Returns the index of the event to be deleted.
     *
     * @return The event index
     */
    public int getEventIndex() {
        return this.index;
    }
}
