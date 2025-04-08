package commandfactory;

import java.util.ArrayList;
import java.util.logging.Logger;

import command.Command;
import command.ListParticipantsCommand;
import command.LoginCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class responsible for creating a ListParticipantsCommand.
 * This factory generates a command that lists participants of a specific event.
 */
public class ListParticipantsCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());

    private final UI ui;
    private final EventManager eventManager;
    private final ParticipantManager participantManager;

    /**
     * Constructs a ListParticipantsCommandFactory with the given UI, event manager, and participant manager.
     *
     * @param ui The UI for interacting with the user
     * @param eventManager The event manager to fetch events
     * @param participantManager The participant manager to verify the current user
     */
    public ListParticipantsCommandFactory(UI ui, EventManager eventManager, ParticipantManager participantManager) {
        this.ui = ui;
        this.eventManager = eventManager;
        this.participantManager = participantManager;
    }

    /**
     * Creates a ListParticipantsCommand that lists participants for a selected event.
     *
     * @return A ListParticipantsCommand for the selected event
     * @throws SyncException If the user is not logged in or the event index is invalid
     */
    public Command createCommand() throws SyncException {
        assert participantManager != null : "ParticipantManager cannot be null";
        LOGGER.info("Attempting ListParticipantsCommandFactory");
        Participant participant = participantManager.getCurrentUser();

        if (participant == null) {
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        ArrayList<Event> events = eventManager.getEventsByParticipant(participantManager);
        if (events.isEmpty()) {
            throw new SyncException("No events available.");
        }

        showAllEvents(events);
        ui.showMessage("Enter event index to list participants (or type 'exit' to cancel):");
        String input = ui.readLine().trim();

        ui.checkForExit(input);
        try {
            int eventIndex = Integer.parseInt(input) - 1;
            if (eventIndex < 0 || eventIndex >= events.size()) {
                ui.showMessage("Invalid index. Please try again.");
            }

            // Show participants of the selected event
            Command listCommand = new ListParticipantsCommand(eventIndex);
            return new ListParticipantsCommand(eventIndex);

        } catch (NumberFormatException e) {
            ui.showMessage("Invalid input. Please enter a valid number or 'exit'.");
        }
        return null;
    }

    /**
     * Displays all available events for the logged-in user.
     */
    private void showAllEvents(ArrayList<Event> events) {
        ui.showMessage("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            ui.showMessage((i + 1) + ". " + events.get(i).getName());
        }
    }

}
