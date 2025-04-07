package command;

import event.Event;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Represents a command to delete an event from the event manager.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Constructs a {@code DeleteCommand} with the specified event index.
     *
     * @param index the index of the event to be deleted.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command to delete an event if the index is valid and user confirms the deletion.
     *
     * @param eventManager the event manager containing the list of events.
     * @param ui the user interface used to display prompts and messages.
     * @param participantManager the participant manager (not used in this command).
     * @throws SyncException if the event index is invalid.
     */
    @Override
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        if (index < 0 || index >= eventManager.getEvents().size()) {
            throw new SyncException("Invalid event index. Please enter a valid index.");
        }

        if (eventManager.getEvents().isEmpty()) {
            ui.showMessage("No events available.");
            return;
        }

        Event eventToDelete = eventManager.getEvents().get(index);

        if (ui.confirmDeletion(eventToDelete.getName())) {
            eventManager.deleteEvent(index);
        } else {
            ui.showDeletionCancelledMessage();
        }
    }

    /**
     * Returns the index of the event to be deleted.
     *
     * @return the event index.
     */
    public int getEventIndex() {
        return this.index;
    }
}
