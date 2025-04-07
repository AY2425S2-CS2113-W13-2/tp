package commandfactory;

import command.Command;
import command.EditEventCommand;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class responsible for creating an EditEventCommand.
 * This factory ensures that the user is logged in, has admin privileges,
 * and can edit an event by providing a valid event index.
 */
public class EditCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final EventManager eventManager;
    private final UI ui;

    /**
     * Constructor to initialize the factory with participant manager, event manager, and UI.
     *
     * @param participantManager The participant manager to handle participant data
     * @param eventManager The event manager to handle event data
     * @param ui The UI used to interact with the user
     */
    public EditCommandFactory(ParticipantManager participantManager, EventManager eventManager, UI ui) {
        this.participantManager = participantManager;
        this.eventManager = eventManager;
        this.ui = ui;
    }

    /**
     * Creates an EditEventCommand based on the user's input.
     * This method checks if the user is logged in and has admin privileges,
     * and then allows the user to edit an event by providing a valid event index.
     *
     * @return A new EditEventCommand to edit the selected event
     * @throws SyncException If an error occurs during command creation, such as invalid input or lack of privileges
     */
    @Override
    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        }

        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can edit events!");
        }

        // Show event list before prompting for index
        ui.showMessage("\n📅 Here are the available events to edit:\n");
        eventManager.viewAllEvents();

        int index = readEditEventIndex();
        return new EditEventCommand(index, this.participantManager);
    }

    /**
     * Prompts the user to enter the index of the event they wish to edit.
     *
     * @return The index of the event to edit
     * @throws SyncException If an invalid event index is provided
     */
    private int readEditEventIndex() throws SyncException {
        ui.showMessage("\nEnter event index to edit: ");
        try {
            int index = Integer.parseInt(ui.readLine().trim()) - 1;
            return index;
        } catch (Exception e) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
    }
}
