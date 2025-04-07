package commandfactory;

import java.util.logging.Logger;

import command.Command;
import command.DuplicateCommand;
import command.LoginCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class responsible for creating a DuplicateCommand.
 * This factory ensures that the user is logged in, has admin privileges,
 * and can duplicate an event by providing a valid event index and a new name.
 */
public class DuplicateCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    private final ParticipantManager participantManager;
    private final UI ui;
    private final EventManager eventManager;

    /**
     * Constructor to initialize the factory with participant manager, UI, and event manager.
     *
     * @param participantManager The participant manager to handle participant data
     * @param ui The UI used to interact with the user
     * @param eventManager The event manager to handle event data
     */
    public DuplicateCommandFactory(ParticipantManager participantManager, UI ui, EventManager eventManager) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    /**
     * Creates a DuplicateCommand based on the user's input.
     * This method checks if the user is logged in and has admin privileges,
     * and then allows the user to duplicate an event by providing a valid index
     * and a new name for the event.
     *
     * @return A new DuplicateCommand to duplicate the selected event
     * @throws SyncException If an error occurs during the command creation, such as invalid input or lack of privileges
     */
    @Override
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting DuplicateCommandFactory");
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can duplicate events!");
        } else {
            String input = ui.readDuplicateEventInput();
            String[] parts = input.split(" ", 2);

            if (parts.length < 2) {
                throw new SyncException("Invalid duplicate command format. Use: duplicate index New Event Name");
            }

            try {
                int index = Integer.parseInt(parts[0]) - 1;
                if (index >= 0 && index < eventManager.getEvents().size()) {
                    Event eventToDuplicate = eventManager.getEvents().get(index);
                    String newName = parts[1];
                    return new DuplicateCommand(eventToDuplicate, newName);
                } else {
                    throw new SyncException("Invalid event index.");
                }
            } catch (NumberFormatException e) {
                throw new SyncException("Invalid index format. Use a number.");
            }
        }
    }
}
