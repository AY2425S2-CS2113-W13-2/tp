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

public class ListParticipantsCommandFactory implements CommandFactory {
    private final UI ui;
    private final EventManager eventManager;
    private final ParticipantManager participantManager;

    public ListParticipantsCommandFactory(UI ui, EventManager eventManager, ParticipantManager participantManager) {
        this.ui = ui;
        this.eventManager = eventManager;
        this.participantManager = participantManager;
    }

    public Command createCommand() throws SyncException {
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


    private void showAllEvents(ArrayList<Event> events) {
        ui.showMessage("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            ui.showMessage((i + 1) + ". " + events.get(i).getName());
        }
    }

}
