package commandfactory;

import java.util.ArrayList;

import command.Command;
import command.ListParticipantsCommand;
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
        Participant participant = participantManager.getCurrentUser();

        if (participant == null) {
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        showAllEvents();
        ui.showMessage("Enter event index to list participants:");
        String input = ui.readLine();
        try {
            int eventIndex = Integer.parseInt(input.trim()) - 1;
            return new ListParticipantsCommand(eventIndex);
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid event index. Please enter a number.");
        }
    }

    /**
     * Displays all available events for the logged-in user.
     */
    private void showAllEvents() {
        ArrayList<Event> events = eventManager.getEventsByParticipant(participantManager);
        if (events.isEmpty()) {
            ui.showMessage("No events available.");
            return;
        }

        ui.showMessage("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            ui.showMessage((i + 1) + ". " + events.get(i).getName());
        }
    }
}
