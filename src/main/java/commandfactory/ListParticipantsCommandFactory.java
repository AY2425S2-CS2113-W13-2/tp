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

        ArrayList<Event> events = eventManager.getEventsByParticipant(participantManager);
        if (events.isEmpty()) {
            throw new SyncException("No events available.");
        }

        while (true) {
            showAllEvents(events);
            ui.showMessage("Enter event index to list participants (or type 'exit' to cancel):");
            String input = ui.readLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                throw new SyncException("Exited list participants menu.");
            }

            try {
                int eventIndex = Integer.parseInt(input) - 1;
                if (eventIndex < 0 || eventIndex >= events.size()) {
                    ui.showMessage("Invalid index. Please try again.");
                    continue;
                }

                // Show participants of the selected event
                Command listCommand = new ListParticipantsCommand(eventIndex);
                listCommand.execute(eventManager, ui, participantManager);


            } catch (NumberFormatException e) {
                ui.showMessage("Invalid input. Please enter a valid number or 'exit'.");
            } catch (SyncException e) {
                ui.showMessage("Error: " + e.getMessage());
            }

            ui.showMessage("--------------------------------------------------");
        }
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
