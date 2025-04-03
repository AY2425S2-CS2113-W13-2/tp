package commandfactory;

import command.AddParticipantCommand;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import java.util.ArrayList;
import parser.CommandParser;
import ui.UI;

public class AddParticipantCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final UI ui;
    private final EventManager eventManager;

    public AddParticipantCommandFactory(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    public AddParticipantCommand createCommand() throws SyncException {
        checkAdminPrivileges();
        showAllEvents();
        showAllParticipants();
        String[] input = CommandParser.splitAddParticipantCommandInput();

        return new AddParticipantCommand(
                Integer.parseInt(input[0].trim()) - 1,
                input[1].trim()
        );
    }

    private void showAllParticipants() {
        ArrayList<Participant> participants = participantManager.getParticipants();
        if (participants.isEmpty()) {
            ui.showMessage("No participants available.");
            return;
        }

        ui.showMessage("Available Participants:");
        for (Participant participant : participants) {
            ui.showMessage("- " + participant.getName());
        }
    }

    private void checkAdminPrivileges() throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only ADMIN users can add participants.");
        }
    }

    private void showAllEvents() {
        ArrayList<Event> events = eventManager.getEvents();
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
