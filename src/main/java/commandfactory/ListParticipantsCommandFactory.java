package commandfactory;

import java.util.ArrayList;

import command.Command;
import command.ListParticipantsCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
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
